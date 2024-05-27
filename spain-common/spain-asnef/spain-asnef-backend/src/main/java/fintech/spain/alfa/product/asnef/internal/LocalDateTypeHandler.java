package fintech.spain.alfa.product.asnef.internal;

import org.apache.commons.lang3.StringUtils;
import org.beanio.types.TypeConversionException;
import org.beanio.types.TypeHandler;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class LocalDateTypeHandler implements TypeHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Override
    public Object parse(String text) throws TypeConversionException {
        return Optional.ofNullable(StringUtils.trimToNull(text)).map(t -> LocalDate.parse(t, FORMATTER)).orElse(null);
    }

    @Override
    public String format(Object value) {
        return Optional.ofNullable(value).map(v -> FORMATTER.format((LocalDate) v)).orElse(null);
    }

    @Override
    public Class<?> getType() {
        return LocalDate.class;
    }
}
