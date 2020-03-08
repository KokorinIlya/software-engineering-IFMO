package com.github.kokorin.fitness.gate.http

import com.github.kokorin.fitness.gate.config.StatsConfig
import io.ktor.client.HttpClient

class StatsHttpClientsProviderImpl(private val conf: StatsConfig) : StatsHttpClientsProvider {
    override fun getClient(): StatsHttpClient {
        val client = HttpClient()
        return StatsHttpClientImpl(client, conf)
    }
}
