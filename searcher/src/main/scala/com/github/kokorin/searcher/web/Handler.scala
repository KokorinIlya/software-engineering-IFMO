package com.github.kokorin.searcher.web

import akka.http.scaladsl.server.Route

trait Handler {
  def route: Route
}
