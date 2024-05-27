package fintech.scoring.values.spi;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import fintech.ScoringProperties;
import fintech.scoring.values.util.ScoringValuesScheme;
import lombok.SneakyThrows;

import java.util.Properties;

public interface ScoringValuesProvider {

    Properties provide(long clientId);

    @SneakyThrows
    default Properties flattenPojo(String prefix, Object object) {
        JavaPropsMapper mapper = new JavaPropsMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        ScoringProperties properties = new ScoringProperties(prefix);
        mapper.writeValue(properties, object, new ScoringValuesScheme());
        return properties;
    }
}
