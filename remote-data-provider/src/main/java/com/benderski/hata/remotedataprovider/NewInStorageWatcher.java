package com.benderski.hata.remotedataprovider;

import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class NewInStorageWatcher implements Watcher {

    private TaskScheduler taskScheduler;

    public void start() {
        PublishNewApartmentsTask task = new PublishNewApartmentsTask();
        taskScheduler = new TaskScheduler();
        taskScheduler.scheduleRepetableTask(task, 1, TimeUnit.MINUTES);
    }

    public void stop() {
        taskScheduler.stop();
    }
}
