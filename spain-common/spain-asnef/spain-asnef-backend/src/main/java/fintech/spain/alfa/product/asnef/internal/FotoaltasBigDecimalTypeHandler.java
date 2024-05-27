package fintech.spain.alfa.product.asnef.internal;

import fintech.BigDecimalUtils;
import org.apache.commons.lang3.StringUtils;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

import java.math.BigDecimal;
import java.util.Optional;

class FotoaltasBigDecimalTypeHandler implements TypeHandler {

    @Override
    public Object parse(String text) throws TypeConversionException {
        return Optional.ofNullable(StringUtils.trimToNull(text)).map(t -> BigDecimalUtils.amount(StringUtils.replace(t, ",", "."))).orElse(null);
    }

    @Override
    public String format(Object value) {
        return Optional.ofNullable(value).map(v -> StringUtils.replace(BigDecimalUtils.amount((BigDecimal) value).toString(), ".", ",")).orElse(null);
    }

    @Override
    public Class<?> getType() {
        return BigDecimal.class;
    }
}
