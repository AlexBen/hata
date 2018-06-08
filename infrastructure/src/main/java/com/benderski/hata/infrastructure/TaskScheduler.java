package com.benderski.hata.infrastructure;

import org.springframework.lang.NonNull;

import java.util.concurrent.*;


public class TaskScheduler {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);

    public void scheduleRepeatableTask(@NonNull Runnable runnable, long period, TimeUnit timeUnit) {
        executorService.scheduleAtFixedRate(runnable,0, period, timeUnit);
    }

    public void stop() {
        executorService.shutdown();
    }
}
