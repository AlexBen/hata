package com.benderski.hata.integration.runner;

import com.benderski.hata.infrastructure.Apartment;

import io.reactivex.subjects.ReplaySubject;
import io.reactivex.subjects.Subject;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


@Configuration
public class AppConfig {

    @Bean(name = "apartmentStorageObservable")
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    public Subject<Apartment> createPublishSubject() {
        return ReplaySubject.createWithSize(20);
    }
}
