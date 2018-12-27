package com.benderski.hata.telegram.dialog.steps;

public abstract class IntegerInputChatStep<B> extends InputChatStep<Integer, B> {

    @Override
    public Integer parse(String text) {
        return Integer.parseInt(text);
    }
}