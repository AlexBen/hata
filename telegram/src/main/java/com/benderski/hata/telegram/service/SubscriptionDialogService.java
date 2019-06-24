package com.benderski.hata.telegram.service;

import com.benderski.hata.subscription.PropertyType;
import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.subscription.SubscriptionService;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.DialogProvider;
import com.benderski.hata.telegram.dialog.DialogStateStorage;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;
import com.benderski.hata.telegram.dialog.steps.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SubscriptionDialogService {

    private static final Set<String> PROPERTY_TYPES = Stream.of(PropertyType.values())
            .map(PropertyType::getType).collect(Collectors.toSet());

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDialogService.class);

    @Autowired
    private DialogProvider dialogProvider;

    @Autowired
    private DialogStateStorage dialogStateStorage;

    @Autowired
    private SubscriptionService subscriptionService;

    public boolean hasActiveSubscription(Update update) {
        return subscriptionService.hasSubscription(getUserId(update));
    }

    public void startFilterCreation(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final Integer userId = ctx.user().getId();
        final Long chatId = ctx.chatId();
        boolean isAlreadyStarted = isUserInSubscriptionFlow(userId);
        if (isAlreadyStarted) {
            sendMessage.accept(new SendMessage(chatId, "Создаём фильтр заново"));
            resetFilter(ctx, sendMessage);
        }
        sendSelectPropertyType(ctx, sendMessage);
    }

    public void selectFlow(Update update, Consumer<SendMessage> sendMessage) {
        final String dialogId = update.getMessage().getText();
        Integer userId = getUserId(update);
        Long chatId = update.getMessage().getChatId();
        if (PROPERTY_TYPES.contains(dialogId)) {
            dialogStateStorage.getOrInitForUserAndDialog(userId, dialogId);
            processNext(userId, chatId, sendMessage);
        }
    }

    public void performInputStep(Update update, Consumer<SendMessage> sendMessage) {
        final Integer userId = getUserId(update);
        InputChatStep<?, ?, SubscriptionModel> step = (InputChatStep) getStep(userId, getDialog(userId).getId());
        if (step == null) {
            LOGGER.warn("Flow is ended for user " + userId);
            return;
        }
        final String text = update.getMessage().getText();
        final SubscriptionModel profile = subscriptionService.createOrGetProfile(userId);
        StepResult result;
        try {
            result = step.performStepAction(profile, text);
        } catch (IllegalArgumentException e) {
            sendMessage.accept(new SendMessage(update.getMessage().getChatId(), "Ой. Что-то не то ввели"));
            sendMessage.accept(new SendMessage(update.getMessage().getChatId(), step.getMessage()));
            return;
        }
        if (!result.isSuccess()) {
            sendMessage.accept(new SendMessage(update.getMessage().getChatId(), result.getMessage()));
            sendMessage.accept(new SendMessage(update.getMessage().getChatId(), step.getMessage()));
            return;
        }
        subscriptionService.updateProfile(userId, profile);
        dialogStateStorage.incrementFor(userId);
        processNext(userId, update.getMessage().getChatId(), sendMessage);
    }

    public void stopSubscription(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final Integer userId = ctx.user().getId();
        boolean unsubscribed = subscriptionService.unsubscribe(userId);
        if (unsubscribed) {
            sendMessage.accept(new SendMessage(ctx.chatId(),
                    "Подписка остановлена. Вы можете активировать её командой /live"));
        } else {
            sendMessage.accept(new SendMessage(ctx.chatId(),
                    "Ваша подписка не активна. Вы можете активировать её командой /live"));
        }
    }

    public void processShowSubscription(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        int userId = ctx.user().getId();
        SubscriptionModel profile = subscriptionService.getProfile(userId);
        final String message = Optional.ofNullable(profile).map(Objects::toString)
                .orElse("Ваш фильтр не настроен. Используйте команду /create");
        try {
            sendMessage.accept(new SendMessage(ctx.chatId(), message));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public void goLive(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        int userId = ctx.user().getId();
        boolean subscribed = subscriptionService.startSubscription(userId,
                s -> sendMessage.accept(new SendMessage(ctx.chatId(), s)));
        if (subscribed) {
            sendMessage.accept(new SendMessage(ctx.chatId(),
                    "Отлично, теперь вы будете получать все новые объявления, прошедшие фильтр"));
            processShowSubscription(ctx, sendMessage);
        } else {
            sendMessage.accept(new SendMessage(ctx.chatId(),
                    "Ваша подписка уже активна. Чтобы деактивировать, используйте команду /stop"));
        }
    }

    public void resetFilter(MessageContext ctx, Consumer<SendMessage> sendMessageFunction) {
        final Integer userId = getUserId(ctx.update());
        subscriptionService.unsubscribe(userId);
        subscriptionService.updateProfile(userId, new SubscriptionModel());
        dialogStateStorage.finishDialog(userId);
    }

    public boolean isUserInSubscriptionFlow(Update update) {
        return isUserInSubscriptionFlow(getUserId(update));
    }

    private void sendSelectPropertyType(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final KeyboardRow row = new KeyboardRow();
        row.add("Квартира");
        row.add("Комната");
        final List<KeyboardRow> rows = Collections.singletonList(row);
        final ReplyKeyboardMarkup replyKeyboard
                = new ReplyKeyboardMarkup()
                .setResizeKeyboard(true)
                .setOneTimeKeyboard(true);
        replyKeyboard.setKeyboard(rows);
        sendMessageWithKeyboard("Выберите тип жилья", ctx.chatId(), sendMessage, replyKeyboard);
    }

    private void processNextWith(final Integer userId, final Long chatId, Consumer<SendMessage> sendMessage,
                                 @Nullable ReplyKeyboard withKeyboard) {
        ChatStep step = getStep(userId, getDialog(userId).getId());
        while (step != null) {
            sendMessageWithKeyboard(step.getMessage(), chatId, sendMessage, withKeyboard);
            if (step.hasInput()) {
                //wait for the input
                return;
            }
            dialogStateStorage.incrementFor(userId);
            step = getStep(userId, getDialog(userId).getId());
        }
    }

    private void sendMessageWithKeyboard(String text, final Long chatId, Consumer<SendMessage> sendMessage,
                                         @Nullable ReplyKeyboard withKeyboard) {
        SendMessage message = new SendMessage(chatId, text);
        if (withKeyboard != null) {
            message.setReplyMarkup(withKeyboard);
        }
        sendMessage.accept(message);
    }

    @Nullable
    private ChatStep getStep(Integer userId, String dialogId) {
        Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, dialogId);
        ChatStep step = getDialog(userId).getByIndex(pointer);
        if (step == null) {
            dialogStateStorage.finishDialog(userId);
            subscriptionService.commit();
        }
        return step;
    }

    private void processNext(final Integer userId, final Long chatId, Consumer<SendMessage> sendMessage) {
        processNextWith(userId, chatId, sendMessage, null);
    }

    private boolean isUserInSubscriptionFlow(Integer userId) {
        return dialogStateStorage.hasDialogStarted(userId, () -> getDialog(userId).getId());
    }

    private boolean validateProfile(SubscriptionModel model) {
        return true;
    }

    private Integer getUserId(Update update) {
        User user = AbilityUtils.getUser(update);
        return user.getId();
    }

    private DialogFlow getDialog(Integer userId) {
        return dialogProvider.getDialog(userId);
    }

    public void setDialogStateStorage(DialogStateStorage dialogStateStorage) {
        this.dialogStateStorage = dialogStateStorage;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}
