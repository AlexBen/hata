package com.benderski.hata.subscription.filter;

import org.springframework.lang.NonNull;

import java.util.Date;
import java.util.function.Predicate;

public class DateNotBeforeFilterFactory {

    public static Predicate<Date> construct(@NonNull Date subscribtionDate) {
        return advertisementDate ->
                advertisementDate != null && subscribtionDate.before(advertisementDate);
    }
}
