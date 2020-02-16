package com.github.kokorin.searcher.web.api

import akka.actor.{Actor, ActorSystem}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import com.github.kokorin.searcher.config.ApiConfig
import com.github.kokorin.searcher.web.Handler
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.{Await, ExecutionContextExecutor}
import scala.util.{Failure, Success, Try}

class WebApiStarter(actorSystemName: String,
                    handler: Handler,
                    apiConfig: ApiConfig)
    extends StrictLogging {
  def start(): Unit = {
    implicit val system: ActorSystem = ActorSystem(actorSystemName)
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher
    logger.info(
      s"Ready to bind on interface ${apiConfig.interface}, port ${apiConfig.port}"
    )
    val bindingFuture =
      Http().bindAndHandle(handler.route, apiConfig.interface, apiConfig.port)

    Runtime.getRuntime.addShutdownHook(new Thread() {
      override def run(): Unit = {
        logger.info("Shutting down...")
        val unbindFuture = bindingFuture.flatMap(_.unbind())
        Try {
          Await.result(unbindFuture, apiConfig.unbindTimeout)
        } match {
          case Failure(exception) =>
            logger.error("Error while unbinding API", exception)
          case Success(_) => logger.info("Successfully API unbinding")
        }
        system.terminate()
      }
    })
  }
}
