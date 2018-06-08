package com.benderski.hata.onliner;

import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class OnlinerWatcher implements Watcher {
    private static Logger LOGGER = Logger.getLogger(OnlinerWatcher.class.getName());

    static private long executionPeriod = 5;
    static private TimeUnit timeUnit = TimeUnit.MINUTES;

    private TaskScheduler checker;

    @Autowired
    private OnlinerCheckUpdateTask task;


    public void start() {
        LOGGER.info("Start watching Onliner");
        checker = new TaskScheduler();
        checker.scheduleRepeatableTask(task, executionPeriod, timeUnit);
    }

    public void stop() {
        checker.stop();
    }

}
