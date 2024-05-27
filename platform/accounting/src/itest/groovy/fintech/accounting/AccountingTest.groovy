package fintech.accounting

import fintech.accounting.db.EntryRepository
import fintech.transactions.AddTransactionCommand
import fintech.transactions.TransactionType
import fintech.transactions.VoidTransactionCommand
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date
import static fintech.transactions.TransactionEntryType.FEE

class AccountingTest extends BaseSpecification {

    @Autowired
    EntryRepository entryRepository


    def "Registration fee example"() {
        given:
        def txId = bookTransaction()

        when:
        accountingService.book(new BookTransactionCommand(transactionId: txId, entries: [
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: BANK_A, entryType: EntryType.D, amount: 100.00g),
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: REGISTRATION_FEE, entryType: EntryType.C, amount: 50.00g),
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: ACCOUNTS_PAYABLE_PARTNER, entryType: EntryType.C, amount: 50.00g),
        ]))
        def turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[BANK_A].debit == 100.00g
        turnover[REGISTRATION_FEE].credit == 50.00g
        turnover[ACCOUNTS_PAYABLE_PARTNER].credit == 50.00g
    }

    def "Fail to add transaction with non equal debit/credit"() {
        given:
        def txId = bookTransaction()

        when:
        accountingService.book(new BookTransactionCommand(transactionId: txId, entries: [
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"),  accountCode: BANK_A, entryType: EntryType.D, amount: 100.00g)
        ]))

        then:
        thrown(IllegalArgumentException)

        when:
        accountingService.book(new BookTransactionCommand(transactionId: txId, entries: [
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: BANK_A, entryType: EntryType.D, amount: 100.00g),
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: REGISTRATION_FEE, entryType: EntryType.C, amount: 99.00g)
        ]))

        then:
        thrown(IllegalArgumentException)
    }

    def "Void transactions"() {
        given:
        def txId = bookTransaction()

        when:
        accountingService.book(new BookTransactionCommand(transactionId: txId, entries: [
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"), accountCode: BANK_A, entryType: EntryType.D, amount: 100.00g),
            new PostEntry(bookingDate: date("2016-01-01"), valueDate: date("2015-12-28"),accountCode: REGISTRATION_FEE, entryType: EntryType.C, amount: 100.00g),
        ]))
        def turnover = accountingReports.getTurnover(new ReportQuery())


        then:
        turnover[BANK_A].debit == 100.00g
        turnover[REGISTRATION_FEE].credit == 100.00g

        when:
        def voidTxId = transactionService.voidTransaction(new VoidTransactionCommand(id: txId, voidedDate: date("2016-01-01"), bookingDate: date("2016-01-01")))
        accountingService.bookVoid(transactionService.getTransaction(voidTxId))
        turnover = accountingReports.getTurnover(new ReportQuery())

        then:
        turnover[BANK_A].debit == 0.00g
        turnover[REGISTRATION_FEE].credit == 0.00g
    }

    def "Find account"() {
        expect:
        !accountingService.findAccount("unknown").isPresent()
        accountingService.findAccount(BANK_A).get().getCode() == BANK_A
    }

    private long bookTransaction() {
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            postDate: date("2016-01-01"),
            valueDate: date("2016-01-01"),
            bookingDate: date("2016-01-01"),
            cashIn: 100.00g,
            clientId: 1L,
            paymentId: 2L,
            institutionId: 3L,
            institutionAccountId: 4L,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 100.00, amountPaid: 100.00)]

        ))
    }
}
