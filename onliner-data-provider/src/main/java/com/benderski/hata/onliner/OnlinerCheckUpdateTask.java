package com.benderski.hata.onliner;

import com.benderski.hata.infrastructure.Apartment;
import io.reactivex.Observer;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

@Service
public class OnlinerCheckUpdateTask implements Runnable {
    private static Logger LOGGER = Logger.getLogger(OnlinerCheckUpdateTask.class.getName());

    private static AtomicLong counter = new AtomicLong(0);

    private final OnlinerRestClient restClient;
    private final Observer<Apartment> observer;

    public OnlinerCheckUpdateTask(OnlinerRestClient restClient, Observer<Apartment> observer) {
        this.restClient = restClient;
        this.observer = observer;
    }

    @Override
    public void run() {
        try {
            LOGGER.info(OnlinerCheckUpdateTask.class.getName() + " task executed, #" + counter.incrementAndGet());
            OnlinerResponse onlinerResponse = restClient.requestList(Collections.emptyMap());
            LOGGER.info("Number of apartments: " + onlinerResponse.getApartments().size());
            onlinerResponse.getApartments().forEach(observer::onNext);
        } catch (Exception e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
    }
}
