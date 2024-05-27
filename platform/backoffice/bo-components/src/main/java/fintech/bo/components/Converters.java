package fintech.bo.components;

import com.vaadin.data.converter.StringToBigDecimalConverter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Converters {

    public static StringToBigDecimalConverter stringToBigDecimalInputConverter() {
        return new CustomStringToBigDecimalConverter();
    }


    public static StringToBigDecimalConverter stringToBigDecimalInput4Converter() {
        return new CustomStringToBigDecimal4Converter();
    }

    static class CustomStringToBigDecimalConverter extends StringToBigDecimalConverter {
        public CustomStringToBigDecimalConverter() {
            super(BigDecimal.ZERO, "Invalid");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            DecimalFormat format = Formats.decimalInputFormat();
            format.setParseBigDecimal(true);
            return format;
        }
    }

    static class CustomStringToBigDecimal4Converter extends StringToBigDecimalConverter {
        public CustomStringToBigDecimal4Converter() {
            super(BigDecimal.ZERO, "Invalid");
        }

        @Override
        protected NumberFormat getFormat(Locale locale) {
            DecimalFormat format = Formats.decimalInput4Format();
            format.setParseBigDecimal(true);
            return format;
        }
    }

}
