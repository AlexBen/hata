package com.benderski.hata.subscription;

import java.io.Serializable;
import java.util.Arrays;

public enum PropertyType implements Serializable {

    FLAT("Квартира"), ROOM("Комната"), HOUSE("Дом");

    private String type;

    PropertyType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static PropertyType fromType(String type) {
        return Arrays.stream(PropertyType.values())
                .filter(v -> v.getType().equals(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No property type found for " + type));
    }
}
