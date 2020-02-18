package com.github.kokorin.searcher.web.http

import org.apache.http.impl.nio.client.HttpAsyncClients

object AsyncHTTPClientsProviderImpl extends AsyncHTTPClientsProvider {
  override def newAsyncClient: AsyncHTTPClient =
    new AsyncHTTPClientImpl(HttpAsyncClients.createDefault())
}
