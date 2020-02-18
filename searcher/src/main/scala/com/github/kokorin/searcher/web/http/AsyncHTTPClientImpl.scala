package com.github.kokorin.searcher.web.http

import java.util.concurrent.Future

import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.util.EntityUtils

class AsyncHTTPClientImpl(httpClient: CloseableHttpAsyncClient)
    extends AsyncHTTPClient {
  override def start(): Unit = httpClient.start()

  override def execute(request: HttpUriRequest,
                       callback: FutureCallback[String]): Future[_] =
    httpClient.execute(request, new FutureCallback[HttpResponse] {
      override def completed(t: HttpResponse): Unit = {
        val stringResponse = EntityUtils.toString(t.getEntity)
        callback.completed(stringResponse)
      }

      override def failed(e: Exception): Unit = callback.failed(e)

      override def cancelled(): Unit = callback.cancelled()
    })

  override def close(): Unit = httpClient.close()
}
