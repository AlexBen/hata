package com.benderski.hata.integration;

import com.benderski.hata.remotedataprovider.events.NewApartmentEvent;
import com.benderski.hata.subscription.SubscriptionNotificator;
import com.benderski.hata.subscription.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NewApartmentListener {

    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionNotificator subscriptionNotificator;

    @EventListener(classes = NewApartmentEvent.class)
    public void onApplicationEvent(NewApartmentEvent newApartmentEvent) {
        System.out.println("Event with new apartment! " + newApartmentEvent.getApartment().getLink());
        subscriptionService.getAll().forEach(subscription ->
                subscriptionNotificator.notify(subscription.getId(), newApartmentEvent.getApartment().getLink()));
    }
}
