package fintech.spain.alfa.product.cms;

import com.google.common.annotations.VisibleForTesting;
import fintech.strategy.model.ExtensionOffer;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class LoanModel {

    private static final String EXTENSION_KEY_TEMPLATE = "extension%d%s";

    private String number;
    private LocalDate issueDate;
    private LocalDate maturityDate;
    private LocalDate paymentDueDate;
    private BigDecimal principal = amount(0);
    private BigDecimal totalDue = amount(0);
    private BigDecimal totalOutstanding = amount(0);
    private BigDecimal feeOutstanding = amount(0);
    private BigDecimal interestDue = amount(0);
    private BigDecimal penaltyDue = amount(0);
    private BigDecimal principalDue = amount(0);

    private BigDecimal prePaymentInterestDue = amount(0);
    private BigDecimal prePaymentTotalDue = amount(0);

    @Deprecated
    private ExtensionModel extension30Days;
    @Deprecated
    private ExtensionModel extension45Days;

    private Map<String, ExtensionModel> extensions;

    public LoanModel setExtensions(List<ExtensionOffer> extensions) {
        this.extensions = extensions.stream()
            .map(ex -> new ExtensionModel(extensionModelKey(ex.getPeriodUnit(), ex.getPeriodCount()), ex.getPrice()))
            .collect(Collectors.toMap(ExtensionModel::getKey, Function.identity()));

        extension30Days = this.extensions.get(extensionModelKey(ChronoUnit.DAYS, 30));
        extension45Days = this.extensions.get(extensionModelKey(ChronoUnit.DAYS, 45));
        return this;
    }

    @VisibleForTesting
    protected String extensionModelKey(ChronoUnit unit, long count) {
        return String.format(EXTENSION_KEY_TEMPLATE, count, unit);
    }

}
