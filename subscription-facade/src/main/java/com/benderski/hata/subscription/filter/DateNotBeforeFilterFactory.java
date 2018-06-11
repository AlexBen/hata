package com.benderski.hata.subscription.filter;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Predicate;

@Service
public class DateNotBeforeFilterFactory {

    public Predicate<Date> construct(@NonNull Date subscriptionDate) {
        return advertisementDate ->
                advertisementDate != null && subscriptionDate.before(advertisementDate);
    }
}
