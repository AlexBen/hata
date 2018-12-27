package com.benderski.hata.telegram.dialog;

import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MaxPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinRoomNumberStep;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDialogFactory {

    private static final String SUBSCRIPTION_DIALOG_ID = "SubscriptionDialog.v.1";

    public static DialogFlow createSubscriptionDialog() {
        List<ChatStep> steps = new ArrayList<>();
        steps.add(() -> "Давайте создадим фильтр поиска объявлений");
        steps.add(new MinRoomNumberStep());
        steps.add(new MinPriceStep());
        steps.add(new MaxPriceStep());
        steps.add(() -> "Фильтр сохранен. Чтобы начать получать новые объявления, введите команду /live");
        return new DialogFlow(SUBSCRIPTION_DIALOG_ID, steps);
    }
}
