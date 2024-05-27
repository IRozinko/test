package fintech.accounting

import fintech.transactions.AddTransactionCommand
import fintech.transactions.TransactionType

import java.time.LocalDate

import static fintech.DateUtils.date
import static fintech.RandomUtils.randomId
import static fintech.transactions.TransactionEntryType.FEE

class AccountingReportsTest extends BaseSpecification {

    def "Account trial balance - all"() {
        given:
        book(date("2016-02-01"), date("2016-01-01"), ACCOUNT_A, ACCOUNT_B, 100.00g)
        book(date("2016-02-02"), date("2016-01-02"), ACCOUNT_B, ACCOUNT_C, 50.00g)

        when:
        def balance = accountingReports.getTrialBalance(new ReportQuery(postDateTo: date("2016-02-02"), bookingDateFrom: date("2016-01-01"), bookingDateTo: date("2016-01-02")))
        def balanceMap = balance.collectEntries { [it.accountCode, it] }

        then:
        balanceMap[ACCOUNT_A].openingDebit == 0.00g
        balanceMap[ACCOUNT_A].openingCredit == 0.00g
        balanceMap[ACCOUNT_A].turnoverDebit == 100.00g
        balanceMap[ACCOUNT_A].turnoverCredit == 0.00g
        balanceMap[ACCOUNT_A].closingDebit == 100.00g
        balanceMap[ACCOUNT_A].closingCredit == 0.00g

        and:
        balanceMap[ACCOUNT_B].openingDebit == 0.00g
        balanceMap[ACCOUNT_B].openingCredit == 0.00g
        balanceMap[ACCOUNT_B].turnoverDebit == 50.00g
        balanceMap[ACCOUNT_B].turnoverCredit == 100.00g
        balanceMap[ACCOUNT_B].closingDebit == 0.00g
        balanceMap[ACCOUNT_B].closingCredit == 50.00g

        and:
        balanceMap[ACCOUNT_C].openingDebit == 0.00g
        balanceMap[ACCOUNT_C].openingCredit == 0.00g
        balanceMap[ACCOUNT_C].turnoverDebit == 0.00g
        balanceMap[ACCOUNT_C].turnoverCredit == 50.00g
        balanceMap[ACCOUNT_C].closingDebit == 0.00g
        balanceMap[ACCOUNT_C].closingCredit == 50.00g
    }

    def "Account trial balance - post date limited"() {
        given:
        book(date("2016-02-01"), date("2016-01-01"), ACCOUNT_A, ACCOUNT_B, 100.00g)
        book(date("2016-02-02"), date("2016-01-02"), ACCOUNT_B, ACCOUNT_C, 50.00g)

        when:
        def balance = accountingReports.getTrialBalance(new ReportQuery(postDateTo: date("2016-02-01"), bookingDateFrom: date("2016-01-01"), bookingDateTo: date("2016-01-02")))
        def balanceMap = balance.collectEntries { [it.accountCode, it] }

        then:
        balanceMap[ACCOUNT_B].openingDebit == 0.00g
        balanceMap[ACCOUNT_B].openingCredit == 0.00g
        balanceMap[ACCOUNT_B].turnoverDebit == 00.00g
        balanceMap[ACCOUNT_B].turnoverCredit == 100.00g
        balanceMap[ACCOUNT_B].closingDebit == 0.00g
        balanceMap[ACCOUNT_B].closingCredit == 100.00g

        and:
        balanceMap[ACCOUNT_C] == null
    }

    def "Account trial balance - value date limited"() {
        given:
        book(date("2016-02-01"), date("2016-01-01"), ACCOUNT_A, ACCOUNT_B, 100.00g)
        book(date("2016-02-02"), date("2016-01-02"), ACCOUNT_B, ACCOUNT_C, 50.00g)

        when:
        def balance = accountingReports.getTrialBalance(new ReportQuery(postDateTo: date("2016-02-02"), bookingDateFrom: date("2016-01-02"), bookingDateTo: date("2016-01-02")))
        def balanceMap = balance.collectEntries { [it.accountCode, it] }

        then:
        balanceMap[ACCOUNT_A].openingDebit == 100.00g
        balanceMap[ACCOUNT_A].openingCredit == 0.00g
        balanceMap[ACCOUNT_A].turnoverDebit == 0.00g
        balanceMap[ACCOUNT_A].turnoverCredit == 0.00g
        balanceMap[ACCOUNT_A].closingDebit == 100.00g
        balanceMap[ACCOUNT_A].closingCredit == 0.00g

        and:
        balanceMap[ACCOUNT_B].openingDebit == 0.00g
        balanceMap[ACCOUNT_B].openingCredit == 100.00g
        balanceMap[ACCOUNT_B].turnoverDebit == 50.00g
        balanceMap[ACCOUNT_B].turnoverCredit == 0.00g
        balanceMap[ACCOUNT_B].closingDebit == 0.00g
        balanceMap[ACCOUNT_B].closingCredit == 50.00g
    }

    def "Account trial balance - filter by account"() {
        given:
        book(date("2016-02-01"), date("2016-01-01"), ACCOUNT_A, ACCOUNT_B, 100.00g)
        book(date("2016-02-02"), date("2016-01-02"), ACCOUNT_B, ACCOUNT_C, 50.00g)

        when:
        def balance = accountingReports.getTrialBalance(new ReportQuery(postDateTo: date("2016-02-02"), bookingDateFrom: date("2016-01-01"), bookingDateTo: date("2016-01-02"), accountCode: REGISTRATION_FEE))

        then:
        balance.size() == 1
        balance[0].accountCode == REGISTRATION_FEE

    }

    private void book(LocalDate postDate, LocalDate bookingDate, String debitAccount, String creditAccount, BigDecimal amount) {
        long txId = randomTransaction(postDate, bookingDate, amount)
        accountingService.book(new BookTransactionCommand(transactionId: txId, entries: [
            new PostEntry(bookingDate: bookingDate, valueDate: bookingDate.minusDays(3), accountCode: debitAccount, entryType: EntryType.D, amount: amount),
            new PostEntry(bookingDate: bookingDate, valueDate: bookingDate.minusDays(3), accountCode: creditAccount, entryType: EntryType.C, amount: amount),
        ]))
    }

    private long randomTransaction(LocalDate postDate, LocalDate bookingDate, BigDecimal amount,
                                   Long loanId = randomId(), Long paymentId = randomId(), Long clientId = randomId()) {
        return transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            postDate: postDate,
            valueDate: bookingDate.minusDays(2),
            bookingDate: bookingDate,
            cashIn: amount,
            clientId: clientId,
            loanId: loanId,
            paymentId: paymentId,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 0.00, amountPaid: amount)]

        ))
    }

}
