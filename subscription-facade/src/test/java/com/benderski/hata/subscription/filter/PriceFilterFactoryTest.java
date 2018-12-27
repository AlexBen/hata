package com.benderski.hata.subscription.filter;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

class PriceFilterFactoryTest {
    @Test
    void construct() {
        Predicate<Integer> filter = new PriceFilterFactory().construct(100, 300);
        assertTrue(filter.test((200)));
        assertTrue(filter.test((100)));
        assertTrue(filter.test((300)));
        assertFalse(filter.test((99)));
        assertFalse(filter.test(null));

        //Predicate<BigDecimal> filter2 = PriceFilterFactory.construct(0, 300);
    }



}