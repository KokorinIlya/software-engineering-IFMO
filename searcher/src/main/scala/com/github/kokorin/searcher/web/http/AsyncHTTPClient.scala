package com.github.kokorin.searcher.web.http

import java.io.Closeable
import java.util.concurrent.Future

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback

trait AsyncHTTPClient extends Closeable {
  def start(): Unit

  def execute(request: HttpUriRequest,
              callback: FutureCallback[String]): Future[_]
}
