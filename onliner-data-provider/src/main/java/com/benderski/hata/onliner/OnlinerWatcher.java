package com.benderski.hata.onliner;

import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import com.benderski.hata.infrastructure.Apartment;
import io.reactivex.Observer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class OnlinerWatcher implements Watcher<Apartment> {
    private static Logger LOGGER = Logger.getLogger(OnlinerWatcher.class.getName());

    static private long executionPeriod = 5;
    static private TimeUnit timeUnit = TimeUnit.MINUTES;

    @Autowired
    OnlinerRestClient restClient;


    @Override
    public TaskScheduler scheduleTaskWithObserver(Observer<Apartment> observer) {
        LOGGER.info("Start watching Onliner");
        TaskScheduler scheduler = new TaskScheduler();
        OnlinerCheckUpdateTask task = new OnlinerCheckUpdateTask(restClient, observer);
        scheduler.scheduleRepeatableTask(task, executionPeriod, timeUnit);
        return scheduler;
    }
}
