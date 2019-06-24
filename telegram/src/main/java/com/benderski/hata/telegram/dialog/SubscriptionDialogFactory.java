package com.benderski.hata.telegram.dialog;

import com.benderski.hata.subscription.PropertyType;
import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MaxPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinRoomNumberStep;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDialogFactory {

    public static DialogFlow createFlatFilterDialog() {
        List<ChatStep> steps = new ArrayList<>();
        steps.add(new MinRoomNumberStep());
        steps.add(new MinPriceStep());
        steps.add(new MaxPriceStep());
        steps.add(() -> "Фильтр сохранен. Чтобы начать получать новые объявления, введите команду /live");
        return new DialogFlow(steps, PropertyType.FLAT);
    }

    public static DialogFlow createRoomFilterDialog() {
        List<ChatStep> steps = new ArrayList<>();
        steps.add(new MinPriceStep());
        steps.add(new MaxPriceStep());
        steps.add(() -> "Фильтр сохранен. Чтобы начать получать новые объявления, введите команду /live");
        return new DialogFlow(steps, PropertyType.ROOM);
    }
}
