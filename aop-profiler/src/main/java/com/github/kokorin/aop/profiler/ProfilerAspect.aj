package com.github.kokorin.aop.profiler;

import com.github.kokorin.aop.model.MethodRef;

public aspect ProfilerAspect {
    pointcut methodCall(): call (* *.*(..)) &&
            !within(com.github.kokorin.aop.profiler.Profiler) &&
            !within(com.github.kokorin.aop.profiler.ProfilerAspect);

    before(): methodCall() {
        Profiler.INSTANCE.registerMethodCall(
                new MethodRef(
                        thisJoinPointStaticPart.getSignature().getDeclaringTypeName(),
                        thisJoinPointStaticPart.getSignature().getName()
                )
        );
    }

    after(): methodCall() {
        Profiler.INSTANCE.registerMethodExit(
                new MethodRef(
                        thisJoinPointStaticPart.getSignature().getDeclaringTypeName(),
                        thisJoinPointStaticPart.getSignature().getName()
                )
        );
    }
}
