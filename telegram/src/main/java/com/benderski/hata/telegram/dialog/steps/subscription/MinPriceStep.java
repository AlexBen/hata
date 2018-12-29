package com.benderski.hata.telegram.dialog.steps.subscription;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.telegram.dialog.fields.MinPriceField;
import com.benderski.hata.telegram.dialog.steps.IntegerInputChatStep;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class MinPriceStep extends IntegerInputChatStep<MinPriceField, SubscriptionModel> {

    @Override
    protected Set<ConstraintViolation<MinPriceField>> validate(Integer arg) {
        return getValidator().validate(new MinPriceField(arg));
    }

    @Override
    protected void setField(SubscriptionModel model, Integer value) {
        model.setMinPrice(value);
    }

    @Override
    public String getMessage() {
        return "Укажите минимальную цену в долларах";
    }
}
