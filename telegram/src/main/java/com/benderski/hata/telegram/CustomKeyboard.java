package com.benderski.hata.telegram;

import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class CustomKeyboard {

    public static InlineKeyboardMarkup create() {
        InlineKeyboardMarkup keyboard = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> rowInline = new ArrayList<>();
        rowInline.add(0, new InlineKeyboardButton().setText("Create subscription").setCallbackData("create"));
        rowInline.add(1, new InlineKeyboardButton().setText("Cancel subscription").setCallbackData("cancel"));
        rows.add(0, rowInline);
        List<InlineKeyboardButton> row2 = new ArrayList<>();
        row2.add(0, new InlineKeyboardButton().setText("Hahaha").setCallbackData("haha"));
        rows.add(1, row2);
        keyboard.setKeyboard(rows);
        return keyboard;
    }
}
