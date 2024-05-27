package fintech.spain.equifax.mock;

import fintech.spain.equifax.model.EquifaxResponse;
import fintech.spain.equifax.model.EquifaxStatus;

import java.util.function.Supplier;

import static fintech.BigDecimalUtils.amount;

public final class MockedEquifaxResponse {

    public static final Supplier<EquifaxResponse> NOT_FOUND = 
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.NOT_FOUND);

    public static final Supplier<EquifaxResponse> DEFAULT = NOT_FOUND;

    public static final Supplier<EquifaxResponse> ERROR_DS_NOT_AVAILABLE =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.ERROR)
            .setError("10040: Datasource unavailable");

    public static final Supplier<EquifaxResponse> ERROR_NOT_AUTHORIZED =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.ERROR)
            .setError("10001: Authorization failed");

    public static final Supplier<EquifaxResponse> FOUND =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(2L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(10.0))
            .setUnpaidBalanceOwnEntity(amount(102.0))
            .setUnpaidBalanceOfOther(amount(103.0))
            .setUnpaidBalanceOfConsumerCredit(amount(104.0))
            .setUnpaidBalanceOfMortgage(amount(105.0))
            .setUnpaidBalanceOfPersonalLoan(amount(106.0))
            .setUnpaidBalanceOfCreditCard(amount(107.0))
            .setUnpaidBalanceOfTelco(amount(108.0))
            .setUnpaidBalanceOfOtherProducts(amount(109.0))
            .setWorstUnpaidBalance(amount(110.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(8L)
            .setNumberOfCreditors(9L)
            .setDelincuencyDays(10L)
            .setScoringCategory("A")
            .setRiskScore("1");

    public static final Supplier<EquifaxResponse> DEBT_AMOUNT =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(0L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(103.0))
            .setUnpaidBalanceOwnEntity(amount(0.0))
            .setUnpaidBalanceOfOther(amount(103.0))
            .setUnpaidBalanceOfConsumerCredit(amount(101.0))
            .setUnpaidBalanceOfMortgage(amount(0.0))
            .setUnpaidBalanceOfPersonalLoan(amount(0.0))
            .setUnpaidBalanceOfCreditCard(amount(0.0))
            .setUnpaidBalanceOfTelco(amount(2.0))
            .setUnpaidBalanceOfOtherProducts(amount(0.0))
            .setWorstUnpaidBalance(amount(0.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(8L)
            .setNumberOfCreditors(1L)
            .setDelincuencyDays(10L);

    public static final Supplier<EquifaxResponse> DEBT_AMOUNT_TELCO =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(0L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(101.0))
            .setUnpaidBalanceOwnEntity(amount(0.0))
            .setUnpaidBalanceOfOther(amount(101.0))
            .setUnpaidBalanceOfConsumerCredit(amount(99.0))
            .setUnpaidBalanceOfMortgage(amount(0.0))
            .setUnpaidBalanceOfPersonalLoan(amount(0.0))
            .setUnpaidBalanceOfCreditCard(amount(0.0))
            .setUnpaidBalanceOfTelco(amount(2.0))
            .setUnpaidBalanceOfOtherProducts(amount(0.0))
            .setWorstUnpaidBalance(amount(0.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(8L)
            .setNumberOfCreditors(1L)
            .setDelincuencyDays(10L);

    public static final Supplier<EquifaxResponse> DEBT_COUNT =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(2L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(102.0))
            .setUnpaidBalanceOwnEntity(amount(0.0))
            .setUnpaidBalanceOfOther(amount(103.0))
            .setUnpaidBalanceOfConsumerCredit(amount(104.0))
            .setUnpaidBalanceOfMortgage(amount(105.0))
            .setUnpaidBalanceOfPersonalLoan(amount(106.0))
            .setUnpaidBalanceOfCreditCard(amount(107.0))
            .setUnpaidBalanceOfTelco(amount(108.0))
            .setUnpaidBalanceOfOtherProducts(amount(109.0))
            .setWorstUnpaidBalance(amount(110.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(8L)
            .setNumberOfCreditors(9L)
            .setDelincuencyDays(10L);

    public static final Supplier<EquifaxResponse> DELINCUENCY_DAYS =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(0L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(1.0))
            .setUnpaidBalanceOwnEntity(amount(2.0))
            .setUnpaidBalanceOfOther(amount(3.0))
            .setUnpaidBalanceOfConsumerCredit(amount(4.0))
            .setUnpaidBalanceOfMortgage(amount(5.0))
            .setUnpaidBalanceOfPersonalLoan(amount(6.0))
            .setUnpaidBalanceOfCreditCard(amount(7.0))
            .setUnpaidBalanceOfTelco(amount(8.0))
            .setUnpaidBalanceOfOtherProducts(amount(9.0))
            .setWorstUnpaidBalance(amount(10.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(8L)
            .setNumberOfCreditors(1L)
            .setDelincuencyDays(110L);

    public static final Supplier<EquifaxResponse> WORST_SITUATION_DAYS =
        () -> new EquifaxResponse()
            .setStatus(EquifaxStatus.FOUND)
            .setTotalNumberOfOperations(0L)
            .setNumberOfConsumerCreditOperations(2L)
            .setNumberOfMortgageOperations(3L)
            .setNumberOfPersonalLoanOperations(4L)
            .setNumberOfCreditCardOperations(5L)
            .setNumberOfTelcoOperations(6L)
            .setTotalNumberOfOtherUnpaid(7L)
            .setTotalUnpaidBalance(amount(1.0))
            .setUnpaidBalanceOwnEntity(amount(2.0))
            .setUnpaidBalanceOfOther(amount(3.0))
            .setUnpaidBalanceOfConsumerCredit(amount(4.0))
            .setUnpaidBalanceOfMortgage(amount(5.0))
            .setUnpaidBalanceOfPersonalLoan(amount(6.0))
            .setUnpaidBalanceOfCreditCard(amount(7.0))
            .setUnpaidBalanceOfTelco(amount(8.0))
            .setUnpaidBalanceOfOtherProducts(amount(9.0))
            .setWorstUnpaidBalance(amount(10.0))
            .setWorstSituationCode("01")
            .setNumberOfDaysOfWorstSituation(101L)
            .setNumberOfCreditors(1L)
            .setDelincuencyDays(10L);

}
