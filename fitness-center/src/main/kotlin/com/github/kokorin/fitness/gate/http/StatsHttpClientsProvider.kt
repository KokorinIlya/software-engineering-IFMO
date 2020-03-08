package com.github.kokorin.fitness.gate.http

interface StatsHttpClientsProvider {
    fun getClient(): StatsHttpClient
}
