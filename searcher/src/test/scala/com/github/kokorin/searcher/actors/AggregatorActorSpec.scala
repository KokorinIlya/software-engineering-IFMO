package com.github.kokorin.searcher.actors

import java.util.concurrent.Future

import akka.actor.{ActorSystem, Props}
import com.github.kokorin.searcher.config.AggregatorActorConfig
import com.github.kokorin.searcher.config.SearchEngineConfig
import com.github.kokorin.searcher.model.{
  AggregatedSearchResponse,
  SearchEngineResponse
}
import com.github.kokorin.searcher.web.http.{
  AsyncHTTPClient,
  AsyncHTTPClientsProvider
}
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.json4s.{Formats, NoTypeHints}
import org.json4s.native.Serialization
import org.scalamock.scalatest.MockFactory
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import org.json4s.native.Serialization.write

import scala.concurrent.{Await, Promise}
import scala.concurrent.duration.FiniteDuration
import scala.concurrent.duration._

class AggregatorActorSpec extends AnyFlatSpec with Matchers with MockFactory {
  "AggregatorActor" should "collect all answers, if all answers come on time" in {
    val answerPromise: Promise[AggregatedSearchResponse] = Promise()
    val actorSystem = ActorSystem("test")
    val actorConfig = new AggregatorActorConfig {
      override val timeout: FiniteDuration = 1.second
    }
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val clientProvider = new AsyncHTTPClientsProvider {
      override def newAsyncClient: AsyncHTTPClient = new AsyncHTTPClient {
        override def start(): Unit = ()

        override def execute(request: HttpUriRequest,
                             callback: FutureCallback[String]): Future[_] = {
          val stringRequest = request.getURI.getQuery.substring(6) // remove query=
          val host = request.getURI.toURL.getHost
          val response = SearchEngineResponse(
            status = SearchEngineResponse.OK,
            urls = Seq(s"Response for $stringRequest from $host")
          )
          callback.completed(write(response))
          mock[Future[Unit]]
        }

        override def close(): Unit = ()
      }
    }
    val actor = actorSystem.actorOf(
      Props(
        classOf[AggregatorActor],
        answerPromise,
        actorConfig,
        clientProvider
      )
    )
    val enginesConfig = Seq(new SearchEngineConfig {
      override val host: String = "yandex_host"
      override val port: Int = 42
      override val name: String = "yandex"
    }, new SearchEngineConfig {
      override val host: String = "google_host"
      override val port: Int = 42
      override val name: String = "google"
    }, new SearchEngineConfig {
      override val host: String = "bing_host"
      override val port: Int = 42
      override val name: String = "bing"
    })
    val request = "request"
    actor ! AggregatorActor.SearchQueryMessage(request, enginesConfig)
    val response = Await.result(answerPromise.future, 3.seconds)
    val expectedResponse = AggregatedSearchResponse(
      Map(
        "google" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from google_host")
        ),
        "bing" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from bing_host")
        ),
        "yandex" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from yandex_host")
        )
      )
    )
    response shouldBe expectedResponse
  }

  it should "collect all answers, except failed" in {
    val answerPromise: Promise[AggregatedSearchResponse] = Promise()
    val actorSystem = ActorSystem("test")
    val actorConfig = new AggregatorActorConfig {
      override val timeout: FiniteDuration = 1.second
    }
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val clientProvider = new AsyncHTTPClientsProvider {
      override def newAsyncClient: AsyncHTTPClient = new AsyncHTTPClient {
        override def start(): Unit = ()

        override def execute(request: HttpUriRequest,
                             callback: FutureCallback[String]): Future[_] = {
          val stringRequest = request.getURI.getQuery.substring(6) // remove query=
          val host = request.getURI.toURL.getHost
          if (host == "google_host") {
            callback.failed(new IllegalArgumentException())
          } else {
            val response = SearchEngineResponse(
              status = SearchEngineResponse.OK,
              urls = Seq(s"Response for $stringRequest from $host")
            )
            callback.completed(write(response))
          }
          mock[Future[Unit]]
        }

        override def close(): Unit = ()
      }
    }
    val actor = actorSystem.actorOf(
      Props(
        classOf[AggregatorActor],
        answerPromise,
        actorConfig,
        clientProvider
      )
    )
    val enginesConfig = Seq(new SearchEngineConfig {
      override val host: String = "yandex_host"
      override val port: Int = 42
      override val name: String = "yandex"
    }, new SearchEngineConfig {
      override val host: String = "google_host"
      override val port: Int = 42
      override val name: String = "google"
    }, new SearchEngineConfig {
      override val host: String = "bing_host"
      override val port: Int = 42
      override val name: String = "bing"
    })
    val request = "request"
    actor ! AggregatorActor.SearchQueryMessage(request, enginesConfig)
    val response = Await.result(answerPromise.future, 3.seconds)
    val expectedResponse = AggregatedSearchResponse(
      Map(
        "google" -> SearchEngineResponse(SearchEngineResponse.ERR, List()),
        "bing" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from bing_host")
        ),
        "yandex" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from yandex_host")
        )
      )
    )
    response shouldBe expectedResponse
  }

  it should "collect all answers, except timeouted" in {
    val answerPromise: Promise[AggregatedSearchResponse] = Promise()
    val actorSystem = ActorSystem("test")
    val actorConfig = new AggregatorActorConfig {
      override val timeout: FiniteDuration = 1.second
    }
    implicit val formats: Formats = Serialization.formats(NoTypeHints)
    val clientProvider = new AsyncHTTPClientsProvider {
      override def newAsyncClient: AsyncHTTPClient = new AsyncHTTPClient {
        override def start(): Unit = ()

        override def execute(request: HttpUriRequest,
                             callback: FutureCallback[String]): Future[_] = {
          val stringRequest = request.getURI.getQuery.substring(6) // remove query=
          val host = request.getURI.toURL.getHost
          if (host == "google_host") {
            new Future[Unit] {
              @volatile
              var cancelled: Boolean = false

              override def cancel(mayInterruptIfRunning: Boolean): Boolean = {
                cancelled = true
                true
              }

              override def isCancelled: Boolean = cancelled

              override def isDone: Boolean = false

              override def get(): Unit =
                throw new IllegalArgumentException(
                  "Cannot get from empty future"
                )

              override def get(timeout: Long, unit: TimeUnit): Unit =
                throw new IllegalArgumentException(
                  "Cannot get from empty future"
                )
            }
          } else {
            val response = SearchEngineResponse(
              status = SearchEngineResponse.OK,
              urls = Seq(s"Response for $stringRequest from $host")
            )
            callback.completed(write(response))
            mock[Future[Unit]]
          }
        }

        override def close(): Unit = ()
      }
    }
    val actor = actorSystem.actorOf(
      Props(
        classOf[AggregatorActor],
        answerPromise,
        actorConfig,
        clientProvider
      )
    )
    val enginesConfig = Seq(new SearchEngineConfig {
      override val host: String = "yandex_host"
      override val port: Int = 42
      override val name: String = "yandex"
    }, new SearchEngineConfig {
      override val host: String = "google_host"
      override val port: Int = 42
      override val name: String = "google"
    }, new SearchEngineConfig {
      override val host: String = "bing_host"
      override val port: Int = 42
      override val name: String = "bing"
    })
    val request = "request"
    actor ! AggregatorActor.SearchQueryMessage(request, enginesConfig)
    val response = Await.result(answerPromise.future, 3.seconds)
    val expectedResponse = AggregatedSearchResponse(
      Map(
        "bing" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from bing_host")
        ),
        "yandex" -> SearchEngineResponse(
          SearchEngineResponse.OK,
          List("Response for request from yandex_host")
        )
      )
    )
    response shouldBe expectedResponse
  }
}
