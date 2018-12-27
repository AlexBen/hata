package com.benderski.hata.telegram.dialog;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DialogStateStorage {

    private Map<Integer, Map<String, Integer>> userToDialogPointerMap = new ConcurrentHashMap<>();

    @NonNull
    public Integer getOrInitForUserAndDialog(@NonNull Integer userId, @NonNull String dialog) {
        Map<String, Integer> dialogPointerMap = userToDialogPointerMap.get(userId);
        Optional<Integer> counter = Optional.ofNullable(dialogPointerMap.get(dialog));
        if(counter.isPresent()) {
            return counter.get();
        }
        dialogPointerMap.put(dialog, 0);
        userToDialogPointerMap.put(userId, dialogPointerMap);
        return dialogPointerMap.get(dialog);
    }

    public int incrementFor(@NonNull Integer userId, @NonNull String dialog) {
        Integer pointer = userToDialogPointerMap.get(userId).get(dialog);
        pointer += 1;
        userToDialogPointerMap.get(userId).put(dialog, pointer);
        return pointer;
    }
}
