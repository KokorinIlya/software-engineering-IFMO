package com.github.kokorin.watcher.clients.http

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.slf4j.LoggerFactory

class AsyncHttpClientImpl(private val httpClient: HttpClient) : AsyncHttpClient {
    private val log = LoggerFactory.getLogger(this.javaClass)

    override suspend fun get(query: String): String {
        return httpClient.get(query)
    }

    override fun close() {
        try {
            httpClient.close()
            log.info("KTOR Http client closed")
        } catch (e: Throwable) {
            log.error("Error while closing Http client", e)
            throw e
        }
    }

}
