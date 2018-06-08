package com.benderski.hata.remotedataprovider;

import com.benderski.hata.remotedataprovider.events.NewApartmentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PublishNewApartmentsTask implements Runnable {
    private static Logger LOGGER = Logger.getLogger(PublishNewApartmentsTask.class.getName());

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private ApartmentsStorage apartmentsStorage;

    @Override
    public void run() {
        try {
            checkForNewApartment();
        } catch (Exception e) {
            LOGGER.severe(e.getLocalizedMessage());
        }
    }

    private void checkForNewApartment() {
        Apartment apartment = apartmentsStorage.getFirstElement();
        if (apartment == null) {
            return;
        }
        LOGGER.config("New in storage! " + apartment.getLink());
        publishNewApartmentsFound(apartment);
    }

    private void publishNewApartmentsFound(Apartment apartment) {
        publisher.publishEvent(new NewApartmentEvent(apartment));
    }
}
