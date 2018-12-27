package com.benderski.hata.telegram.service;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.subscription.SubscriptionService;
import com.benderski.hata.telegram.dialog.DialogFlow;
import com.benderski.hata.telegram.dialog.DialogStateStorage;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.abilitybots.api.objects.MessageContext;
import org.telegram.abilitybots.api.util.AbilityUtils;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;

import javax.validation.ConstraintViolation;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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
        final Integer userId = ctx.user().id();
        Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, subscriptionDialog.getId());
        ChatStep step;
        do {
            step = subscriptionDialog.getByIndex(pointer);
            if (step == null) {
                dialogStateStorage.finishDialog(userId, subscriptionDialog.getId());
                return;
            }
            sendMessage.accept(new SendMessage(ctx.chatId(), step.getMessage()));
            pointer = dialogStateStorage.incrementFor(userId, subscriptionDialog.getId());
        } while ((!step.hasInput()));
    }

    public void processShowSubscription(MessageContext ctx, Consumer<SendMessage> sendMessage) {
        int userId = ctx.user().id();
        SubscriptionModel profile = subscriptionService.getProfile(userId);
        final String message = Optional.ofNullable(profile).map(Objects::toString)
                .orElse("Ваш фильтр не настроен. Используйте команду /start");
        try {
            sendMessage.accept(new SendMessage(ctx.chatId(), message));
        } catch (Exception e) {
            LOGGER.error(e.getLocalizedMessage(), e);
        }
    }

    public void performNextStep(Update update, Consumer<SendMessage> sendMessage) {
        final Integer userId = getUserId(update);
        Integer pointer = dialogStateStorage.getOrInitForUserAndDialog(userId, subscriptionDialog.getId());
        InputChatStep step = (InputChatStep) subscriptionDialog.getByIndex(pointer);
        String text = update.getMessage().getText();
        Object arg = step.parse(text);
        Set<ConstraintViolation> validationResult = step.validate(arg);
        if (validationResult.isEmpty()) {
            dialogStateStorage.incrementFor(userId, subscriptionDialog.getId());
            return;

        }
        String message = validationResult.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        sendMessage.accept(new SendMessage(update.getMessage().getChatId(), message));
    }

    public boolean isUserInSubscriptionFlow(Update update) {
        return dialogStateStorage.hasDialogStarted(getUserId(update), subscriptionDialog.getId());
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
