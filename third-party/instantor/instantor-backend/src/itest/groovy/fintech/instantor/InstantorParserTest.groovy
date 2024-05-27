package fintech.instantor

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.instantor.db.InstantorResponseRepository
import fintech.instantor.db.InstantorTransactionRepository
import fintech.instantor.events.InstantorResponseFailed
import fintech.instantor.events.InstantorResponseProcessed
import fintech.instantor.json.insight.InstantorInsightResponse
import fintech.instantor.model.InstantorProcessingStatus
import fintech.instantor.model.InstantorResponseQuery
import fintech.instantor.model.InstantorResponseStatus
import fintech.instantor.model.InstantorTransactionQuery
import fintech.instantor.model.SaveInstantorResponseCommand
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_12M
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_1M
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_1W
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_3M
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_6M
import static fintech.instantor.model.InstantorResponseAttributes.ATM_EXPENSES_RATIO_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.AVG_30DROLLING_PAST
import static fintech.instantor.model.InstantorResponseAttributes.AVG_30D_ROLLING_RECENT
import static fintech.instantor.model.InstantorResponseAttributes.AVG_NORM_STD_MONTHLY_EXPENSE_STREAMS
import static fintech.instantor.model.InstantorResponseAttributes.AVG_ROLLING_DIFF
import static fintech.instantor.model.InstantorResponseAttributes.AVG_ROLLING_TREND
import static fintech.instantor.model.InstantorResponseAttributes.AVG_STD_MONTHLY_EXPENSE_STREAMS
import static fintech.instantor.model.InstantorResponseAttributes.AVG_STD_TIMING
import static fintech.instantor.model.InstantorResponseAttributes.BANK_NAME
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_12M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_1M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_1W
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_3M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_6M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_0_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_12M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_1M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_1W
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_3M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_6M
import static fintech.instantor.model.InstantorResponseAttributes.DAYS_BALANCE_BELOW_7_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO1M
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO_12M
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO_1W
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO_3M
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO_6M
import static fintech.instantor.model.InstantorResponseAttributes.GAMBLING_INCOME_RATIO_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.INSTANTOR_REQUEST_ID
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_0_TO_5
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_1000_TO_2000
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_100_TO_200
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_10_TO_20
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_2000_TO_INF
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_200_TO_500
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_20_TO_50
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_500_TO_1000
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_50_TO_100
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_5_TO_10
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_1000_TO_MINUS_500
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_100_TO_MINUS_50
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_10_TO_MINUS_5
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_2000_TO_MINUS_1000
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_200_TO_MINUS_100
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_20_TO_MINUS_10
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_500_TO_MINUS_200
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_50_TO_MINUS_20
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_5_TO_0
import static fintech.instantor.model.InstantorResponseAttributes.LAST_MONTH_SUM_MINUS_INF_TO_MINUS_2000
import static fintech.instantor.model.InstantorResponseAttributes.MAX_NORM_STD_MONTHLY_EXPENSE_STREAMS
import static fintech.instantor.model.InstantorResponseAttributes.MAX_STD_MONTHLY_EXPENSE_STREAMS
import static fintech.instantor.model.InstantorResponseAttributes.MAX_STD_TIMING
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_12M
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_1M
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_1W
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_3M
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_6M
import static fintech.instantor.model.InstantorResponseAttributes.NEGATIVE_CASHFLOW_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_12M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_1M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_1W
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_3M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_6M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_COLLECTIONS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.NUM_MONTHLY_EXPENSE_STREAMS
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_12M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_1M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_1W
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_3M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_6M
import static fintech.instantor.model.InstantorResponseAttributes.NUM_SAVINGS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_AMOUNT_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_AVG_12M
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_AVG_3M
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_AVG_6M
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_DAYS_AGO_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_DESCRIPTIONS
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_LAST_45D_FOUND
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_MEAN_AMOUNT_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_MEAN_TIME_BETWEEN_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_NUM_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_SPAN_MONTHS
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_TREND_12M
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_TREND_3M
import static fintech.instantor.model.InstantorResponseAttributes.OTHER_RECUR_TREND_6M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_12M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_1M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_1W
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_3M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_6M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_CASHFLOW_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_12M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_1M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_1W
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_3M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_6M
import static fintech.instantor.model.InstantorResponseAttributes.POSITIVE_NEGATIVE_RATIO_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_AMOUNT_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_AVG_12M
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_AVG_3M
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_AVG_6M
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_DAYS_AGO_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_DESCRIPTIONS
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_LAST_45D_FOUND
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_MEAN_AMOUNT_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_NUM_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_SPAN_MONTHS
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_TREND_12M
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_TREND_3M
import static fintech.instantor.model.InstantorResponseAttributes.PRIMARY_INCOME_TREND_6M
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_12M
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_1M
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_1W
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_3M
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_6M
import static fintech.instantor.model.InstantorResponseAttributes.REPAYMENT_LOAN_RATIO_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_AMOUNT_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_AVG_12M
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_AVG_3M
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_AVG_6M
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_DAYS_AGO_LAST_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_DESCRIPTIONS
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_LAST_45D_FOUND
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_MEAN_AMOUNT_PAYMENT
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_NUM_PAYMENTS
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_SPAN_MONTHS
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_TREND_12M
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_TREND_3M
import static fintech.instantor.model.InstantorResponseAttributes.SECONDARY_INCOME_TREND_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_ATM_WITHDRAWALS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_COLLECTIONS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_GAMBLING_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_LOANS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_REPAYMENTS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_12M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_1M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_1W
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_3M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_6M
import static fintech.instantor.model.InstantorResponseAttributes.SUM_SAVINGS_TOTAL
import static fintech.instantor.model.InstantorResponseAttributes.TREND_0_TO_5
import static fintech.instantor.model.InstantorResponseAttributes.TREND_1000_TO_2000
import static fintech.instantor.model.InstantorResponseAttributes.TREND_100_TO_200
import static fintech.instantor.model.InstantorResponseAttributes.TREND_10_TO_20
import static fintech.instantor.model.InstantorResponseAttributes.TREND_2000_TO_INF
import static fintech.instantor.model.InstantorResponseAttributes.TREND_200_TO_500
import static fintech.instantor.model.InstantorResponseAttributes.TREND_20_TO_50
import static fintech.instantor.model.InstantorResponseAttributes.TREND_500_TO_1000
import static fintech.instantor.model.InstantorResponseAttributes.TREND_50_TO_100
import static fintech.instantor.model.InstantorResponseAttributes.TREND_5_TO_10
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_1000_TO_MINUS_500
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_100_TO_MINUS_50
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_10_TO_MINUS_5
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_2000_TO_MINUS_1000
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_200_TO_MINUS_100
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_20_TO_MINUS_10
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_500_TO_MINUS_200
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_50_TO_MINUS_20
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_5_TO_0
import static fintech.instantor.model.InstantorResponseAttributes.TREND_MINUS_INF_TO_MINUS_2000

class InstantorParserTest extends BaseApiTest {

    @Autowired
    InstantorResponseRepository responseRepository

    @Autowired
    InstantorService service

    @Autowired
    InstantorTransactionRepository transactionRepository

    def "Simulate response"() {
        expect:
        responseRepository.count() == 0
        transactionRepository.count() == 0

        when:
        def id = service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        then:
        responseRepository.count() == 1

        and:
        with(responseRepository.getRequired(id)) {
            status == InstantorResponseStatus.OK
            processingStatus == InstantorProcessingStatus.PENDING
            clientId == 123L
            JsonUtils.readValue(payloadJson, InstantorInsightResponse.class).processStatus == "ok"
        }

        when:
        service.processResponse(id)

        then:
        eventConsumer.containsEvent(InstantorResponseProcessed.class)

        and:
        with(responseRepository.getRequired(id)) {
            status == InstantorResponseStatus.OK
            processingStatus == InstantorProcessingStatus.PROCESSED
        }

        and:
        with(service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()) {
            !nameForVerification
            personalNumberForVerification == "111"
            accountNumbers.startsWith("ES1793017144274569123112,ES1793017144274569123113")
        }

        and:
        transactionRepository.count() == 418
        def transactions = transactionRepository.findAll()
        with(transactions[0]) {
            amount == -4.45
            balance == 0.77
            date == date("2018-11-20")
            accountNumber == "ES1793017144274569123112"
            accountHolderName == "John"
            description == "Description: FARMACIA ELENA BE"
            currency == "EUR"
            !category
        }

        with(transactions[184]) {
            amount == -400.00
        }

        with(transactions[185]) {
            amount == 50.00
            balance == 442.32
            date == date("2018-06-05")
        }

        with(transactions[186]) {
            date == date("2018-06-05")
            accountNumber == "ES1793017144274569123112"
            amount == -67.9
            balance == 392.32
        }
    }

    def "Scrape error"() {
        when:
        def id = service.saveResponse(new SaveInstantorResponseCommand(status: InstantorResponseStatus.OK, payloadJson: ClasspathUtils.resourceToString("instantor-payload-scrape-error.json")))
        service.processResponse(id)

        then:
        def entity = responseRepository.getRequired(id)
        entity.clientId == 10715L
        entity.status == InstantorResponseStatus.FAILED
        eventConsumer.containsEvent(InstantorResponseFailed.class)
    }

    def "No client id"() {
        when:
        def id = service.saveResponse(new SaveInstantorResponseCommand(status: InstantorResponseStatus.OK, payloadJson: ClasspathUtils.resourceToString("instantor-payload-no-client-id.json")))

        then:
        def entity = responseRepository.getRequired(id)
        !entity.clientId
        entity.status == InstantorResponseStatus.FAILED
        !eventConsumer.containsEvent(InstantorResponseFailed.class)
    }

    def "Null holder name in account and details"() {
        when:
        def id = service.saveResponse(new SaveInstantorResponseCommand(status: InstantorResponseStatus.OK, payloadJson: ClasspathUtils.resourceToString("instantor-payload-null-holder-name.json")))

        then:
        def entity = responseRepository.getRequired(id)
        entity.clientId
        entity.status == InstantorResponseStatus.OK
        !entity.nameForVerification
    }

    def "Find latest"() {
        expect:
        !service.findLatest(new InstantorResponseQuery().setClientId(123L)).present

        when:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793017144274569123112", "ES1793017144274569123113"))
        def latestId = service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793017144274569123112", "ES1793017144274569123113"))
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123456L, "111", "John", "ES1793017144274569123112", "ES1793017144274569123113"))

        then:
        with(service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()) {
            id == latestId
            clientId == 123L
        }
    }

    def "Processing failed"() {
        when:
        def id = service.saveResponse(new SaveInstantorResponseCommand(status: InstantorResponseStatus.OK, payloadJson: ClasspathUtils.resourceToString("instantor-payload-scrape-error.json")))
        service.processingFailed(id)

        then:
        def entity = responseRepository.getRequired(id)
        entity.status == InstantorResponseStatus.FAILED
        entity.processingStatus == InstantorProcessingStatus.PROCESSING_ERROR
    }

    def "Find transactions"() {
        when:
        def id = service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        then:
        def tx = service.findTransactions(new InstantorTransactionQuery()
            .setClientId(123L)
            .setResponseId(id)
            .setDateFrom(date("2018-11-20"))
            .setDateTo(date("2018-11-20"))
            .setAccountNumber("ES1793017144274569123112")
        )
        tx.size() == 1
        tx[0].amount == -4.45

        and:
        service.findTransactions(new InstantorTransactionQuery()
            .setClientId(123L)
            .setResponseId(id)
            .setDateFrom(date("2013-11-20"))
            .setDateTo(date("2045-11-20"))
            .setAccountNumber("ES1793017144274569123112"))
            .size() == 336

        and:
        service.findTransactions(new InstantorTransactionQuery()
            .setClientId(1234L))
            .isEmpty()
        service.findTransactions(new InstantorTransactionQuery()
            .setResponseId(id + 1))
            .isEmpty()
        service.findTransactions(new InstantorTransactionQuery()
            .setAccountNumber("A ES1793017144274569123112"))
            .isEmpty()
    }


    def "Parsing expected request data"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(BANK_NAME).get() == "CaixaBank"
        response.getAttribute(INSTANTOR_REQUEST_ID).get() == "96e6cba4-f6dd-4d29-81cd-479fc0ac337c"
    }

    def "Parsing expected values of Instantor IncomeVerifications"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(PRIMARY_INCOME_MEAN_AMOUNT_PAYMENT).get() == "1"
        response.getAttribute(PRIMARY_INCOME_LAST_45D_FOUND).get() == "2"
        response.getAttribute(PRIMARY_INCOME_AVG_6M).get() == "3"
        response.getAttribute(PRIMARY_INCOME_AVG_3M).get() == "4"
        response.getAttribute(PRIMARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS).get() == "5"
        response.getAttribute(PRIMARY_INCOME_TREND_3M).get() == "6"
        response.getAttribute(PRIMARY_INCOME_DAYS_AGO_LAST_PAYMENT).get() == "7"
        response.getAttribute(PRIMARY_INCOME_TREND_6M).get() == "8"
        response.getAttribute(PRIMARY_INCOME_AMOUNT_LAST_PAYMENT).get() == "9"
        response.getAttribute(PRIMARY_INCOME_SPAN_MONTHS).get() == "10"
        response.getAttribute(PRIMARY_INCOME_TREND_12M).get() == "11"
        response.getAttribute(PRIMARY_INCOME_AVG_12M).get() == "12"
        response.getAttribute(PRIMARY_INCOME_NUM_PAYMENTS).get() == "13"
        response.getAttribute(PRIMARY_INCOME_DESCRIPTIONS).get() == "14"
        response.getAttribute(SECONDARY_INCOME_MEAN_AMOUNT_PAYMENT).get() == "15"
        response.getAttribute(SECONDARY_INCOME_LAST_45D_FOUND).get() == "16"
        response.getAttribute(SECONDARY_INCOME_AVG_6M).get() == "17"
        response.getAttribute(SECONDARY_INCOME_AVG_3M).get() == "18"
        response.getAttribute(SECONDARY_INCOME_MEAN_TIME_BETWEEN_PAYMENTS).get() == "19"
        response.getAttribute(SECONDARY_INCOME_TREND_3M).get() == "20"
        response.getAttribute(SECONDARY_INCOME_DAYS_AGO_LAST_PAYMENT).get() == "21"
        response.getAttribute(SECONDARY_INCOME_TREND_6M).get() == "22"
        response.getAttribute(SECONDARY_INCOME_AMOUNT_LAST_PAYMENT).get() == "23"
        response.getAttribute(SECONDARY_INCOME_SPAN_MONTHS).get() == "24"
        response.getAttribute(SECONDARY_INCOME_TREND_12M).get() == "25"
        response.getAttribute(SECONDARY_INCOME_AVG_12M).get() == "26"
        response.getAttribute(SECONDARY_INCOME_NUM_PAYMENTS).get() == "27"
        response.getAttribute(SECONDARY_INCOME_DESCRIPTIONS).get() == "28"
        response.getAttribute(OTHER_RECUR_MEAN_AMOUNT_PAYMENT).get() == "29"
        response.getAttribute(OTHER_RECUR_LAST_45D_FOUND).get() == "30"
        response.getAttribute(OTHER_RECUR_AVG_6M).get() == "31"
        response.getAttribute(OTHER_RECUR_AVG_3M).get() == "32"
        response.getAttribute(OTHER_RECUR_MEAN_TIME_BETWEEN_PAYMENTS).get() == "33"
        response.getAttribute(OTHER_RECUR_TREND_3M).get() == "34"
        response.getAttribute(OTHER_RECUR_DAYS_AGO_LAST_PAYMENT).get() == "35"
        response.getAttribute(OTHER_RECUR_TREND_6M).get() == "36"
        response.getAttribute(OTHER_RECUR_AMOUNT_LAST_PAYMENT).get() == "37"
        response.getAttribute(OTHER_RECUR_SPAN_MONTHS).get() == "38"
        response.getAttribute(OTHER_RECUR_TREND_12M).get() == "39"
        response.getAttribute(OTHER_RECUR_AVG_12M).get() == "40"
        response.getAttribute(OTHER_RECUR_NUM_PAYMENTS).get() == "41"
        response.getAttribute(OTHER_RECUR_DESCRIPTIONS).get() == "42"
    }

    def "Parsing expected values of Instantor Risk Savings"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(SUM_SAVINGS_1W).get() == "1"
        response.getAttribute(NUM_SAVINGS_6M).get() == "2"
        response.getAttribute(NUM_SAVINGS_TOTAL).get() == "3"
        response.getAttribute(SUM_SAVINGS_TOTAL).get() == "4"
        response.getAttribute(SUM_SAVINGS_1M).get() == "5"
        response.getAttribute(SUM_SAVINGS_3M).get() == "6"
        response.getAttribute(SUM_SAVINGS_12M).get() == "7"
        response.getAttribute(NUM_SAVINGS_12M).get() == "8"
        response.getAttribute(NUM_SAVINGS_3M).get() == "9"
        response.getAttribute(NUM_SAVINGS_1W).get() == "10"
        response.getAttribute(SUM_SAVINGS_6M).get() == "11"
        response.getAttribute(NUM_SAVINGS_1M).get() == "12"
    }

    def "Parsing expected values of Instantor Risk IncomeTrends"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(AVG_30D_ROLLING_RECENT).get() == "1"
        response.getAttribute(AVG_30DROLLING_PAST).get() == "2"
        response.getAttribute(AVG_ROLLING_TREND).get() == "3"
        response.getAttribute(AVG_ROLLING_DIFF).get() == "4"
    }

    def "Parsing expected values of Instantor Risk CashFlow"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(POSITIVE_CASHFLOW_1M).get() == "1"
        response.getAttribute(POSITIVE_CASHFLOW_1W).get() == "2"
        response.getAttribute(NEGATIVE_CASHFLOW_3M).get() == "3"
        response.getAttribute(POSITIVE_CASHFLOW_TOTAL).get() == "4"
        response.getAttribute(POSITIVE_CASHFLOW_3M).get() == "5"
        response.getAttribute(NEGATIVE_CASHFLOW_TOTAL).get() == "6"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_6M).get() == "7"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_TOTAL).get() == "8"
        response.getAttribute(NEGATIVE_CASHFLOW_1W).get() == "9"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_3M).get() == "10"
        response.getAttribute(NEGATIVE_CASHFLOW_12M).get() == "11"
        response.getAttribute(NEGATIVE_CASHFLOW_1M).get() == "12"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_1W).get() == "13"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_1M).get() == "14"
        response.getAttribute(POSITIVE_CASHFLOW_6M).get() == "15"
        response.getAttribute(POSITIVE_CASHFLOW_12M).get() == "16"
        response.getAttribute(POSITIVE_NEGATIVE_RATIO_12M).get() == "17"
        response.getAttribute(NEGATIVE_CASHFLOW_6M).get() == "18"
    }

    def "Parsing expected values of Instantor Risk Collections"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(SUM_COLLECTIONS_3M).get() == "1"
        response.getAttribute(NUM_COLLECTIONS_TOTAL).get() == "2"
        response.getAttribute(SUM_COLLECTIONS_6M).get() == "3"
        response.getAttribute(NUM_COLLECTIONS_1W).get() == "4"
        response.getAttribute(SUM_COLLECTIONS_12M).get() == "5"
        response.getAttribute(SUM_COLLECTIONS_TOTAL).get() == "6"
        response.getAttribute(NUM_COLLECTIONS_1M).get() == "7"
        response.getAttribute(NUM_COLLECTIONS_6M).get() == "8"
        response.getAttribute(SUM_COLLECTIONS_1M).get() == "9"
        response.getAttribute(SUM_COLLECTIONS_1W).get() == "10"
        response.getAttribute(NUM_COLLECTIONS_12M).get() == "11"
        response.getAttribute(NUM_COLLECTIONS_3M).get() == "12"
    }

    def "Parsing expected values of Instantor Risk LowBalances"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(DAYS_BALANCE_BELOW_7_3M).get() == "1"
        response.getAttribute(DAYS_BALANCE_BELOW_0_1W).get() == "2"
        response.getAttribute(DAYS_BALANCE_BELOW_0_3M).get() == "3"
        response.getAttribute(DAYS_BALANCE_BELOW_7_1W).get() == "4"
        response.getAttribute(DAYS_BALANCE_BELOW_0_12M).get() == "5"
        response.getAttribute(DAYS_BALANCE_BELOW_0_6M).get() == "6"
        response.getAttribute(DAYS_BALANCE_BELOW_7_1M).get() == "7"
        response.getAttribute(DAYS_BALANCE_BELOW_7_TOTAL).get() == "8"
        response.getAttribute(DAYS_BALANCE_BELOW_0_TOTAL).get() == "9"
        response.getAttribute(DAYS_BALANCE_BELOW_0_1M).get() == "10"
        response.getAttribute(DAYS_BALANCE_BELOW_7_12M).get() == "11"
        response.getAttribute(DAYS_BALANCE_BELOW_7_6M).get() == "12"
    }

    def "Parsing expected values of Instantor Risk Loans"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(REPAYMENT_LOAN_RATIO_3M).get() == "1"
        response.getAttribute(SUM_REPAYMENTS_12M).get() == "2"
        response.getAttribute(REPAYMENT_LOAN_RATIO_TOTAL).get() == "3"
        response.getAttribute(SUM_REPAYMENTS_TOTAL).get() == "4"
        response.getAttribute(SUM_REPAYMENTS_1W).get() == "5"
        response.getAttribute(REPAYMENT_LOAN_RATIO_1W).get() == "6"
        response.getAttribute(SUM_LOANS_TOTAL).get() == "7"
        response.getAttribute(SUM_LOANS_3M).get() == "8"
        response.getAttribute(SUM_LOANS_1W).get() == "9"
        response.getAttribute(SUM_REPAYMENTS_3M).get() == "10"
        response.getAttribute(REPAYMENT_LOAN_RATIO_6M).get() == "11"
        response.getAttribute(SUM_LOANS_1M).get() == "12"
        response.getAttribute(REPAYMENT_LOAN_RATIO_12M).get() == "13"
        response.getAttribute(REPAYMENT_LOAN_RATIO_1M).get() == "14"
        response.getAttribute(SUM_LOANS_12M).get() == "15"
        response.getAttribute(SUM_REPAYMENTS_1M).get() == "16"
        response.getAttribute(SUM_REPAYMENTS_6M).get() == "17"
        response.getAttribute(SUM_LOANS_6M).get() == "18"
    }

    def "Parsing expected values of Instantor Risk SpendingDistribution"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(LAST_MONTH_SUM_1000_TO_2000).get() == "1"
        response.getAttribute(TREND_0_TO_5).get() == "2"
        response.getAttribute(LAST_MONTH_SUM_50_TO_100).get() == "3"
        response.getAttribute(LAST_MONTH_SUM_MINUS_5_TO_0).get() == "4"
        response.getAttribute(TREND_20_TO_50).get() == "5"
        response.getAttribute(TREND_MINUS_200_TO_MINUS_100).get() == "6"
        response.getAttribute(LAST_MONTH_SUM_2000_TO_INF).get() == "7"
        response.getAttribute(TREND_500_TO_1000).get() == "8"
        response.getAttribute(LAST_MONTH_SUM_500_TO_1000).get() == "9"
        response.getAttribute(LAST_MONTH_SUM_0_TO_5).get() == "10"
        response.getAttribute(LAST_MONTH_SUM_20_TO_50).get() == "11"
        response.getAttribute(LAST_MONTH_SUM_MINUS_50_TO_MINUS_20).get() == "12"
        response.getAttribute(TREND_MINUS_INF_TO_MINUS_2000).get() == "13"
        response.getAttribute(TREND_200_TO_500).get() == "14"
        response.getAttribute(TREND_MINUS_1000_TO_MINUS_500).get() == "15"
        response.getAttribute(TREND_MINUS_100_TO_MINUS_50).get() == "16"
        response.getAttribute(TREND_MINUS_10_TO_MINUS_5).get() == "17"
        response.getAttribute(TREND_10_TO_20).get() == "18"
        response.getAttribute(TREND_MINUS_2000_TO_MINUS_1000).get() == "19"
        response.getAttribute(LAST_MONTH_SUM_10_TO_20).get() == "20"
        response.getAttribute(LAST_MONTH_SUM_MINUS_200_TO_MINUS_100).get() == "21"
        response.getAttribute(LAST_MONTH_SUM_MINUS_2000_TO_MINUS_1000).get() == "22"
        response.getAttribute(LAST_MONTH_SUM_200_TO_500).get() == "23"
        response.getAttribute(LAST_MONTH_SUM_MINUS_INF_TO_MINUS_2000).get() == "24"
        response.getAttribute(TREND_5_TO_10).get() == "25"
        response.getAttribute(TREND_MINUS_50_TO_MINUS_20).get() == "26"
        response.getAttribute(TREND_100_TO_200).get() == "27"
        response.getAttribute(LAST_MONTH_SUM_5_TO_10).get() == "28"
        response.getAttribute(LAST_MONTH_SUM_100_TO_200).get() == "29"
        response.getAttribute(TREND_1000_TO_2000).get() == "30"
        response.getAttribute(TREND_50_TO_100).get() == "31"
        response.getAttribute(TREND_MINUS_500_TO_MINUS_200).get() == "32"
        response.getAttribute(LAST_MONTH_SUM_MINUS_1000_TO_MINUS_500).get() == "33"
        response.getAttribute(TREND_MINUS_20_TO_MINUS_10).get() == "34"
        response.getAttribute(LAST_MONTH_SUM_MINUS_20_TO_MINUS_10).get() == "35"
        response.getAttribute(LAST_MONTH_SUM_MINUS_500_TO_MINUS_200).get() == "36"
        response.getAttribute(TREND_MINUS_5_TO_0).get() == "37"
        response.getAttribute(LAST_MONTH_SUM_MINUS_10_TO_MINUS_5).get() == "38"
        response.getAttribute(TREND_2000_TO_INF).get() == "39"
        response.getAttribute(LAST_MONTH_SUM_MINUS_100_TO_MINUS_50).get() == "40"
    }

    def "Parsing expected values of Instantor Risk GamblingVsIncome"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(GAMBLING_INCOME_RATIO_3M).get() == "1"
        response.getAttribute(GAMBLING_INCOME_RATIO_1W).get() == "2"
        response.getAttribute(SUM_GAMBLING_1W).get() == "3"
        response.getAttribute(GAMBLING_INCOME_RATIO_TOTAL).get() == "4"
        response.getAttribute(GAMBLING_INCOME_RATIO_12M).get() == "5"
        response.getAttribute(SUM_GAMBLING_TOTAL).get() == "6"
        response.getAttribute(GAMBLING_INCOME_RATIO1M).get() == "7"
        response.getAttribute(SUM_GAMBLING_6M).get() == "8"
        response.getAttribute(SUM_GAMBLING_12M).get() == "9"
        response.getAttribute(GAMBLING_INCOME_RATIO_6M).get() == "10"
        response.getAttribute(SUM_GAMBLING_3M).get() == "11"
        response.getAttribute(SUM_GAMBLING_1M).get() == "12"
    }

    def "Parsing expected values of Instantor Risk AtmWithdrawals"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(ATM_EXPENSES_RATIO_TOTAL).get() == "1"
        response.getAttribute(SUM_ATM_WITHDRAWALS_6M).get() == "2"
        response.getAttribute(ATM_EXPENSES_RATIO_1W).get() == "3"
        response.getAttribute(ATM_EXPENSES_RATIO_1M).get() == "4"
        response.getAttribute(ATM_EXPENSES_RATIO_6M).get() == "5"
        response.getAttribute(SUM_ATM_WITHDRAWALS_3M).get() == "6"
        response.getAttribute(SUM_ATM_WITHDRAWALS_TOTAL).get() == "7"
        response.getAttribute(ATM_EXPENSES_RATIO_3M).get() == "8"
        response.getAttribute(SUM_ATM_WITHDRAWALS_1W).get() == "9"
        response.getAttribute(SUM_ATM_WITHDRAWALS_1M).get() == "10"
        response.getAttribute(SUM_ATM_WITHDRAWALS_12M).get() == "11"
        response.getAttribute(ATM_EXPENSES_RATIO_12M).get() == "12"
    }

    def "Parsing expected values of Instantor Risk MonthlyPaymentVariance"() {
        given:
        service.saveResponse(InsightInstantorSimulation.simulateOkResponse(123L, "111", "John", "ES1793 017144274569123112", "ES1793 017144274569123113"))

        when:
        def response = service.findLatest(new InstantorResponseQuery().setClientId(123L)).get()

        then:
        response.getAttribute(NUM_MONTHLY_EXPENSE_STREAMS).get() == "1"
        response.getAttribute(MAX_NORM_STD_MONTHLY_EXPENSE_STREAMS).get() == "2"
        response.getAttribute(MAX_STD_MONTHLY_EXPENSE_STREAMS).get() == "3"
        response.getAttribute(AVG_NORM_STD_MONTHLY_EXPENSE_STREAMS).get() == "4"
        response.getAttribute(AVG_STD_MONTHLY_EXPENSE_STREAMS).get() == "5"
        response.getAttribute(AVG_STD_TIMING).get() == "6"
        response.getAttribute(MAX_STD_TIMING).get() == "7"
    }
}
