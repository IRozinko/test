package fintech.spain.equifax.scoring;

import fintech.ScoringProperties;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.spain.equifax.EquifaxService;
import fintech.spain.equifax.model.EquifaxQuery;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class EquifaxScoringValuesProvider implements ScoringValuesProvider {

    private static final String EQUIFAX_PREFIX = "equifax";
    private static final String TOTAL_NUMBER_OF_OPERATIONS = "total_number_of_operations";
    private static final String NUMBER_OF_CONSUMER_CREDIT_OPERATIONS = "number_of_consumer_credit_operations";
    private static final String NUMBER_OF_MORTGAGE_OPERATIONS = "number_of_mortgage_operations";
    private static final String NUMBER_OF_PERSONAL_LOAN_OPERATIONS = "number_of_personal_loan_operations";
    private static final String NUMBER_OF_CREDIT_CARD_OPERATIONS = "number_of_credit_card_operations";
    private static final String NUMBER_OF_TELCO_OPERATIONS = "number_of_telco_operations";
    private static final String TOTAL_NUMBER_OF_OTHER_UNPAID = "total_number_of_other_unpaid";
    private static final String TOTAL_UNPAID_BALANCE = "total_unpaid_balance";
    private static final String UNPAID_BALANCE_OWN_ENTITY = "unpaid_balance_own_entity";
    private static final String UNPAID_BALANCE_OF_OTHER = "unpaid_balance_of_other";
    private static final String UNPAID_BALANCE_OF_CONSUMER_CREDIT = "unpaid_balance_of_consumer_credit";
    private static final String UNPAID_BALANCE_OF_MORTGAGE = "unpaid_balance_of_mortgage";
    private static final String UNPAID_BALANCE_OF_PERSONAL_LOAN = "unpaid_balance_of_personal_loan";
    private static final String UNPAID_BALANCE_OF_CREDIT_CARD = "unpaid_balance_of_credit_card";
    private static final String UNPAID_BALANCE_OF_TELCO = "unpaid_balance_of_telco";
    private static final String UNPAID_BALANCE_OF_OTHER_PRODUCTS = "unpaid_balance_of_other_products";
    private static final String WORST_UNPAID_BALANCE = "worst_unpaid_balance";
    private static final String WORST_SITUATION_CODE = "worst_situation_code";
    private static final String NUMBER_OF_DAYS_OF_WORST_SITUATION = "number_of_days_of_worst_situation";
    private static final String NUMBER_OF_CREDITORS = "number_of_creditors";
    private static final String DELINCUENCY_DAYS = "delincuency_days";
    private static final String SCORING_CATEGORY = "scoring_category";
    private static final String RISK_SCORE = "risk_score";
    private static final String PRESENT = "present";

    private final EquifaxService equifaxService;

    @Override
    public Properties provide(long clientId) {
        return equifaxService.findLatestResponse(EquifaxQuery.byClientIdOkOrNotFound(clientId))
            .map(resp -> {
                ScoringProperties properties = new ScoringProperties(EQUIFAX_PREFIX);
                properties.put(TOTAL_NUMBER_OF_OPERATIONS, resp.getTotalNumberOfOperations());
                properties.put(NUMBER_OF_CONSUMER_CREDIT_OPERATIONS, resp.getNumberOfConsumerCreditOperations());
                properties.put(NUMBER_OF_MORTGAGE_OPERATIONS, resp.getNumberOfMortgageOperations());
                properties.put(NUMBER_OF_PERSONAL_LOAN_OPERATIONS, resp.getNumberOfPersonalLoanOperations());
                properties.put(NUMBER_OF_CREDIT_CARD_OPERATIONS, resp.getNumberOfCreditCardOperations());
                properties.put(NUMBER_OF_TELCO_OPERATIONS, resp.getNumberOfTelcoOperations());
                properties.put(TOTAL_NUMBER_OF_OTHER_UNPAID, resp.getTotalNumberOfOtherUnpaid());
                properties.put(TOTAL_UNPAID_BALANCE, resp.getTotalUnpaidBalance());
                properties.put(UNPAID_BALANCE_OWN_ENTITY, resp.getUnpaidBalanceOwnEntity());
                properties.put(UNPAID_BALANCE_OF_OTHER, resp.getUnpaidBalanceOfOther());
                properties.put(UNPAID_BALANCE_OF_CONSUMER_CREDIT, resp.getUnpaidBalanceOfConsumerCredit());
                properties.put(UNPAID_BALANCE_OF_MORTGAGE, resp.getUnpaidBalanceOfMortgage());
                properties.put(UNPAID_BALANCE_OF_PERSONAL_LOAN, resp.getUnpaidBalanceOfPersonalLoan());
                properties.put(UNPAID_BALANCE_OF_CREDIT_CARD, resp.getUnpaidBalanceOfCreditCard());
                properties.put(UNPAID_BALANCE_OF_TELCO, resp.getUnpaidBalanceOfTelco());
                properties.put(UNPAID_BALANCE_OF_OTHER_PRODUCTS, resp.getUnpaidBalanceOfOtherProducts());
                properties.put(WORST_UNPAID_BALANCE, resp.getWorstUnpaidBalance());
                properties.put(WORST_SITUATION_CODE, resp.getWorstSituationCode());
                properties.put(NUMBER_OF_DAYS_OF_WORST_SITUATION, resp.getNumberOfDaysOfWorstSituation());
                properties.put(NUMBER_OF_CREDITORS, resp.getNumberOfCreditors());
                properties.put(DELINCUENCY_DAYS, resp.getDelincuencyDays());
                properties.put(SCORING_CATEGORY, resp.getScoringCategory());
                properties.put(RISK_SCORE, resp.getRiskScore());
                properties.put(PRESENT, resp.getStatus() == EquifaxStatus.FOUND);
                return properties;
            }).orElse(new ScoringProperties());
    }

}
