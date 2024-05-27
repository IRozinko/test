package fintech.spain.equifax.json.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class EquifaxJsonResponse {

    private String interactionId;
    private Applicants applicants;
    private List<Error> errors = new ArrayList<>();
    private TransactionState transactionState;
    private Integer transactionId;
    private String timestamp;

    public String presentCode() {
        return applicants.primaryConsumer.dataSourceResponses.eipg.risk.present;
    }

    public String returnCode() {
        return applicants.primaryConsumer.dataSourceResponses.eipg.risk.returnCode;
    }

    public AraAttributes araAttributes() {
        return applicants.primaryConsumer.dataSourceResponses.eipg.risk.araAttributes;
    }

    public String riskScore() {
        return applicants.primaryConsumer.dataSourceResponses.eipg.risk.rating;
    }

    public enum TransactionState {
        COMPLETED, ERROR
    }

    @Getter
    @Setter
    public static class DataSourceResponses {

        @JsonProperty("EIPG")
        private Eipg eipg;

    }

    @Getter
    @Setter
    public static class AraAttributes {
        private Long totalNumberOfOperations;
        private Long numberOfConsumerCreditOperations;
        private Long numberOfMortgageOperations;
        private Long numberOfPersonalLoanOperations;
        private Long numberOfCreditCardOperations;
        private Long numberOfTelcoOperations;
        private Long totalNumberOfOtherUnpaid;
        private BigDecimal totalUnpaidBalance;
        private BigDecimal unpaidBalanceOwnEntity;
        private BigDecimal unpaidBalanceOfOtherEntities;
        private BigDecimal unpaidBalanceOfConsumerCredit;
        private BigDecimal unpaidBalanceOfMortgage;
        private BigDecimal unpaidBalanceOfPersonalLoan;
        private BigDecimal unpaidBalanceOfCreditCard;
        private BigDecimal unpaidBalanceOfTelco;
        private BigDecimal unpaidBalanceOfOtherProducts;
        private BigDecimal worstUnpaidBalance;
        private String worstSituationCode;
        private Long numberOfDaysOfWorstSituation;
        private Long numberOfCreditors;
        private Long delincuencyDays;
    }

    @Getter
    @Setter
    public static class Applicants {
        private PrimaryConsumer primaryConsumer;
    }

    @Getter
    @Setter
    public static class Error {
        private String code;
        private String message;
        private String category;
    }

    @Getter
    @Setter
    public static class PersonalInformation {
        private String idCountryCode;
        private String idType;
        private String idCode;
    }

    @Getter
    @Setter
    public static class PrimaryConsumer {
        private PersonalInformation personalInformation;
        private DataSourceResponses dataSourceResponses;
    }

    @Getter
    @Setter
    public static class Risk {
        private String identifier;
        private String idCode;
        private String returnCode;
        private String present;
        private String rating;
        private AraAttributes araAttributes;
        private List<Error> errors;
    }

    @Getter
    @Setter
    public static class Severity {
        private String scoringCategory;
    }

    @Getter
    @Setter
    public static class Eipg {

        @JsonProperty("RISK")
        private Risk risk;

        @JsonProperty("SEVERITY")
        private Severity severity;

    }

}


