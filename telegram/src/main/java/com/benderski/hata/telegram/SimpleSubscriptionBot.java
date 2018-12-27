package com.benderski.hata.telegram;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.subscription.SubscriptionService;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.DialogStateStorage;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.abilitybots.api.objects.Flag;
import org.telegram.abilitybots.api.objects.Locality;
import org.telegram.abilitybots.api.objects.Privacy;
import org.telegram.abilitybots.api.sender.MessageSender;
import org.telegram.telegrambots.api.methods.BotApiMethod;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class SimpleSubscriptionBot extends AbilityBot {

    private static Logger LOGGER = Logger.getLogger(SimpleSubscriptionBot.class.getName());

    @Autowired
    @Qualifier("createSubscriptionDialog")
    private DialogFlow subscriptionDialog;

    @Autowired
    private DialogStateStorage dialogStateStorage;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    public SimpleSubscriptionBot(BotIdentity botIdentity, DBContext dbContext) {
        super(botIdentity.getToken(), botIdentity.getBotUserName(), dbContext);
    }

    @Override
    public int creatorId() {
        return 223189122;
    }

    @Override
    public <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method) throws TelegramApiException {
        LOGGER.config(method.toString());
        return super.execute(method);
    }

    public Ability start() {
        return Ability.builder()
                .name("start")
                .info("Создать фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> {
                    int userId = ctx.user().id();
                    Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, subscriptionDialog.getId());
                    ChatStep step = subscriptionDialog.getByIndex(pointer);
                    try {
                        this.execute(new SendMessage(ctx.chatId(), step.getMessage()));
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                })
                .reply(update -> {
                    LOGGER.info(update.toString());
                }, Flag.MESSAGE, Flag.TEXT)
                .build();
    }

    public Ability showFilter() {
        return Ability.builder()
                .name("showFilter")
                .info("Просмотреть фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx-> {
                    int userId = ctx.user().id();
                    SubscriptionModel profile = subscriptionService.getProfile(userId);
                    final String message = Optional.ofNullable(profile).map(Objects::toString)
                    .orElse("Ваш фильтр не настроен. Используйте команду /start");
                    try {
                        this.execute(new SendMessage(ctx.chatId(), message));
                    } catch (TelegramApiException e) {
                        LOGGER.severe(e.getLocalizedMessage());
                    }

                }).build();
    }

    @VisibleForTesting
    void setSender(MessageSender sender) {
        this.sender = sender;
    }
}
