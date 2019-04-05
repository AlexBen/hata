package com.benderski.hata.telegram.dialog;

import org.springframework.lang.NonNull;
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
        Map<String, Integer> chatToPointerMap = userToDialogPointerMap.computeIfAbsent(userId, id -> {
            Map<String, Integer> dialogPointerMap = new HashMap<>();
            dialogPointerMap.put(dialog, 0);
            return dialogPointerMap;
        });
        Integer integer = chatToPointerMap.get(dialog);
        if (integer < 0) {
            chatToPointerMap.put(dialog, 0);
        }
        return chatToPointerMap.get(dialog);
    }

    public boolean hasDialogStarted(@NonNull Integer userId, @NonNull String dialog) {
        if (!userToDialogPointerMap.containsKey(userId)) {
            return false;
        }
        return userToDialogPointerMap.get(userId).get(dialog) > -1;
    }

    public void finishDialog(@NonNull Integer userId, @NonNull String dialog) {
        userToDialogPointerMap.computeIfPresent(userId, (integer, stringIntegerMap) -> {
            Map<String, Integer> dialogPointerMap = new HashMap<>();
            dialogPointerMap.put(dialog, 0);
            return dialogPointerMap;
        });
    }

    public int incrementFor(@NonNull Integer userId, @NonNull String dialog) {
        Integer pointer = userToDialogPointerMap.get(userId).get(dialog);
        pointer += 1;
        userToDialogPointerMap.get(userId).put(dialog, pointer);
        return pointer;
    }

    public String getActiveDialog(Integer userId) {
        Map<String, Integer> dialogToStepMap = userToDialogPointerMap.get(userId);
        return dialogToStepMap.entrySet().stream()
                .filter(e -> e.getValue() > -1)
                .map(Map.Entry::getKey)
                .findFirst().orElse(null);
    }
}
