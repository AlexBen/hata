package com.benderski.hata.integration.runner;

import com.benderski.hata.infrastructure.Initializer;
import com.benderski.hata.infrastructure.Watcher;
import com.benderski.hata.telegram.SimpleSubscriptionBot;
import com.benderski.hata.telegram.SubscriptionChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class MyApplication implements Runnable {

    @Autowired
    SimpleSubscriptionBot simpleSubscriptionBot;
    @Autowired
    SubscriptionChatService subscriptionChatService;
    @Autowired
    Set<Watcher> watchers;

    public void run() {
        simpleSubscriptionBot.setSubscriptionChatService(subscriptionChatService);
        watchers.forEach(Watcher::start);
        try {
            Thread.sleep(1000*60*60*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            watchers.forEach(Watcher::stop);
        }
    }

}
