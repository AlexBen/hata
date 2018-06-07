package com.benderski.hata.remotedataprovider.events;

import com.benderski.hata.remotedataprovider.Apartment;
import org.springframework.context.ApplicationEvent;

public class NewApartmentEvent extends ApplicationEvent {

    public NewApartmentEvent(Apartment source) {
        super(source);
    }

    public Apartment getApartment() {
        return (Apartment) getSource();
    }
}
