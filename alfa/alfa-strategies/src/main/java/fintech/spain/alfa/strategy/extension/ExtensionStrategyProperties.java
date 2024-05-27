package fintech.spain.alfa.strategy.extension;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ExtensionStrategyProperties {

    private List<ExtensionOption> extensions = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    public static class ExtensionOption {
        private BigDecimal rate;
        private long term;
    }
}
