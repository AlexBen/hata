package com.benderski.hata.telegram.dialog.steps;

public interface ChatStep {
    String getMessage();
    default boolean hasInput() {
        return false;
    }
}
