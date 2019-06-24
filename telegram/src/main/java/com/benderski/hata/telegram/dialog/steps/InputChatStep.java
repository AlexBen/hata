package com.benderski.hata.telegram.dialog.steps;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Collections;
import java.util.Set;

public abstract class InputChatStep<T, B, M> implements ChatStep {

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Put your validation to this method
     * @param arg data to validate
     * @return set of validation failures
     */
    protected abstract Set<ConstraintViolation<B>> validate(T arg);

    protected Set<String> postValidate(T arg, M model) {
        return Collections.emptySet();
    }

    protected abstract T parse(String text);

    protected abstract void setField(M model, T value);

    public StepResult performStepAction(M model, String value) {
        T parsed = parse(value);
        StepResult<B> result = new StepResult<B>(validate(parsed), postValidate(parsed, model));
        if (result.isSuccess()) {
            setField(model, parsed);
        }
        return result;
    }

    protected Validator getValidator() {
        return validator;
    }

    @Override
    public final boolean hasInput() {
        return true;
    }
}
