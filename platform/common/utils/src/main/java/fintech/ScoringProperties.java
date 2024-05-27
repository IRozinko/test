package fintech;

import java.util.Properties;

public class ScoringProperties extends Properties {

    private final String prefix;

    public ScoringProperties() {
        super();
        this.prefix = "";
    }

    public ScoringProperties(String prefix) {
        super();
        this.prefix = prefix + "_";
    }

    @Override
    public synchronized Object put(Object key, Object value) {
        Object resolvedValue = value == null ? "null" : value;
        return super.put(formatKey(key.toString()), resolvedValue);
    }

    private String formatKey(String key) {
        return prefix + key.toLowerCase();
    }
}
