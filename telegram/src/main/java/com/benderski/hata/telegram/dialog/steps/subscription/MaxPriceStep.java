package com.benderski.hata.telegram.dialog.steps.subscription;

import com.benderski.hata.telegram.dialog.fields.MaxPriceField;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class MaxPriceStep extends InputChatStep<Integer, MaxPriceField> {

    @Override
    public Set<ConstraintViolation<MaxPriceField>> validate(Integer arg) {
        return getValidator().validate(new MaxPriceField(arg));
    }

    @Override
    public String getMessage() {
        return "Укажите максимальную цену в долларах";
    }
}
