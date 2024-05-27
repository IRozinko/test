package fintech.spain.alfa.product.cms;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ApplicationModel {

    private LocalDate date;
    private LocalDate offerDate;
    private LocalDate offerMaturityDate;
    private LocalDate firstInvoiceDueDate;

    private String number;
    private String shortApproveCode;
    private String longApproveCode;
    private Long offeredPeriodCount;

    private BigDecimal creditLimit;
    private BigDecimal nominalApr;
    private BigDecimal effectiveApr;
    private BigDecimal offeredInterest;
    private BigDecimal offeredPrincipal;
    private BigDecimal offeredTotal;

}
