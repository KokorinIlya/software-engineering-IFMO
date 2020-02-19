package com.github.kokorin.rx.providers

interface Provider<T> {
    fun get(): T
}
