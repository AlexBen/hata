package com.benderski.hata.telegram;

import com.benderski.hata.infrastructure.ShutdownSignal;
import com.benderski.hata.subscription.PropertyType;
import com.benderski.hata.telegram.service.SubscriptionDialogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.db.DBContext;
import org.telegram.abilitybots.api.objects.*;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.TimeUnit;

@Service
public class SimpleSubscriptionBot extends AbilityBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleSubscriptionBot.class.getName());

    private final SubscriptionDialogService subscriptionDialogService;
    private final ShutdownSignal shutdownSignal;

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
                .info("Показать инфо")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx-> sendText(ctx, "Привет, я бот, который лучше, чем агент по предоплате. " +
                        "\nЯ проверяю сайты с объявлением о сдаче комнат и квартир каждые несколько минут и тут же отправляю их тем, " +
                        "кто настроил фильтр. Всё просто: чтобы начать, используй команду /create." +
                        "\nВсе доступные " +
                        "команды можно просмотреть командой /commands."))
                .build();
    }

    public Ability create() {
        return Ability.builder()
                .name("create")
                .info("Создать фильтр объявлений")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> {
                    if (subscriptionDialogService.hasActiveSubscription(ctx.update())) {
                        sendText(ctx, "У вас уже есть подписка. Чтобы создать новую, удалите существующую");
                    } else {
                        subscriptionDialogService.startFilterCreation(ctx, this::sendMessageFunction);
                    }
                })
                .reply(update -> {
                            if (subscriptionDialogService.isUserInSubscriptionFlow(update)) {
                                subscriptionDialogService.performInputStep(update, this::sendMessageFunction);
                            } else {
                                subscriptionDialogService.selectFlow(update, this::sendMessageFunction);
                            }
                        },
                        Flag.MESSAGE, Flag.TEXT,
                        this::notCommand
                )
                .build();
    }

    public Ability reset() {
        return Ability.builder()
                .name("reset")
                .info("Сбросить фильтр. Активная подписка будет остановлена")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.resetFilter(ctx, this::sendMessageFunction))
                .post(ctx-> sendText(ctx, "Фильтр удалён"))
                .build();
    }

    public Ability showFilter() {
        return Ability.builder()
                .name("showfilter")
                .info("Просмотреть настроенный фильтр")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.processShowSubscription(ctx, this::sendMessageFunction))
                .build();
    }

    public Ability live() {
        return Ability.builder()
                .name("live")
                .info("Активировать подписку на обновления")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.goLive(ctx, this::sendMessageFunction))
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
                .post(ctx -> sendText(ctx, "Bot is shutting down"))
                .build();
    }

    public Ability stopSubscription() {
        return Ability.builder()
                .name("stop")
                .info("Отключить подписку на новый объявления")
                .privacy(Privacy.PUBLIC)
                .locality(Locality.ALL)
                .input(0)
                .action(ctx -> subscriptionDialogService.stopSubscription(ctx, this::sendMessageFunction))
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

    private boolean notCommand(Update update) {
        return !update.getMessage().getText().startsWith("/");
    }

    private void sendText(MessageContext ctx, String text) {
        sendMessageFunction(new SendMessage(ctx.chatId(), text));
    }

    private void sendMessageFunction(SendMessage message) {
        try {
            this.sender.execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }
}
