package com.github.kokorin.watcher.clients.http

import org.junit.Test
import com.xebialabs.restito.server.StubServer;
import org.junit.Assert.*;
import com.xebialabs.restito.semantics.Condition.startsWithUri
import com.xebialabs.restito.builder.stub.StubHttp.whenHttp
import org.glassfish.grizzly.http.Method;
import com.xebialabs.restito.semantics.Action.stringContent
import com.xebialabs.restito.semantics.Condition.method
import io.ktor.client.HttpClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.io.Closeable


class AsyncHttpClientImplTest {
    private val port = 2517
    private val log = LoggerFactory.getLogger(this.javaClass)

    @Test
    fun testGetRequests() = runBlocking {
        val client = AsyncHttpClientImpl(HttpClient())
        withStubServer(port) {
            whenHttp(it)
                .match(method(Method.GET), startsWithUri("/ping"))
                .then(stringContent("pong"))

            val response = client.get("http://localhost:$port/ping")
            assertEquals(response, "pong")
        }
    }

    private suspend fun withStubServer(port: Int, callback: suspend CoroutineScope.(StubServer) -> Unit) {
        object : Closeable {
            val server = StubServer(port)

            override fun close() {
                try {
                    server.stop()
                    log.info("Stub server stopped")
                } catch (e: Throwable) {
                    log.info("Error stopping stub server")
                    throw e
                }
            }
        }.use {
            it.server.start()
            GlobalScope.callback(it.server)
        }
    }
}
