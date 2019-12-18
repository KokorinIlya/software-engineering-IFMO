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
        return stats
                .toList()
                .sortedBy { it.second.summaryTime }
                .reversed()
                .joinToString(separator = ";\n\n", postfix = ";\n\n") { (method, stats) ->
                    val averageTime = stats.summaryTime.dividedBy(stats.callsNumber.toLong())
                    "${method.className}::${method.methodName}\n" +
                            "Number of calls: ${stats.callsNumber}\n" +
                            "Total time spent: ${stats.summaryTime.toMillis()} milliseconds\n" +
                            "Average time per call: ${averageTime.toMillis()} milliseconds"
                }
    }
}
