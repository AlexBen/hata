package com.benderski.hata.telegram;

import com.benderski.hata.infrastructure.Initializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.logging.Logger;

@Service
public class BotInitializer implements Initializer {

    private static Logger LOGGER = Logger.getLogger(BotInitializer.class.getName());

    @Autowired
    SubscriptionChatService subscriptionChatService;

    @Override
    public void init() {

        LOGGER.info("Bot initializing");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        SimpleSubscriptionBot bot = new SimpleSubscriptionBot();
        try {
            botsApi.registerBot(bot);

            LOGGER.info("Bot initialized " + bot.getBotUsername());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
