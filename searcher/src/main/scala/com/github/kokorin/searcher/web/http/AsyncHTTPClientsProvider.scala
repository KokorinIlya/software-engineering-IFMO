package com.github.kokorin.searcher.web.http

trait AsyncHTTPClientsProvider {
  def newAsyncClient: AsyncHTTPClient
}


