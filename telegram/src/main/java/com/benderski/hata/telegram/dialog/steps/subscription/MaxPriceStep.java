package com.benderski.hata.telegram.dialog.steps.subscription;

import com.benderski.hata.subscription.SubscriptionModel;
import com.benderski.hata.telegram.dialog.fields.MaxPriceField;
import com.benderski.hata.telegram.dialog.steps.IntegerInputChatStep;

import javax.validation.ConstraintViolation;
import java.util.HashSet;
import java.util.Set;

public class MaxPriceStep extends IntegerInputChatStep<MaxPriceField, SubscriptionModel> {

    @Override
    protected Set<ConstraintViolation<MaxPriceField>> validate(Integer arg) {
        return getValidator().validate(new MaxPriceField(arg));
    }

    protected Set<String> postValidate(Integer maxPrice, SubscriptionModel model) {
        Set<String> set = new HashSet<>();
        if (model.getMinPrice() >= maxPrice) {
            set.add("Максимальная цена должна быть больше минимальной");
        }
        return set;
    }

    @Override
    protected void setField(SubscriptionModel model, Integer value) {
        model.setMaxPrice(value);
    }

    @Override
    public String getMessage() {
        return "Укажите максимальную цену в долларах";
    }
}
