package fintech.cms.impl.pebble;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalDateFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public LocalDateFilter() {
        argumentNames.add("format");
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        LocalDate date = (LocalDate) input;
        String format = (String) args.get("format");
        if (format == null) {
            format = "dd.MM.yyyy";
        }
        String formatted = date.format(DateTimeFormatter.ofPattern(format));
        return new SafeString(formatted);
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }
}
