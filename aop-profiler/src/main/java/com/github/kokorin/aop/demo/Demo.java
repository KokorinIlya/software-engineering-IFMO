package com.github.kokorin.aop.demo;

public class Demo {
    public void f() throws InterruptedException {
        g();
        Thread.sleep(300);
        g();
    }

    private void g() throws InterruptedException {
        h();
        Thread.sleep(200);
        h();
    }

    private void h() throws InterruptedException {
        Thread.sleep(100);
    }

    public void a() throws InterruptedException {
        Thread.sleep(400);
    }
}
