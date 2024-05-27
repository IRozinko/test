package fintech.spain.alfa.product.workflow.undewrtiting.handlers

import fintech.lending.core.loan.Loan
import fintech.spain.alfa.product.settings.AlfaSettings
import spock.lang.Specification

import static fintech.DateUtils.date

class IsGoodClientTest extends Specification {

    def "is good client"() {
        given:
        def settings = new AlfaSettings.GoodClientSettings()
            .setMaxDpd(60)
            .setMinNumberOfPaidLoans(3)
            .setMinRepaidPrincipalAmount(300.00)
            .setMonthsToCheckDpd(12)

        expect: "good client"
        assert IsGoodClient.isGoodClient(settings, [
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(0)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(12))
        ], date("2018-01-01"))

        and: "no loans"
        assert !IsGoodClient.isGoodClient(settings, [], date("2018-01-01"))

        and: "not enough paid loans"
        assert !IsGoodClient.isGoodClient(settings, [
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(0)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6))
        ], date("2018-01-01"))

        and: "too many over due days"
        assert !IsGoodClient.isGoodClient(settings, [
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(0)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6)),
            new Loan(overdueDays: 61, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(12))
        ], date("2018-01-01"))

        and: "ignore loan before check period"
        assert IsGoodClient.isGoodClient(settings, [
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(0)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6)),
            new Loan(overdueDays: 61, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(12).minusDays(2))
        ], date("2018-01-01"))

        and: "too little paid"
        assert !IsGoodClient.isGoodClient(settings, [
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(0)),
            new Loan(overdueDays: 60, principalPaid: 100.00, closeDate: date("2018-01-01").minusMonths(6)),
            new Loan(overdueDays: 60, principalPaid: 99.00, closeDate: date("2018-01-01").minusMonths(12))
        ], date("2018-01-01"))

    }
}
