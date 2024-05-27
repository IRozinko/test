package fintech.cms.impl.pebble;

import com.mitchellbosecke.pebble.extension.Filter;
import com.mitchellbosecke.pebble.extension.escaper.SafeString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocalDateTimeFilter implements Filter {

    private final List<String> argumentNames = new ArrayList<>();

    public LocalDateTimeFilter() {
        argumentNames.add("format");
    }

    @Override
    public Object apply(Object input, Map<String, Object> args) {
        if (input == null) {
            return null;
        }
        LocalDateTime date = (LocalDateTime) input;
        String format = (String) args.get("format");
        if (format == null) {
            format = "dd.MM.yyyy HH:mm:ss";
        }
        String formatted = date.format(DateTimeFormatter.ofPattern(format));
        return new SafeString(formatted);
    }

    @Override
    public List<String> getArgumentNames() {
        return argumentNames;
    }
}
