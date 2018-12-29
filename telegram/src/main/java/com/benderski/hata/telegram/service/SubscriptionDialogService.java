package com.benderski.hata.telegram.service;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.subscription.SubscriptionService;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.DialogStateStorage;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;
import com.benderski.hata.telegram.dialog.steps.StepResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class SubscriptionDialogService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubscriptionDialogService.class);

    @Autowired
    @Qualifier("createSubscriptionDialog")
    private DialogFlow subscriptionDialog;

    @Autowired
    private DialogStateStorage dialogStateStorage;

    @Autowired
    private SubscriptionService subscriptionService;


    public void processSubscriptionStart(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        final Integer userId = ctx.user().getId();
        final Long chatId = ctx.chatId();
        processNext(userId, chatId, sendMessage);
    }

    private void processNext(final Integer userId, final Long chatId, Consumer<SendMessage> sendMessage){
        ChatStep step = getStep(userId, subscriptionDialog.getId());
        while (step != null) {
            sendMessage.accept(new SendMessage(chatId, step.getMessage()));
            if (step.hasInput()) {
                //wait for the input
                return;
            }
            dialogStateStorage.incrementFor(userId, subscriptionDialog.getId());
            step = getStep(userId, subscriptionDialog.getId());
        }
    }

    public void performInputStep(Update update, Consumer<SendMessage> sendMessage) {
        final Integer userId = getUserId(update);
        InputChatStep<?, ?, SubscriptionModel> step = (InputChatStep) getStep(userId, subscriptionDialog.getId());
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
        dialogStateStorage.incrementFor(userId, subscriptionDialog.getId());
        processNext(userId, update.getMessage().getChatId(), sendMessage);
    }

    public void stopSubscription(MessageContext messageContext) {
        final Integer userId = messageContext.user().getId();
        subscriptionService.unsubscribe(userId);
    }

    @Nullable
    private ChatStep getStep(Integer userId, String dialogId) {
        Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, dialogId);
        ChatStep step = subscriptionDialog.getByIndex(pointer);
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
                .orElse("Ваш фильтр не настроен. Используйте команду /start");
        try {
            sendMessage.accept(new SendMessage(ctx.chatId(), message));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public void goLive(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        int userId = ctx.user().getId();
        subscriptionService.startSubscription(userId, s -> sendMessage.accept(new SendMessage(ctx.chatId(), s)));
    }

    public boolean notCommand(Update update) {
        return !update.getMessage().getText().startsWith("/");
    }

    public boolean isUserInSubscriptionFlow(Update update) {
        return dialogStateStorage.hasDialogStarted(getUserId(update), subscriptionDialog.getId());
    }

    private boolean validateProfile(SubscriptionModel model) {
        return true;
    }

    private Integer getUserId(Update update) {
        User user = AbilityUtils.getUser(update);
        return user.getId();
    }

    public void setSubscriptionDialog(DialogFlow subscriptionDialog) {
        this.subscriptionDialog = subscriptionDialog;
    }

    public void setDialogStateStorage(DialogStateStorage dialogStateStorage) {
        this.dialogStateStorage = dialogStateStorage;
    }

    public void setSubscriptionService(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }
}