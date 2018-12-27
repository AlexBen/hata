package com.benderski.hata.telegram.dialog.steps.subscription;

import com.benderski.hata.telegram.dialog.fields.MinPriceField;
import com.benderski.hata.telegram.dialog.steps.InputChatStep;

import javax.validation.ConstraintViolation;
import java.util.Set;

public class MinPriceStep extends InputChatStep<Integer, MinPriceField> {

    @Override
    public Set<ConstraintViolation<MinPriceField>> validate(Integer arg) {
        return getValidator().validate(new MinPriceField(arg));
    }

    @Override
    public String getMessage() {
        return "Укажите минимальную цену в долларах";
    }
}
