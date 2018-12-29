package com.benderski.hata.telegram;

import com.benderski.hata.infrastructure.ShutdownSignal;
import com.benderski.hata.telegram.service.SubscriptionDialogService;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.TimeUnit;

@Service
public class SimpleSubscriptionBot extends AbilityBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSubscriptionBot.class.getName());

    private SubscriptionDialogService subscriptionDialogService;
    private ShutdownSignal shutdownSignal;

    public SimpleSubscriptionBot(@Autowired BotIdentity botIdentity, @Autowired DBContext dbContext,
                                 @Autowired SubscriptionDialogService subscriptionDialogService,
                                 @Autowired ShutdownSignal shutdownSignal) {
        super(botIdentity.getToken(), botIdentity.getBotUserName(), dbContext);
        this.subscriptionDialogService = subscriptionDialogService;
        this.shutdownSignal = shutdownSignal;
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
                                subscriptionDialogService.performInputStep(update, this::sendMessageFunction),
                        Flag.MESSAGE, Flag.TEXT,
                        u -> subscriptionDialogService.notCommand(u),
                        u -> subscriptionDialogService.isUserInSubscriptionFlow(u))
                .build();
    }

    public Ability showFilter() {
        return Ability.builder()
                .name("showfilter")
                .info("Просмотреть фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.processShowSubscription(ctx, this::sendMessageFunction))
                .build();
    }

    public Ability live() {
        return Ability.builder()
                .name("live")
                .info("Начать получать обновления")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.goLive(ctx, this::sendMessageFunction))
                .post(ctx -> {
                    sendMessageFunction(new SendMessage(ctx.chatId(),
                            "Отлично, теперь вы будете получать все новые объявления, прошедшие фильтр"));
                    subscriptionDialogService.processShowSubscription(ctx, this::sendMessageFunction);
                })
                .build();
    }

    public Ability shutdown() {
        return Ability.builder()
                .name("shutdown")
                .info("Остановить бота")
                .privacy(Privacy.CREATOR)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> shutdownSignal.stop())
                .post(ctx -> sendMessageFunction(new SendMessage(ctx.chatId(), "Bot is shutting down")))
                .build();
    }

    public Ability stopSubscription() {
        return Ability.builder()
                .name("stop")
                .info("Отключить подписку")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.stopSubscription(ctx))
                .post(ctx -> sendMessageFunction(new SendMessage(
                        ctx.chatId(), "Подписка остановлена. Вы можете возобновить её командой /live")))
                .build();
    }

    @Override
    public void onClosing() {
        try {
            exe.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            exe.shutdown();
        }
    }

    private void sendMessageFunction(SendMessage message) {
        try {
            this.sender.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
}
