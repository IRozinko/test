package fintech.strategy.bo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

public interface SupportedStrategiesResolver {
    List<Strategy> resolve();

    @Data
    @Accessors(chain = true)
    class Strategy {
        private String type;
        private String name;
        private List<String> calculationTypes;
    }
}
