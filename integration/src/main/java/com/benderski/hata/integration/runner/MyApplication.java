package com.benderski.hata.integration.runner;


import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import com.benderski.hata.infrastructure.Apartment;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class MyApplication implements Runnable {

    @Autowired
    private Watcher<Apartment> watcher;

    @Autowired
    @Qualifier("apartmentStorageObservable")
    private Observer<Apartment> subject;


    public void run() {
        //TaskScheduler taskScheduler = watcher.scheduleTaskWithObserver(subject);
        try {
            Thread.sleep(1000*60*60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //taskScheduler.stop();
        }
    }

    public void setWatcher(Watcher<Apartment> watcher) {
        this.watcher = watcher;
    }

    public void setSubject(PublishSubject<Apartment> subject) {
        this.subject = subject;
    }
}
