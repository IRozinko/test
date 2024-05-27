package fintech.bo.components;

import com.vaadin.ui.renderers.NumberRenderer;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Formats {

    public static String DECIMAL_FORMAT = "#,##0.00##";
    public static String DECIMAL_INPUT_FORMAT = "#,##0.##";
    public static String DECIMAL_INPUT_4_FORMAT = "#,##0.0000";
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String LONG_DATE_FORMAT = "EE, MMM dd yyyy";
    public static String LONG_DATETIME_FORMAT = "EE, MMM dd yyyy, HH:mm";

    public static DecimalFormat decimalFormat() {
        return new DecimalFormat(DECIMAL_FORMAT);
    }

    public static DecimalFormat decimalInputFormat() {
        return new DecimalFormat(DECIMAL_INPUT_FORMAT);
    }

    public static DecimalFormat decimalInput4Format() {
        return new DecimalFormat(DECIMAL_INPUT_4_FORMAT);
    }

    public static NumberRenderer decimalRenderer() {
        return new NumberRenderer(decimalFormat());
    }

    public static DateTimeFormatter dateFormatter() {
        return DateTimeFormatter.ofPattern(DATE_FORMAT);
    }

    public static DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    }

    public static String formatDateTime(TemporalAccessor ta) {
        if (ta == null) {
            return null;
        }
        return dateTimeFormatter().format(ta);
    }

    public static String formatDate(TemporalAccessor ta) {
        if (ta == null) {
            return null;
        }
        return dateFormatter().format(ta);
    }
}
