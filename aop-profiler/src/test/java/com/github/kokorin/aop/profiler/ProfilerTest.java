package com.github.kokorin.aop.profiler;

import com.github.kokorin.aop.clock.TimeTravelClock;
import com.github.kokorin.aop.demo.TestDemo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

public class ProfilerTest {
    private ProfilerTestChecker profilerTestChecker = new ProfilerTestChecker();

    @Before
    public void clear() {
        Profiler.INSTANCE.clear();
    }

    @Test
    public void testSimple() {
        TimeTravelClock clock = new TimeTravelClock(Instant.now());
        TestDemo testDemo = new TestDemo(clock);

        Profiler.INSTANCE.setClock(clock);
        Profiler.INSTANCE.setPackageName("com.github.kokorin.aop.demo");

        testDemo.a();

        profilerTestChecker.checkSimple(Profiler.INSTANCE.getCallTree(), Profiler.INSTANCE.getStats());
    }

    @Test
    public void testSequential() {
        TimeTravelClock clock = new TimeTravelClock(Instant.now());
        TestDemo testDemo = new TestDemo(clock);

        Profiler.INSTANCE.setClock(clock);
        Profiler.INSTANCE.setPackageName("com.github.kokorin.aop.demo");

        testDemo.a();
        testDemo.a();

        profilerTestChecker.checkSequential(Profiler.INSTANCE.getCallTree(), Profiler.INSTANCE.getStats());
    }

    @Test
    public void testInner() {
        TimeTravelClock clock = new TimeTravelClock(Instant.now());
        TestDemo testDemo = new TestDemo(clock);

        Profiler.INSTANCE.setClock(clock);
        Profiler.INSTANCE.setPackageName("com.github.kokorin.aop.demo");

        testDemo.f();

        profilerTestChecker.checkInner(Profiler.INSTANCE.getCallTree(), Profiler.INSTANCE.getStats());
    }

    @Test
    public void testComplex() {
        TimeTravelClock clock = new TimeTravelClock(Instant.now());
        TestDemo testDemo = new TestDemo(clock);

        Profiler.INSTANCE.setClock(clock);
        Profiler.INSTANCE.setPackageName("com.github.kokorin.aop.demo");

        testDemo.f();
        testDemo.a();

        profilerTestChecker.checkComplex(Profiler.INSTANCE.getCallTree(), Profiler.INSTANCE.getStats());
    }

    @Test
    public void testMultithreading() throws InterruptedException {
        Thread thread1 = new Thread(this::testSimple);
        Thread thread2 = new Thread(this::testComplex);
        AtomicBoolean wrong = new AtomicBoolean(false);
        thread1.setUncaughtExceptionHandler(
                (t, e) -> {
                    System.out.println("Thread " + t + " exited with error " + e);
                    wrong.set(true);
                }
        );
        thread2.setUncaughtExceptionHandler(
                (t, e) -> {
                    System.out.println("Thread " + t + " exited with error " + e);
                    wrong.set(true);
                }
        );
        thread1.start();
        thread2.start();
        thread1.join();
        thread2.join();
        Assert.assertFalse(wrong.get());
    }
}
