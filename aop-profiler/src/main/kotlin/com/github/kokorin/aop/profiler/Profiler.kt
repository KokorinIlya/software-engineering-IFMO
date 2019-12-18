package com.github.kokorin.aop.profiler

import com.github.kokorin.aop.clock.Clock
import com.github.kokorin.aop.model.CompletedMethodCall
import com.github.kokorin.aop.model.MethodCallInProgress
import com.github.kokorin.aop.model.MethodRef
import com.github.kokorin.aop.model.MethodStats
import java.lang.IllegalStateException
import java.time.Duration
import java.time.Instant
import java.util.*

object Profiler {
    private val callsStack: ThreadLocal<Deque<Pair<MethodCallInProgress, Instant>>> = ThreadLocal.withInitial {
        ArrayDeque<Pair<MethodCallInProgress, Instant>>()
    }

    private val stats: ThreadLocal<MutableMap<MethodRef, MethodStats>> = ThreadLocal.withInitial {
        mutableMapOf<MethodRef, MethodStats>()
    }

    private val callRoots: ThreadLocal<MutableList<CompletedMethodCall>> = ThreadLocal.withInitial {
        mutableListOf<CompletedMethodCall>()
    }

    @Volatile
    lateinit var clock: Clock

    fun registerMethodCall(methodRef: MethodRef) {
        val startTime = clock.now()
        val methodCall = MethodCallInProgress(methodRef, mutableListOf())
        callsStack.get().addFirst(Pair(methodCall, startTime))
    }

    fun registerMethodExit(methodRef: MethodRef) {
        val finishTime = clock.now()
        val (lastCalledMethod, callTime) = callsStack.get().pollFirst()
                ?: throw IllegalStateException("Method $methodRef finished without being called")
        if (lastCalledMethod.methodRef != methodRef) {
            throw IllegalStateException("Method ${lastCalledMethod.methodRef} was called, " +
                    "but method $methodRef finished")
        }
        val methodDuration = Duration.between(callTime, finishTime)
        stats.get().compute(methodRef) { _, methodStats ->
            if (methodStats == null) {
                MethodStats(methodDuration, 1)
            } else {
                MethodStats(methodStats.summaryTime + methodDuration,
                        methodStats.callsNumber + 1)
            }
        }
        val completedMethod = CompletedMethodCall(
                methodRef,
                methodDuration,
                lastCalledMethod.InnerCalls.toList()
        )
        if (callsStack.get().size == 0) {
            callRoots.get().add(completedMethod)
        } else {
            val (parentMethod, _) = callsStack.get().peekFirst()
            parentMethod.InnerCalls.add(completedMethod)
        }
    }

    fun getStats(): Map<MethodRef, MethodStats> = stats.get().toMap()

    fun getCallTree(): List<CompletedMethodCall> = callRoots.get().toList()
}
