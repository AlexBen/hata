package com.benderski.hata.integration.runner;

import com.benderski.hata.telegram.SimpleSubscriptionBot;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;

@SpringBootApplication
@ComponentScan("com.benderski.hata.**")
public class Application {


    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        SimpleSubscriptionBot bot = ctx.getBean(SimpleSubscriptionBot.class);
        init(bot);
        ctx.getBean(MyApplication.class).run();
        return args -> {

        };
    }


    public static void init(SimpleSubscriptionBot bot) {
        //LOGGER.info("Bot initializing");
        TelegramBotsApi botsApi = new TelegramBotsApi();
        try {
            botsApi.registerBot(bot);
    //        LOGGER.info("Bot initialized " + bot.getBotUsername());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }



}
