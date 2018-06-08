package com.benderski.hata.remotedataprovider;

import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class NewInStorageWatcher implements Watcher {
    private static Logger LOGGER = Logger.getLogger(NewInStorageWatcher.class.getName());

    @Autowired
    private PublishNewApartmentsTask task;

    private TaskScheduler taskScheduler;

    public void start() {
        LOGGER.info("Start watching new in storage");
        Assert.notNull(task, "Task shouldn't be null");

        taskScheduler = new TaskScheduler();
        taskScheduler.scheduleRepeatableTask(task, 10, TimeUnit.SECONDS);
    }

    public void stop() {
        taskScheduler.stop();
    }
}
