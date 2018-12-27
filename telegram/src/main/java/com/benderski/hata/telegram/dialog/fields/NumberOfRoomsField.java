package com.benderski.hata.telegram.dialog.fields;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class NumberOfRoomsField implements ModelField {

    @NotNull(message = "Совсем без комнат не получится")
    @Min(value = 1, message = "Количество комнат должно быть не меньше 1")
    @Max(value = 5, message = "А не занадта?")
    private Integer minNumberOfRooms;

    public NumberOfRoomsField(Integer minNumberOfRooms) {
        this.minNumberOfRooms = minNumberOfRooms;
    }

    @Override
    public Object getValue() {
        return minNumberOfRooms;
    }
}
