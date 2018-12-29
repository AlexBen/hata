package com.benderski.hata.telegram.dialog.steps;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Validation results wrapper. Immutable
 */
public class StepResult<B> {
    private final Set<ConstraintViolation<B>> validationResult;
    private final Set<String> postValidateResult;

    StepResult(Set<ConstraintViolation<B>> validationResult, Set<String> postValidateResult) {
        this.validationResult = validationResult;
        this.postValidateResult = postValidateResult;
    }

    public boolean isSuccess() {
        return validationResult.isEmpty() && postValidateResult.isEmpty();
    }

    public String getMessage() {
        return Stream.concat(validationResult.stream()
                .map(ConstraintViolation::getMessage),
                postValidateResult.stream())
                .collect(Collectors.joining(", "));
    }
}
