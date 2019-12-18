package com.github.kokorin.aop.model

data class MethodCallInProgress(val methodRef: MethodRef, val InnerCalls: MutableList<CompletedMethodCall>)
