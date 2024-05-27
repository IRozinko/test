package fintech.spain.alfa.strategy.fee;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class FeeStrategyProperties {

    private List<FeeOption> fees = new ArrayList<>();

    @Data
    @Accessors(chain = true)
    public static class FeeOption {
        private BigDecimal oneTimeFeeRate;
        private String company;
    }
}
