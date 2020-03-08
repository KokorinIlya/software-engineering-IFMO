package com.github.kokorin.fitness.gate.http

import com.github.kokorin.fitness.gate.config.StatsConfig
import com.github.kokorin.fitness.gate.utils.serialize
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.joda.time.LocalDateTime

class StatsHttpClientImpl(private val client: HttpClient, private val conf: StatsConfig) : StatsHttpClient {
    override suspend fun exitCommand(uid: Int, enterTimestamp: LocalDateTime, exitTimestamp: LocalDateTime): String {
        val enterTimeString = enterTimestamp.serialize()
        val exitTimeString = exitTimestamp.serialize()
        val url = "${conf.schema}://${conf.host}:${conf.port}/command/exit?" +
                "uid=$uid&enter=$enterTimeString&exit=$exitTimeString"
        return client.get(url)
    }
}
