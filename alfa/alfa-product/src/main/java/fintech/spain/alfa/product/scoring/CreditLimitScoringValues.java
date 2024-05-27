package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.scoring.values.spi.ScoringValuesProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreditLimitScoringValues implements ScoringValuesProvider {

    private final CreditLimitService creditLimitService;

    private static final String ALL_CREDIT_LIMITS = "all_credit_limits";

    @Override
    public Properties provide(long clientId) {
        List<CreditLimit> creditLimits = creditLimitService.findAll(clientId);

        List<BigDecimal> creditLimitValues = new ArrayList<>(creditLimits.size());

        creditLimits.forEach(cl -> creditLimitValues.add(cl.getLimit()));

        ScoringProperties properties = new ScoringProperties();
        properties.put(ALL_CREDIT_LIMITS, creditLimitValues);
        return properties;
    }
}
