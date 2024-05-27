package fintech.web.api.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
@AllArgsConstructor
public class AmortizationPreviewResponse {

    @NonNull
    private LocalDate firstInvoiceDate;

    @NonNull
    private BigDecimal nominalApr;

    @NonNull
    private BigDecimal effectiveApr;

    @NonNull
    private List<AmortizationPayment> payments = newArrayList();

}
