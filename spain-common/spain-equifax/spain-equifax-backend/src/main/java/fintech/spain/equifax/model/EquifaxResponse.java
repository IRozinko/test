package fintech.spain.equifax.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static fintech.BigDecimalUtils.amount;

@Data
@Accessors(chain = true)
public class EquifaxResponse {

    private Long id;
    private Long clientId;
    private Long applicationId;
    private EquifaxStatus status;
    private String error;
    private String requestBody;
    private String responseBody;
    private Long totalNumberOfOperations = 0L;
    private Long numberOfConsumerCreditOperations = 0L;
    private Long numberOfMortgageOperations = 0L;
    private Long numberOfPersonalLoanOperations = 0L;
    private Long numberOfCreditCardOperations = 0L;
    private Long numberOfTelcoOperations = 0L;
    private Long totalNumberOfOtherUnpaid = 0L;
    private BigDecimal totalUnpaidBalance = amount(0);
    private BigDecimal unpaidBalanceOwnEntity = amount(0);
    private BigDecimal unpaidBalanceOfOther = amount(0);
    private BigDecimal unpaidBalanceOfConsumerCredit = amount(0);
    private BigDecimal unpaidBalanceOfMortgage = amount(0);
    private BigDecimal unpaidBalanceOfPersonalLoan = amount(0);
    private BigDecimal unpaidBalanceOfCreditCard = amount(0);
    private BigDecimal unpaidBalanceOfTelco = amount(0);
    private BigDecimal unpaidBalanceOfOtherProducts = amount(0);
    private BigDecimal worstUnpaidBalance = amount(0);
    private String worstSituationCode;
    private Long numberOfDaysOfWorstSituation = 0L;
    private Long numberOfCreditors = 0L;
    private Long delincuencyDays = 0L;
    private String scoringCategory;
    private String riskScore;
    private LocalDateTime createdAt;
}
