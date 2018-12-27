package com.benderski.hata.telegram.dialog.steps;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public abstract class InputChatStep<T, B> implements ChatStep {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    public abstract Set<ConstraintViolation<B>> validate(T arg);

    protected Validator getValidator() {
        return validator;
    }

    @Override
    public boolean hasInput() {
        return true;
    }
}
