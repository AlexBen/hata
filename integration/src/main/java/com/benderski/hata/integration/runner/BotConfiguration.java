package com.benderski.hata.integration.runner;

import com.benderski.hata.telegram.BotIdentity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotConfiguration {

    @Bean
    public BotIdentity getBotIdentity() {
        return new BotIdentity("569635102:AAFjmRF2ugKoGCHzZYuK7U6QD3eFAWJO3w8", "benderfirst_bot");
    }
}
