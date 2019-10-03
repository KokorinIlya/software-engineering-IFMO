package com.github.kokorin.watcher.clients.http

import java.io.Closeable

interface AsyncHttpClient : Closeable {
    suspend fun get(query: String): String
}
