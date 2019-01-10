package com.benderski.hata.integration.runner;


import com.benderski.hata.infrastructure.ShutdownSignal;
import com.benderski.hata.infrastructure.TaskScheduler;
import com.benderski.hata.infrastructure.Watcher;
import com.benderski.hata.infrastructure.Apartment;
import com.benderski.hata.telegram.SimpleSubscriptionBot;
import io.reactivex.Observer;
import io.reactivex.subjects.PublishSubject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.IOException;


@Configuration
@Component
public class MyApplication implements Runnable {

    static {
        ApiContextInitializer.init();
    }

    private final static Logger LOGGER = LoggerFactory.getLogger(MyApplication.class);

    @Autowired
    private ShutdownSignal shutdownSignal;

    @Autowired
    private DBContext dbContext;

    @Autowired
    private Watcher<Apartment> watcher;

    @Autowired
    private SimpleSubscriptionBot bot;

    @Autowired
    @Qualifier("apartmentStorageObservable")
    private Observer<Apartment> subject;

    public void run() {
        TaskScheduler taskScheduler = watcher.scheduleTaskWithObserver(subject);
        try {
            init(bot);
            while (!shutdownSignal.isStopped()) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        } finally {
            LOGGER.info("Shutting down");
            bot.onClosing();
            try {
                taskScheduler.stop();
                dbContext.close();
            } catch (IOException e) {
                LOGGER.error(e.getLocalizedMessage(), e);
            }
        }
    }

    public static void init(SimpleSubscriptionBot bot) {
        LOGGER.info("Bot initializing");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(bot);
            LOGGER.info("Bot initialized " + bot.getBotUsername());
        } catch (TelegramApiException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public void setWatcher(Watcher<Apartment> watcher) {
        this.watcher = watcher;
    }

    public void setSubject(PublishSubject<Apartment> subject) {
        this.subject = subject;
    }
}
