package fintech.spain.alfa.product.asnef.internal;

import fintech.BigDecimalUtils;
import org.apache.commons.lang3.StringUtils;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

import java.math.BigDecimal;
import java.util.Optional;

public class RpBigDecimalTypeHandler implements TypeHandler {

    @Override
    public Object parse(String text) throws TypeConversionException {
        return Optional.ofNullable(StringUtils.trimToNull(text)).map(t -> BigDecimalUtils.amount(text).movePointLeft(2)).orElse(null);
    }

    @Override
    public String format(Object value) {
        return Optional.ofNullable(value).map(v -> BigDecimalUtils.amount(((BigDecimal) value)).movePointRight(2).toString()).orElse(null);
    }

    @Override
    public Class<?> getType() {
        return BigDecimal.class;
    }
}
