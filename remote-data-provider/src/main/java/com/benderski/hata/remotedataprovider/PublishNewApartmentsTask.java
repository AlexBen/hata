package com.benderski.hata.remotedataprovider;

import com.benderski.hata.remotedataprovider.events.NewApartmentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;


public class PublishNewApartmentsTask implements Runnable {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private ApartmentsStorage apartmentsStorage;

    @Override
    public void run() {
        Apartment apartment = apartmentsStorage.getFirstElement();
        if (apartment == null) {
            return;
        }
        System.out.println("New in storage! " + apartment.getLink());
        publishNewApartmentsFound(apartment);
    }

    private void publishNewApartmentsFound(Apartment apartment) {
        publisher.publishEvent(new NewApartmentEvent(apartment));
    }
}
