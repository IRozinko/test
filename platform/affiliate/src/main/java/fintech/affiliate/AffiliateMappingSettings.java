package fintech.affiliate;

import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Value
public class AffiliateMappingSettings {

    List<FieldMappingConfig> fieldMappingConfigs;

    @Value
    public static class FieldMappingConfig {

        String field;

        List<ValueMappingConfig> valueMappingConfigs;

        public Optional<String> get(String from) {
            return valueMappingConfigs.stream()
                .filter(config -> StringUtils.equalsIgnoreCase(config.getFrom(), from))
                .map(ValueMappingConfig::getTo)
                .findFirst();
        }

        @Value
        public static class ValueMappingConfig {

            String from;

            String to;
        }
    }
}
