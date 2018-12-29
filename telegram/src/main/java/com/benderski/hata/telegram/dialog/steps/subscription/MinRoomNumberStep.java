package com.benderski.hata.telegram.dialog.steps.subscription;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.telegram.dialog.fields.NumberOfRoomsField;
import com.benderski.hata.telegram.dialog.steps.IntegerInputChatStep;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class MinRoomNumberStep extends IntegerInputChatStep<NumberOfRoomsField, SubscriptionModel> {

    @Override
    protected Set<ConstraintViolation<NumberOfRoomsField>> validate(Integer arg) {
        return getValidator().validate(new NumberOfRoomsField(arg));
    }

    @Override
    protected void setField(SubscriptionModel model, Integer value) {
        model.setMinNumberOfRooms(value);
    }

    @Override
    public String getMessage() {
        return "Не меньше скольки комнат хотите? (от 1 до 5)";
    }
}
