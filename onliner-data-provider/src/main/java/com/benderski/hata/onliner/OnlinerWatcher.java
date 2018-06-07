package com.benderski.hata.onliner;

import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class OnlinerWatcher implements Watcher {

    static private long executionPeriod = 5;
    static private TimeUnit timeUnit = TimeUnit.MINUTES;

    private TaskScheduler checker;


    public void start() {
        checker = new TaskScheduler();
        Runnable task = new OnlinerCheckUpdateTask();
        checker.scheduleRepetableTask(task, executionPeriod, timeUnit);
    }

    public void stop() {
        checker.stop();
    }

}
