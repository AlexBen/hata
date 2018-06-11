package com.benderski.hata.subscription.filter;

import java.util.function.Predicate;

public abstract class MinMaxFilterFactory<L extends Number> {

    public Predicate<L> construct(int min, int max) {
        return valueToMatch ->
                valueToMatch != null
                && valueToMatch.doubleValue() - min >= 0
                && valueToMatch.doubleValue() - max <= 0;
    }


}
