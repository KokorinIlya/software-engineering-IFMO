package com.github.kokorin.todo.utils

fun <T, U> T?.map(block: (T) -> U): U? {
    return if (this != null) {
        block(this)
    } else {
        null
    }
}
