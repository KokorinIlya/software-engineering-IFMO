package com.github.kokorin.fitness.common.processor

interface Processor<T> {
    suspend fun process(t: T): String {
        return try {
            doProcess(t)
        } catch (e: Exception) {
            "Error executing $t: ${e.message}"
        }
    }

    suspend fun doProcess(t: T): String
}
