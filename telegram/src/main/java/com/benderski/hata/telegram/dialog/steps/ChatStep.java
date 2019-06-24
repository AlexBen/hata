package com.benderski.hata.telegram.dialog.steps;

/**
 * Interface for chat step.
 * Chat step could be just a message from bot to user, or it could expect some input from user
 */
public interface ChatStep {
    String getMessage();

    /**
     * Indicates if this step expects input from user
     * @return true, if step expects input. False by default
     */
    default boolean hasInput() {
        return false;
    }
}
