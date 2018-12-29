package com.benderski.hata.telegram.dialog.steps;

public abstract class IntegerInputChatStep<B, M> extends InputChatStep<Integer, B, M> {

    @Override
    public Integer parse(String text) {
        return Integer.parseInt(text);
    }
}