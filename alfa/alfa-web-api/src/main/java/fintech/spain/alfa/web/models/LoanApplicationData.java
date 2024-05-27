package fintech.spain.alfa.web.models;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class LoanApplicationData {

    private Long id;

    private BigDecimal principal;
    private BigDecimal interest;
    private BigDecimal total;

    private Long termInDays;
    private LocalDate startDate;
    private LocalDate maturityDate;

    // TAE
    private BigDecimal aprPercent;
    // TIN mensual
    private BigDecimal monthlyInterestRatePercent;

    private String standardInformationPdfFileHashId;
    private BigDecimal nominalApr;

    private List<UpsellOfferData> upsellOffers = Lists.newArrayList();
}
