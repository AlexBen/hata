package com.benderski.hata.telegram.dialog;

import java.util.concurrent.atomic.AtomicInteger;

public class DialogState {

    private final AtomicInteger flowPointer;
    private final String dialogFlowId;

    public DialogState(String dialogFlowId) {
        this.dialogFlowId = dialogFlowId;
        flowPointer = new AtomicInteger(0);
    }

    public int getFlowPointer() {
        return flowPointer.get();
    }

    public String getDialogFlowId() {
        return dialogFlowId;
    }

    public int incrementAndGet() {
        return flowPointer.incrementAndGet();
    }
}
