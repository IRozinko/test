
package fintech.instantor.json.insight;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "repaymentLoanRatio3M",
    "sumRepayments12M",
    "repaymentLoanRatioTotal",
    "sumRepaymentsTotal",
    "sumRepayments1W",
    "repaymentLoanRatio1W",
    "sumLoansTotal",
    "sumLoans3M",
    "sumLoans1W",
    "version",
    "sumRepayments3M",
    "repaymentLoanRatio6M",
    "sumLoans1M",
    "repaymentLoanRatio12M",
    "repaymentLoanRatio1M",
    "sumLoans12M",
    "sumRepayments1M",
    "sumRepayments6M",
    "sumLoans6M"
})
@Data
public class Loans {

    private BigDecimal repaymentLoanRatio3M;
    private BigDecimal sumRepayments12M;
    private BigDecimal repaymentLoanRatioTotal;
    private BigDecimal sumRepaymentsTotal;
    private BigDecimal sumRepayments1W;
    private BigDecimal repaymentLoanRatio1W;
    private BigDecimal sumLoansTotal;
    private BigDecimal sumLoans3M;
    private BigDecimal sumLoans1W;
    private String version;
    private BigDecimal sumRepayments3M;
    private BigDecimal repaymentLoanRatio6M;
    private BigDecimal sumLoans1M;
    private BigDecimal repaymentLoanRatio12M;
    private BigDecimal repaymentLoanRatio1M;
    private BigDecimal sumLoans12M;
    private BigDecimal sumRepayments1M;
    private BigDecimal sumRepayments6M;
    private BigDecimal sumLoans6M;
}
