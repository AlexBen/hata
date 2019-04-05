package com.benderski.hata.telegram.dialog;

import com.benderski.hata.telegram.dialog.steps.ChatStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MaxPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinPriceStep;
import com.benderski.hata.telegram.dialog.steps.subscription.MinRoomNumberStep;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionDialogFactory {

    private static final String FLAT_DIALOG_ID = "FlatDialog.v.1";
    private static final String ROOM_DIALOG_ID = "RoomDialog.v.1";

    public static DialogFlow createFlatFilterDialog() {
        List<ChatStep> steps = new ArrayList<>();
        steps.add(() -> "Давайте создадим фильтр поиска квартир");
        steps.add(new MinRoomNumberStep());
        steps.add(new MinPriceStep());
        steps.add(new MaxPriceStep());
        steps.add(() -> "Фильтр сохранен. Чтобы начать получать новые объявления, введите команду /live");
        return new DialogFlow(FLAT_DIALOG_ID, steps);
    }

    public static DialogFlow createRoomFilterDialog() {
        List<ChatStep> steps = new ArrayList<>();
        steps.add(() -> "Давайте создадим фильтр поиска комнат");
        steps.add(new MinPriceStep());
        steps.add(new MaxPriceStep());
        steps.add(() -> "Фильтр сохранен. Чтобы начать получать новые объявления, введите команду /live");
        return new DialogFlow(ROOM_DIALOG_ID, steps);
    }
}
