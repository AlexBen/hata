package com.benderski.hata.telegram.dialog;

import com.benderski.hata.telegram.dialog.steps.ChatStep;
import org.springframework.lang.Nullable;

import java.util.List;

public class DialogFlow {

    private final String id;
    private final List<ChatStep> steps;

    public DialogFlow(String id, List<ChatStep> steps) {
        this.id = id;
        this.steps = steps;
    }

    @Nullable
    public ChatStep getByIndex(int index) {

        if (index >= steps.size()) return null;
        return steps.get(index);
    }

    public String getId() {
        return id;
    }

    public int getNumberOfSteps() {
        return steps.size();
    }
}
