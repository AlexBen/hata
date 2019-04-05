package com.benderski.hata.telegram.service;

import com.benderski.hata.subscription.PropertyType;
import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.subscription.SubscriptionService;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.DialogStateStorage;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;
import com.benderski.hata.telegram.dialog.steps.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class SubscriptionDialogService implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDialogService.class);
    private static final Set<String> PROPERTY_TYPES = Stream.of(PropertyType.values())
            .map(PropertyType::getType).collect(Collectors.toSet());

    @Autowired
    @Qualifier("createFlatFilterDialog")
    private DialogFlow flatDialog;

    @Autowired
    @Qualifier("createRoomFilterDialog")
    private DialogFlow roomDialog;

    private Map<String, DialogFlow> dialogsMap;

    @Override
    public void afterPropertiesSet() {
        dialogsMap = Stream.of(flatDialog, roomDialog)
                .collect(Collectors.toMap(DialogFlow::getId, Function.identity()));
    }

    @Autowired
    private DialogStateStorage dialogStateStorage;

    @Autowired
    private SubscriptionService subscriptionService;

    public void startFilterCreation(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final Integer userId = ctx.user().getId();
        final Long chatId = ctx.chatId();
        boolean isAlreadyStarted = isUserInSubscriptionFlow(userId);
        if (isAlreadyStarted) {
            sendMessage.accept(new SendMessage(chatId, "Для сброса фильтра используйте команду /reset"));
        }
        sendSelectPropertyType(ctx, sendMessage);
        //processNext(userId, chatId, sendMessage);
    }

    private void sendSelectPropertyType(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final KeyboardRow row = new KeyboardRow();
        row.add("Квартира");
        row.add("Комната");
        final List<KeyboardRow> rows = Collections.singletonList(row);
        final ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
        replyKeyboard.setKeyboard(rows);
        final SendMessage message = new SendMessage(ctx.chatId(), "Выберите тип жилья");
        message.setReplyMarkup(replyKeyboard);
        sendMessage.accept(message);
    }

    public void selectFlow(Update update, Object sendMessageFunction) {
        final String text = update.getMessage().getText();
        if (PROPERTY_TYPES.contains(text)) {
            PropertyType type = PropertyType.valueOf(text);

        }
    }

    private void processNext(final Integer userId, final Long chatId, Consumer<SendMessage> sendMessage){
        ChatStep step = getStep(userId, getDialog(userId).getId());
        while (step != null) {
            sendMessage.accept(new SendMessage(chatId, step.getMessage()));
            if (step.hasInput()) {
                //wait for the input
                return;
            }
            dialogStateStorage.incrementFor(userId, getDialog(userId).getId());
            step = getStep(userId, getDialog(userId).getId());
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
        dialogStateStorage.incrementFor(userId, getDialog(userId).getId());
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

    @Nullable
    private ChatStep getStep(Integer userId, String dialogId) {
        Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, dialogId);
        ChatStep step = getDialog(userId).getByIndex(pointer);
        if (step == null) {
            dialogStateStorage.finishDialog(userId, dialogId);
            subscriptionService.commit();
        }
        return step;
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
        if(subscribed) {
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
        dialogStateStorage.finishDialog(userId, getDialog(userId).getId());
    }

    public boolean isUserInSubscriptionFlow(Update update) {
        return isUserInSubscriptionFlow(getUserId(update));
    }

    private boolean isUserInSubscriptionFlow(Integer userId) {
        return dialogStateStorage.hasDialogStarted(userId, getDialog(userId).getId());
    }

    private boolean validateProfile(SubscriptionModel model) {
        return true;
    }

    private Integer getUserId(Update update) {
        User user = AbilityUtils.getUser(update);
        return user.getId();
    }

    private DialogFlow getDialog(Integer userId) {
        return dialogsMap.get(dialogStateStorage.getActiveDialog(userId));
    }

    public void setFlatDialog(DialogFlow flatDialog) {
        this.flatDialog = flatDialog;
    }

    public void setRoomDialog(DialogFlow roomDialog) {
        this.roomDialog = roomDialog;
    }

    public void setDialogStateStorage(DialogStateStorage dialogStateStorage) {
        this.dialogStateStorage = dialogStateStorage;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}
