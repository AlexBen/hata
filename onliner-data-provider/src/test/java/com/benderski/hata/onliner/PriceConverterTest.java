package com.benderski.hata.onliner;

import com.benderski.hata.onliner.model.Price;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PriceConverterTest {

    @Test
    void getPriceInUSD() {
        assertTrue(areEquals(BigDecimal.valueOf(300), getFor("300", "USD")));

        assertTrue(areEquals(BigDecimal.valueOf(150), getFor("300", "BYN")));
        assertTrue(areEquals(BigDecimal.ZERO, getFor("", "")));
        assertTrue(areEquals(BigDecimal.ZERO, getFor("1", "")));
        assertTrue(areEquals(BigDecimal.ZERO, getFor("", "1")));
        assertTrue(areEquals(BigDecimal.ZERO, getFor("", "")));

    }

    private boolean areEquals(BigDecimal first, BigDecimal second) {
        return first.compareTo(second) == 0;
    }

    private BigDecimal getFor(String amount, String currency) {
        return PriceConverter.getPriceInUSD(new Price(amount, currency));
    }

}