package com.benderski.hata.telegram;

import com.benderski.hata.telegram.service.SubscriptionDialogService;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.function.Consumer;

@Service
public class SimpleSubscriptionBot extends AbilityBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSubscriptionBot.class.getName());

    @Autowired
    private SubscriptionDialogService subscriptionDialogService;

    @Autowired
    public SimpleSubscriptionBot(BotIdentity botIdentity, DBContext dbContext) {
        super(botIdentity.getToken(), botIdentity.getBotUserName(), dbContext);
    }

    @Override
    public int creatorId() {
        return 223189122;
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info("Создать фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.processSubscriptionStart(ctx, this::sendMessageFunction))
                .reply(update ->
                                subscriptionDialogService.performNextStep(update, this::sendMessageFunction),
                        Flag.MESSAGE, Flag.TEXT, subscriptionDialogService::isUserInSubscriptionFlow)
                .build();
    }

    public Ability showFilter() {
        return Ability.builder()
                .name("showFilter")
                .info("Просмотреть фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.processShowSubscription(ctx, this::sendMessageFunction))
                .build();
    }

    private void sendMessageFunction(SendMessage message) {
        try {
            this.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    @VisibleForTesting
    void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
