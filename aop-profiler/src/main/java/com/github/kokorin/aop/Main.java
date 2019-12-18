package com.github.kokorin.aop;

import com.github.kokorin.aop.clock.RealTimeClock;
import com.github.kokorin.aop.demo.Demo;
import com.github.kokorin.aop.profiler.Profiler;
import com.github.kokorin.aop.view.ProfileViewMaker;
import com.google.gson.Gson;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Profiler.INSTANCE.setClock(new RealTimeClock());

        Demo demo = new Demo();
        demo.f();
        demo.a();

        Gson gson = new Gson();
        ProfileViewMaker viewMaker = new ProfileViewMaker(gson);

        System.out.println(viewMaker.showStats(Profiler.INSTANCE.getStats()));
        System.out.println(viewMaker.showCallTree(Profiler.INSTANCE.getCallTree()));
    }
}
