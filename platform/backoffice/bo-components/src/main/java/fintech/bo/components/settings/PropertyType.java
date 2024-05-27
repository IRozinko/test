package fintech.bo.components.settings;

import fintech.bo.components.DateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public enum PropertyType {

    BOOLEAN, DATE, DATETIME, DECIMAL, NUMBER, TEXT, INNER, ARRAY;

    public static boolean isDate(String val) {
        try {
            LocalDate.parse(val);
            return true;
        } catch (DateTimeParseException e) {
            // not a date
            return false;
        }
    }

    public static boolean isDateTime(String val) {
        try {
            LocalDateTime.parse(val);
            return true;
        } catch (DateTimeParseException e) {
            // not a date
            return false;
        }
    }

}
