package com.github.kokorin.aop.view

import com.github.kokorin.aop.model.CompletedMethodCall
import com.github.kokorin.aop.model.MethodRef
import com.github.kokorin.aop.model.MethodStats
import com.google.gson.Gson

class ProfileViewMaker(private val gson: Gson) {
    fun showCallTree(roots: List<CompletedMethodCall>): String {
        return gson.toJson(roots)
    }

    fun showStats(stats: Map<MethodRef, MethodStats>): String {
        return stats.map {
            val averageTime = it.value.summaryTime.dividedBy(it.value.callsNumber.toLong())
            "${it.key.className}::${it.key.methodName}\n" +
                    "Number of calls: ${it.value.callsNumber}\n" +
                    "Total time spent: ${it.value.summaryTime.toMillis()} milliseconds\n" +
                    "Average time per call: ${averageTime.toMillis()} milliseconds"
        }.joinToString(separator = ";\n\n")
    }
}
