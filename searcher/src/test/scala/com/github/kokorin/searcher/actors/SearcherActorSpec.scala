package com.github.kokorin.searcher.actors

import java.util.concurrent.Future

import akka.actor.{Actor, ActorSystem, Props}
import com.github.kokorin.searcher.config.SearchEngineConfig
import com.github.kokorin.searcher.model.SearchEngineResponse
import com.github.kokorin.searcher.web.http.{
  AsyncHTTPClient,
  AsyncHTTPClientsProvider
}
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.json4s.{Formats, NoTypeHints}
import org.json4s.native.Serialization
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.MockFactory
import org.json4s.native.Serialization.write

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

class TestActor(request: String,
                provider: AsyncHTTPClientsProvider,
                config: SearchEngineConfig,
                answerPromise: Promise[AggregatorActor.SearcherResponseMessage])
    extends Actor {
  override def receive: Receive = {
    case "start" =>
      val searchActor =
        context.actorOf(Props(classOf[SearcherActor], provider))
      searchActor ! SearcherActor.RequestToSearchEngineMessage(request, config)
    case response @ AggregatorActor.SearcherResponseMessage(_, _) =>
      answerPromise.success(response)
  }
}

class SearcherActorSpec extends AnyFlatSpec with Matchers with MockFactory {
  "SearcherActor" should "return answer, when answer comes from search engine" in {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val system = ActorSystem("test")
    val provider = new AsyncHTTPClientsProvider {
      override def newAsyncClient: AsyncHTTPClient = new AsyncHTTPClient {
        override def start(): Unit = ()

        override def execute(request: HttpUriRequest,
                             callback: FutureCallback[String]): Future[_] = {
          val stringRequest = request.getURI.getQuery.substring(6) // remove query=
          val response = SearchEngineResponse(
            status = SearchEngineResponse.OK,
            urls = Seq(s"Response for $stringRequest")
          )
          callback.completed(write(response))
          mock[Future[Unit]]
        }

        override def close(): Unit = ()
      }
    }
    val request = "request"
    val config = new SearchEngineConfig {
      override val host: String = "some_host"
      override val port: Int = 42
      override val name: String = "some_name"
    }

    val answerPromise: Promise[AggregatorActor.SearcherResponseMessage] =
      Promise()

    system.actorOf(
      Props(classOf[TestActor], request, provider, config, answerPromise)
    ) ! "start"

    val answer = Await.result(answerPromise.future, 5.minutes)
    val expectedAnswer = AggregatorActor.SearcherResponseMessage(
      engineName = "some_name",
      response = SearchEngineResponse(
        status = SearchEngineResponse.OK,
        urls = Seq(s"Response for $request")
      )
    )
    answer shouldBe expectedAnswer
  }

  it should "return empty answer, when request fails" in {
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val system = ActorSystem("test")
    val provider = new AsyncHTTPClientsProvider {
      override def newAsyncClient: AsyncHTTPClient = new AsyncHTTPClient {
        override def start(): Unit = ()

        override def execute(request: HttpUriRequest,
                             callback: FutureCallback[String]): Future[_] = {
          val stringRequest = request.getURI.getQuery.substring(6) // remove query=
          callback.failed(new IllegalArgumentException())
          mock[Future[Unit]]
        }

        override def close(): Unit = ()
      }
    }
    val request = "request"
    val config = new SearchEngineConfig {
      override val host: String = "some_host"
      override val port: Int = 42
      override val name: String = "some_name"
    }

    val answerPromise: Promise[AggregatorActor.SearcherResponseMessage] =
      Promise()

    system.actorOf(
      Props(classOf[TestActor], request, provider, config, answerPromise)
    ) ! "start"

    val answer = Await.result(answerPromise.future, 5.minutes)
    val expectedAnswer = AggregatorActor.SearcherResponseMessage(
      engineName = "some_name",
      response =
        SearchEngineResponse(status = SearchEngineResponse.ERR, urls = Seq())
    )
    answer shouldBe expectedAnswer
  }
}
