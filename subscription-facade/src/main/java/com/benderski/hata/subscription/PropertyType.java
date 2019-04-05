package com.benderski.hata.subscription;

import java.io.Serializable;

public enum PropertyType implements Serializable {

    FLAT("Квартира"), ROOM("Комната"), HOUSE("Дом");

    private String type;

    PropertyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
