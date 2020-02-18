package com.github.kokorin.searcher.web

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

trait Handler {
  private def pingRoute: Route = (path("ping") & get) {
    complete("Pong!")
  }

  def route: Route

  final def allRoute: Route = route ~ pingRoute
}
