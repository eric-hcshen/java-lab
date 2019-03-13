package com.tsmc.ta.cpuload;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cpu")
public class CPULoadController {
    static List<CalculationThread> runningCalcs;
    static List<Thread> runningThreads;
    static {
        runningCalcs = new ArrayList<>();
        runningThreads = new ArrayList<>();
    }

    @GetMapping("/add")
    public String AddLoad() {
        CalculationThread r = new CalculationThread(new LongAdder());
        Thread t = new Thread(r);
        runningCalcs.add(r);
        runningThreads.add(t);
        t.start();
        return "Added";
    }

    @GetMapping("/release")
    public String ReleaseLoad() {
        for (int i = 0; i < runningCalcs.size(); i++) {
            runningCalcs.get(i).stop();
            try {
                runningThreads.get(i).join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return "Released";
    }
    public static class CalculationThread implements Runnable
    {
        private final Random rng;
        private final LongAdder calculationsPerformed;
        private boolean stopped;
        private double store;

        public CalculationThread(LongAdder calculationsPerformed)
        {
            this.calculationsPerformed = calculationsPerformed;
            this.stopped = false;
            this.rng = new Random();
            this.store = 1;
        }

        public void stop()
        {
            this.stopped = true;
        }

        @Override
        public void run()
        {
            while (! this.stopped)
            {
                double r = this.rng.nextFloat();
                double v = Math.sin(Math.cos(Math.sin(Math.cos(r))));
                this.store *= v;
                this.calculationsPerformed.add(1);
            }
        }
    }
}