package com.github.kokorin.rx.providers

import com.github.kokorin.rx.http.HTTPClient
import com.github.kokorin.rx.http.HTTPClientImpl
import org.apache.http.impl.client.HttpClients

object HTTPClientsProviderImpl : Provider<HTTPClient> {
    override fun get(): HTTPClient {
        return HTTPClientImpl(HttpClients.createDefault())
    }
}
