package com.benderski.hata.infrastructure;

import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class TaskSchedulerTest {

    private TaskScheduler checker = new TaskScheduler();
    static int i = 0;

    @Test
    void startCheckForUpdate() throws InterruptedException {
        final Counter counter = new Counter();
        long period = 1;
        checker.scheduleRepeatableTask(counter::increment, period, TimeUnit.SECONDS);
        int times = 10;
        long term = TimeUnit.SECONDS.toMillis(period) * times - 1; //9 seconds allows to call 10 times
        Thread.sleep(term);
        checker.stop();
        assertEquals(times, counter.getResult());
    }

    private class Counter {
        int i = 0;

        void increment() {
            i = i+1;
            System.out.println(i);
        }

        int getResult() {
            return i;
        }
    }

}