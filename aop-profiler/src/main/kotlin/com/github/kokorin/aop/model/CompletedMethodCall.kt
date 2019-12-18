package com.github.kokorin.aop.model

import java.time.Duration

data class CompletedMethodCall(val methodRef: MethodRef,
                               val callDuration: Duration,
                               val InnerCalls: List<CompletedMethodCall>)
