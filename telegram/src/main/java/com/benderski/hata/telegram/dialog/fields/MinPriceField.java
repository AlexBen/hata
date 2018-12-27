package com.benderski.hata.telegram.dialog.fields;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class MinPriceField implements ModelField<Integer> {

    @NotNull(message = "Надо всё же указать минимальную цену")
    @Min(value = 1, message = "Бесплатный только сыр в мышеловке!")
    private Integer minPrice;

    public MinPriceField(Integer minPrice) {
        this.minPrice = minPrice;
    }

    @Override
    public Integer getValue() {
        return minPrice;
    }
}
