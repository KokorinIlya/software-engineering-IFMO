package com.github.kokorin.aop.demo;

import com.github.kokorin.aop.clock.TimeTravelClock;

import java.time.Duration;

public class TestDemo {
    private final TimeTravelClock clock;

    public TestDemo(TimeTravelClock clock) {
        this.clock = clock;
    }

    public void f() { // 1100 ms
        g();
        clock.plus(Duration.ofMillis(300));
        g();
    }

    private void g() { // 400 ms
        h();
        clock.plus(Duration.ofMillis(200));
        h();
    }

    private void h() { // 100 ms
        clock.plus(Duration.ofMillis(100));
    }

    public void a() { // 400 ms
        clock.plus(Duration.ofMillis(400));
    }
}
