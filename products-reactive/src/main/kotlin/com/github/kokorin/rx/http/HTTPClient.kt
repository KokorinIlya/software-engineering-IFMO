package com.github.kokorin.rx.http

import java.io.Closeable

interface HTTPClient : Closeable {
    fun getResponse(url: String): String
}
