package fintech.spain.equifax.json;

import fintech.JsonUtils;
import fintech.spain.equifax.json.client.EquifaxJsonResponse;
import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Component
public class EquifaxJsonResponseParser {

    private static final String PRESENT_CODE_NOT_FOUND = "01";
    private static final String PRESENT_CODE_FOUND = "00";
    private static final String RETURN_CODE_COMPLETE = "000";

    public EquifaxResponse parse(EquifaxJsonResponse response) {
        EquifaxParser parser = Stream.of(EquifaxParser.values())
            .filter(val -> val.condition.test(response))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Can't find parser for Equifax response"));

        return parser.parser.apply(response);
    }

    @Getter
    @AllArgsConstructor
    private enum EquifaxParser {

        ERROR(r -> EquifaxJsonResponse.TransactionState.ERROR == r.getTransactionState(),
            EquifaxJsonResponseParser::errors),

        NOT_FOUND(r -> PRESENT_CODE_NOT_FOUND.equals(r.presentCode()),
            EquifaxJsonResponseParser::notFound),

        FOUND(r -> EquifaxJsonResponse.TransactionState.COMPLETED == r.getTransactionState() &&
            (PRESENT_CODE_FOUND.equals(r.presentCode()) ||
                RETURN_CODE_COMPLETE.equals(r.returnCode())),
            EquifaxJsonResponseParser::body);


        private Predicate<EquifaxJsonResponse> condition;
        private Function<EquifaxJsonResponse, EquifaxResponse> parser;

    }

    private static EquifaxResponse errors(EquifaxJsonResponse response) {
        return new EquifaxResponse()
            .setStatus(EquifaxStatus.ERROR)
            .setError(JsonUtils.writeValueAsString(response.getErrors()));
    }

    private static EquifaxResponse notFound(EquifaxJsonResponse response) {
        return new EquifaxResponse()
            .setStatus(EquifaxStatus.NOT_FOUND);
    }

    private static EquifaxResponse body(EquifaxJsonResponse response) {
        EquifaxResponse equifaxResponse = new EquifaxResponse();
        equifaxResponse.setStatus(EquifaxStatus.FOUND);
        equifaxResponse.setRiskScore(response.riskScore());

        EquifaxJsonResponse.Severity severity = response.getApplicants().getPrimaryConsumer().getDataSourceResponses().getEipg().getSeverity();
        if (severity != null) {
            equifaxResponse.setScoringCategory(severity.getScoringCategory());
        }

        EquifaxJsonResponse.AraAttributes ara = response.araAttributes();
        if (ara != null) {
            equifaxResponse.setTotalNumberOfOperations(ara.getTotalNumberOfOperations());
            equifaxResponse.setNumberOfConsumerCreditOperations(ara.getNumberOfConsumerCreditOperations());
            equifaxResponse.setNumberOfMortgageOperations(ara.getNumberOfMortgageOperations());
            equifaxResponse.setNumberOfPersonalLoanOperations(ara.getNumberOfPersonalLoanOperations());
            equifaxResponse.setNumberOfCreditCardOperations(ara.getNumberOfCreditCardOperations());
            equifaxResponse.setNumberOfTelcoOperations(ara.getNumberOfTelcoOperations());
            equifaxResponse.setTotalNumberOfOtherUnpaid(ara.getTotalNumberOfOtherUnpaid());
            equifaxResponse.setTotalUnpaidBalance(ara.getTotalUnpaidBalance());
            equifaxResponse.setUnpaidBalanceOwnEntity(ara.getUnpaidBalanceOwnEntity());
            equifaxResponse.setUnpaidBalanceOfOther(ara.getUnpaidBalanceOfOtherEntities());
            equifaxResponse.setUnpaidBalanceOfConsumerCredit(ara.getUnpaidBalanceOfConsumerCredit());
            equifaxResponse.setUnpaidBalanceOfMortgage(ara.getUnpaidBalanceOfMortgage());
            equifaxResponse.setUnpaidBalanceOfPersonalLoan(ara.getUnpaidBalanceOfPersonalLoan());
            equifaxResponse.setUnpaidBalanceOfCreditCard(ara.getUnpaidBalanceOfCreditCard());
            equifaxResponse.setUnpaidBalanceOfTelco(ara.getUnpaidBalanceOfTelco());
            equifaxResponse.setUnpaidBalanceOfOtherProducts(ara.getUnpaidBalanceOfOtherProducts());
            equifaxResponse.setWorstUnpaidBalance(ara.getWorstUnpaidBalance());
            equifaxResponse.setWorstSituationCode(ara.getWorstSituationCode());
            equifaxResponse.setNumberOfDaysOfWorstSituation(ara.getNumberOfDaysOfWorstSituation());
            equifaxResponse.setNumberOfCreditors(ara.getNumberOfCreditors());
            equifaxResponse.setDelincuencyDays(ara.getDelincuencyDays());
        }

        return equifaxResponse;
    }

}
