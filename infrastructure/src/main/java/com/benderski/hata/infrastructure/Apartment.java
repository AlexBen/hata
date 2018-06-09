package com.benderski.hata.infrastructure;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Date;

public interface Apartment {

    @NonNull Date getCreatedAt();
    @NonNull Date lastUpdateAt();
    @Nullable
    BigDecimal getPriceInUSD();

    @NonNull String getLink();
}
