package com.github.kokorin.calculator.utils

fun <K, V> Map<K, V>.getOrThrow(key: K): V {
    return get(key) ?: throw NoSuchElementException("No key $key in map")
}
