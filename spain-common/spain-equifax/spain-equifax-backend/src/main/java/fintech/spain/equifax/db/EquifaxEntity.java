package fintech.spain.equifax.db;

import fintech.db.BaseEntity;
import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Index;
import javax.persistence.Table;
import java.math.BigDecimal;

import static fintech.BigDecimalUtils.amount;

@Getter
@Setter
@ToString(callSuper = true)
@Entity
@Table(name = "equifax", schema = Entities.SCHEMA, indexes = {
    @Index(columnList = "clientId", name = "idx_equifax_client_id"),
})
@DynamicUpdate
@NoArgsConstructor
public class EquifaxEntity extends BaseEntity {

    private Long clientId;
    private Long applicationId;
    private String documentNumber;

    private String requestBody;
    private String responseBody;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquifaxStatus status;

    private String error;

    @Column(nullable = false)
    private Long totalNumberOfOperations = 0L;
    @Column(nullable = false)
    private Long numberOfConsumerCreditOperations = 0L;
    @Column(nullable = false)
    private Long numberOfMortgageOperations = 0L;
    @Column(nullable = false)
    private Long numberOfPersonalLoanOperations = 0L;
    @Column(nullable = false)
    private Long numberOfCreditCardOperations = 0L;
    @Column(nullable = false)
    private Long numberOfTelcoOperations = 0L;
    @Column(nullable = false)
    private Long totalNumberOfOtherUnpaid = 0L;
    @Column(nullable = false)
    private BigDecimal totalUnpaidBalance = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOwnEntity = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfOther = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfConsumerCredit = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfMortgage = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfPersonalLoan = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfCreditCard = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfTelco = amount(0);
    @Column(nullable = false)
    private BigDecimal unpaidBalanceOfOtherProducts = amount(0);
    @Column(nullable = false)
    private BigDecimal worstUnpaidBalance = amount(0);
    private String worstSituationCode;
    @Column(nullable = false)
    private Long numberOfDaysOfWorstSituation = 0L;
    @Column(nullable = false)
    private Long numberOfCreditors = 0L;
    @Column(nullable = false)
    private Long delincuencyDays = 0L;
    @Column
    private String scoringCategory;
    @Column
    private String riskScore;


    public EquifaxEntity(EquifaxResponse val) {
        this.clientId = val.getClientId();
        this.applicationId = val.getApplicationId();
        this.status = val.getStatus();
        this.error = val.getError();
        this.requestBody = val.getRequestBody();
        this.responseBody = val.getResponseBody();
        this.totalNumberOfOperations = val.getTotalNumberOfOperations();
        this.numberOfConsumerCreditOperations = val.getNumberOfConsumerCreditOperations();
        this.numberOfMortgageOperations = val.getNumberOfMortgageOperations();
        this.numberOfPersonalLoanOperations = val.getNumberOfPersonalLoanOperations();
        this.numberOfCreditCardOperations = val.getNumberOfCreditCardOperations();
        this.numberOfTelcoOperations = val.getNumberOfTelcoOperations();
        this.totalNumberOfOtherUnpaid = val.getTotalNumberOfOtherUnpaid();
        this.totalUnpaidBalance = val.getTotalUnpaidBalance();
        this.unpaidBalanceOwnEntity = val.getUnpaidBalanceOwnEntity();
        this.unpaidBalanceOfOther = val.getUnpaidBalanceOfOther();
        this.unpaidBalanceOfConsumerCredit = val.getUnpaidBalanceOfConsumerCredit();
        this.unpaidBalanceOfMortgage = val.getUnpaidBalanceOfMortgage();
        this.unpaidBalanceOfPersonalLoan = val.getUnpaidBalanceOfPersonalLoan();
        this.unpaidBalanceOfCreditCard = val.getUnpaidBalanceOfCreditCard();
        this.unpaidBalanceOfTelco = val.getUnpaidBalanceOfTelco();
        this.unpaidBalanceOfOtherProducts = val.getUnpaidBalanceOfOtherProducts();
        this.worstUnpaidBalance = val.getWorstUnpaidBalance();
        this.worstSituationCode = val.getWorstSituationCode();
        this.numberOfDaysOfWorstSituation = val.getNumberOfDaysOfWorstSituation();
        this.numberOfCreditors = val.getNumberOfCreditors();
        this.delincuencyDays = val.getDelincuencyDays();
        this.scoringCategory = val.getScoringCategory();
        this.riskScore = val.getRiskScore();
    }

    public EquifaxResponse toValueObject() {
        EquifaxResponse val = new EquifaxResponse();
        val.setId(this.id);
        val.setClientId(this.clientId);
        val.setApplicationId(this.applicationId);
        val.setStatus(this.status);
        val.setError(this.error);
        val.setRequestBody(this.requestBody);
        val.setResponseBody(this.responseBody);
        val.setTotalNumberOfOperations(this.totalNumberOfOperations);
        val.setNumberOfConsumerCreditOperations(this.numberOfConsumerCreditOperations);
        val.setNumberOfMortgageOperations(this.numberOfMortgageOperations);
        val.setNumberOfPersonalLoanOperations(this.numberOfPersonalLoanOperations);
        val.setNumberOfCreditCardOperations(this.numberOfCreditCardOperations);
        val.setNumberOfTelcoOperations(this.numberOfTelcoOperations);
        val.setTotalNumberOfOtherUnpaid(this.totalNumberOfOtherUnpaid);
        val.setTotalUnpaidBalance(this.totalUnpaidBalance);
        val.setUnpaidBalanceOwnEntity(this.unpaidBalanceOwnEntity);
        val.setUnpaidBalanceOfOther(this.unpaidBalanceOfOther);
        val.setUnpaidBalanceOfConsumerCredit(this.unpaidBalanceOfConsumerCredit);
        val.setUnpaidBalanceOfMortgage(this.unpaidBalanceOfMortgage);
        val.setUnpaidBalanceOfPersonalLoan(this.unpaidBalanceOfPersonalLoan);
        val.setUnpaidBalanceOfCreditCard(this.unpaidBalanceOfCreditCard);
        val.setUnpaidBalanceOfTelco(this.unpaidBalanceOfTelco);
        val.setUnpaidBalanceOfOtherProducts(this.unpaidBalanceOfOtherProducts);
        val.setWorstUnpaidBalance(this.worstUnpaidBalance);
        val.setWorstSituationCode(this.worstSituationCode);
        val.setNumberOfDaysOfWorstSituation(this.numberOfDaysOfWorstSituation);
        val.setNumberOfCreditors(this.numberOfCreditors);
        val.setDelincuencyDays(this.delincuencyDays);
        val.setCreatedAt(this.createdAt);
        val.setScoringCategory(this.getScoringCategory());
        val.setRiskScore(this.getRiskScore());
        return val;
    }
}
