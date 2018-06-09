package com.benderski.hata.onliner;

import com.benderski.hata.onliner.model.Price;
import org.springframework.lang.NonNull;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

public class PriceConverter {

    private static Logger LOGGER = Logger.getLogger(PriceConverter.class.getName());
    private static String USD = "USD";
    private static BigDecimal RATE = BigDecimal.valueOf(2);
    private static RoundingMode R_MODE = RoundingMode.CEILING;

    public static BigDecimal getPriceInUSD(Price price) {
        if (StringUtils.isEmpty(price.getCurrency()) || StringUtils.isEmpty(price.getAmount())) {
            return BigDecimal.ZERO;
        }
        return convert(price.getAmount(), price.getCurrency());
    }

    private static BigDecimal parse(@NonNull String amount) {
        try {
            return new BigDecimal(amount).setScale(2, R_MODE);
        } catch (NumberFormatException e) {
            LOGGER.warning("Cannot convert price amount " + amount);
            return BigDecimal.ZERO;
        }
    }

    private static BigDecimal convert(String amount, String currency) {
        BigDecimal price = parse(amount);
        return USD.equals(currency) ? price : convertToUSD(price);
    }

    private static BigDecimal convertToUSD(BigDecimal price) {
        return price.divide(RATE, R_MODE);
    }

}
