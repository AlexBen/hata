package com.benderski.hata.telegram.dialog.fields;

import javax.validation.constraints.Min;

public class MaxPriceField implements ModelField<Integer> {

    @Min(value = 50, message = "Ну совсем бесплатно не выйдет.")
    private Integer maxPrice;

    public MaxPriceField(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    @Override
    public Integer getValue() {
        return maxPrice;
    }
}
