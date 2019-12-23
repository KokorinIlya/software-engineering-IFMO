package com.github.kokorin.aop.profiler

import com.github.kokorin.aop.model.CompletedMethodCall
import com.github.kokorin.aop.model.MethodRef
import com.github.kokorin.aop.model.MethodStats
import org.junit.Assert.*
import java.time.Duration

class ProfilerTestChecker {
    fun checkSimple(testResult: List<CompletedMethodCall>, testStats: Map<MethodRef, MethodStats>) {
        assertTrue(testResult == listOf(
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        callDuration = Duration.ofMillis(400),
                        innerCalls = emptyList()
                )
        ))

        assertTrue(testStats == mapOf(
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        MethodStats(summaryTime = Duration.ofMillis(400), callsNumber = 1)
                )
        ))
    }

    fun checkSequential(testResult: List<CompletedMethodCall>, testStats: Map<MethodRef, MethodStats>) {
        assertTrue(testResult == listOf(
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        callDuration = Duration.ofMillis(400),
                        innerCalls = emptyList()
                ),
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        callDuration = Duration.ofMillis(400),
                        innerCalls = emptyList()
                )
        ))

        assertTrue(testStats == mapOf(
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        MethodStats(summaryTime = Duration.ofMillis(800), callsNumber = 2)
                )
        ))
    }

    fun checkInner(testResult: List<CompletedMethodCall>, testStats: Map<MethodRef, MethodStats>) {
        assertTrue(testResult == listOf(
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo",
                                methodName = "f"
                        ),
                        callDuration = Duration.ofMillis(1100),
                        innerCalls = listOf(
                                CompletedMethodCall(
                                        methodRef = MethodRef(
                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                methodName = "g"
                                        ),
                                        callDuration = Duration.ofMillis(400),
                                        innerCalls = listOf(
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                ),
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                )
                                        )
                                ),
                                CompletedMethodCall(
                                        methodRef = MethodRef(
                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                methodName = "g"
                                        ),
                                        callDuration = Duration.ofMillis(400),
                                        innerCalls = listOf(
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                ),
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                )
                                        )
                                )
                        )
                )
        ))

        assertTrue(testStats == mapOf(
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "f"),
                        MethodStats(summaryTime = Duration.ofMillis(1100), callsNumber = 1)
                ),
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "g"),
                        MethodStats(summaryTime = Duration.ofMillis(800), callsNumber = 2)
                ),
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "h"),
                        MethodStats(summaryTime = Duration.ofMillis(400), callsNumber = 4)
                )
        ))
    }

    fun checkComplex(testResult: List<CompletedMethodCall>, testStats: Map<MethodRef, MethodStats>) {
        assertTrue(testResult == listOf(
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo",
                                methodName = "f"
                        ),
                        callDuration = Duration.ofMillis(1100),
                        innerCalls = listOf(
                                CompletedMethodCall(
                                        methodRef = MethodRef(
                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                methodName = "g"
                                        ),
                                        callDuration = Duration.ofMillis(400),
                                        innerCalls = listOf(
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                ),
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                )
                                        )
                                ),
                                CompletedMethodCall(
                                        methodRef = MethodRef(
                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                methodName = "g"
                                        ),
                                        callDuration = Duration.ofMillis(400),
                                        innerCalls = listOf(
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                ),
                                                CompletedMethodCall(
                                                        methodRef = MethodRef(
                                                                className = "com.github.kokorin.aop.demo.TestDemo",
                                                                methodName = "h"
                                                        ),
                                                        callDuration = Duration.ofMillis(100),
                                                        innerCalls = emptyList()
                                                )
                                        )
                                )
                        )
                ),
                CompletedMethodCall(
                        methodRef = MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        callDuration = Duration.ofMillis(400),
                        innerCalls = emptyList()
                )
        ))

        assertTrue(testStats == mapOf(
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "f"),
                        MethodStats(summaryTime = Duration.ofMillis(1100), callsNumber = 1)
                ),
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "g"),
                        MethodStats(summaryTime = Duration.ofMillis(800), callsNumber = 2)
                ),
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "h"),
                        MethodStats(summaryTime = Duration.ofMillis(400), callsNumber = 4)
                ),
                Pair(
                        MethodRef(className = "com.github.kokorin.aop.demo.TestDemo", methodName = "a"),
                        MethodStats(summaryTime = Duration.ofMillis(400), callsNumber = 1)
                )
        ))
    }
}
