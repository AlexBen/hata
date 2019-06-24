package com.benderski.hata.telegram.dialog;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Service
public class DialogStateStorage {

    private Map<Integer, DialogState> userDialogState = new ConcurrentHashMap<>();

    @NonNull
    public Integer getOrInitForUserAndDialog(@NonNull Integer userId, @NonNull String dialog) {
        DialogState dialogState = userDialogState.compute(userId, (integer, current) -> {
            if (current == null || !current.getDialogFlowId().equals(dialog)) return new DialogState(dialog);
            return current;
        });
        return dialogState.getFlowPointer();
    }

    /**
     * Verifies if dialog for given user exists and it's equals to given
     *
     * @param userId         user id
     * @param dialogSupplier returns dilaogId
     * @return true if dialog with given name exists for given user
     */
    public boolean hasDialogStarted(@NonNull Integer userId, @NonNull Supplier<String> dialogSupplier) {
        DialogState dialogState = userDialogState.get(userId);
        return dialogState != null && dialogState.getDialogFlowId().equals(dialogSupplier.get());
    }

    public void finishDialog(@NonNull Integer userId) {
        userDialogState.remove(userId);
    }

    /**
     * Increments pointer in dialog flow for user
     *
     * @param userId
     * @return
     */
    public int incrementFor(@NonNull Integer userId) {
        return Optional.ofNullable(userDialogState.get(userId))
                .map(DialogState::incrementAndGet)
                .orElse(-1);
    }

    public String getActiveDialog(Integer userId) {
        return Optional.ofNullable(userDialogState.get(userId))
                .map(DialogState::getDialogFlowId)
                .orElse(null);
    }
}
