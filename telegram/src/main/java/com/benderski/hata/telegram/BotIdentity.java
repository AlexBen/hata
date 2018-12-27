package com.benderski.hata.telegram;

public class BotIdentity {
    private final String token;
    private final String botUserName;

    public BotIdentity(String token, String botUserName) {
        this.token = token;
        this.botUserName = botUserName;
    }

    public String getToken() {
        return token;
    }

    public String getBotUserName() {
        return botUserName;
    }
}
