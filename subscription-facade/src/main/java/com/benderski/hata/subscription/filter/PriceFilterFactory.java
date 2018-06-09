package com.benderski.hata.subscription.filter;

import java.math.BigDecimal;
import java.util.function.Predicate;

public class PriceFilterFactory  {

    public static Predicate<BigDecimal> construct(int min, int max) {
        return price ->
                price != null &&
                price.compareTo(BigDecimal.valueOf(min)) >= 0 &&
                price.compareTo(BigDecimal.valueOf(max)) <= 0;
    }

}
