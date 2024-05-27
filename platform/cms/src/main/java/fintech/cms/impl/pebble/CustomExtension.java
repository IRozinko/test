package fintech.cms.impl.pebble;

import com.google.common.collect.ImmutableMap;
import com.mitchellbosecke.pebble.extension.AbstractExtension;
import com.mitchellbosecke.pebble.extension.Filter;

import java.util.Map;

public class CustomExtension extends AbstractExtension {

    @Override
    public Map<String, Filter> getFilters() {
        return ImmutableMap.of("ldate", new LocalDateFilter(), "ldatetime", new LocalDateTimeFilter());
    }
}
