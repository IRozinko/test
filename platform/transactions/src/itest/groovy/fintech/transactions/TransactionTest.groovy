package fintech.transactions

import static fintech.DateUtils.date
import static fintech.RandomUtils.randomId
import static fintech.transactions.TransactionEntryType.FEE

class TransactionTest extends BaseSpecification {


    def "Empty balance"() {
        expect:
        transactionService.getBalance(BalanceQuery.all()) == new Balance()
    }

    def "Add transaction"() {
        when:
        def txId = transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            transactionSubType: "SubType",
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            postDate: date("2001-01-02"),
            paymentId: 1L,
            institutionId: 2L,
            institutionAccountId: 3L,
            clientId: 4L,
            loanId: 5L,
            productId: 6L,
            disbursementId: 8L,
            principalDisbursed: 1.00g,
            principalPaid: 2.00g,
            principalWrittenOff: 3.00g,
            principalInvoiced: 0.5g,
            interestApplied: 4.00g,
            interestPaid: 5.00g,
            interestWrittenOff: 6.00g,
            interestInvoiced: 0.60g,
            penaltyApplied: 7.00g,
            penaltyPaid: 8.00g,
            penaltyWrittenOff: 9.00g,
            penaltyInvoiced: 0.90g,
            overpaymentReceived: 13.00g,
            overpaymentRefunded: 14.00g,
            overpaymentUsed: 15.00g,
            cashIn: 16.00g,
            cashOut: 17.00g,
            creditLimit: 500.00g,
            dpd: 10,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 10.00, amountPaid: 11.00, amountWrittenOff: 12.00, amountInvoiced: 1.20g)]
        ))

        then:
        with(transactionService.getTransaction(txId)) {
            transactionType == TransactionType.MANUAL
            transactionSubType == "SubType"
            valueDate == date("2001-01-02")
            postDate == date("2001-01-02")
            paymentId == 1L
            institutionId == 2L
            institutionAccountId == 3L
            clientId == 4L
            loanId == 5L
            productId == 6L
            disbursementId == 8L
            principalDisbursed == 1.00g
            principalPaid == 2.00g
            principalWrittenOff == 3.00g
            principalInvoiced == 0.5g
            interestApplied == 4.00g
            interestPaid == 5.00g
            interestWrittenOff == 6.00g
            interestInvoiced == 0.60g
            penaltyApplied == 7.00g
            penaltyPaid == 8.00g
            penaltyWrittenOff == 9.00g
            penaltyInvoiced == 0.90g
            feeApplied == 10.00g
            feePaid == 11.00g
            feeWrittenOff == 12.00g
            feeInvoiced == 1.20g
            overpaymentReceived == 13.00g
            overpaymentRefunded == 14.00g
            overpaymentUsed == 15.00g
            cashIn == 16.00g
            cashOut == 17.00g
            creditLimit == 500.00g
            dpd == 10
            entries.size() == 1
        }
    }

    def "add transaction with entries"() {
        given:
        def entry1 = new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
            amountApplied: 100.00, amountPaid: 10.00, amountWrittenOff: 20.00)
        def entry2 = new AddTransactionCommand.TransactionEntry(type: FEE, subType: "REGISTRATION_FEE",
            amountApplied: 100.00)
        def addTransactionCommand = new AddTransactionCommand(
            loanId: randomId(),
            entries: [entry1, entry2],
            transactionType: TransactionType.APPLY_FEE,
            transactionSubType: "SubType",
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            postDate: date("2001-01-02")
        )

        when:
        def txId = transactionService.addTransaction(addTransactionCommand)

        then:
        def entries = transactionService.getTransaction(txId).entries
        assert entries.size() == 2
        with (entries[0]) {
            assert type == entry1.type
            assert subType == entry1.subType
            assert amountApplied == entry1.amountApplied
            assert amountPaid == entry1.amountPaid
            assert amountWrittenOff == entry1.amountWrittenOff
        }
        with (entries[1]) {
            assert type == entry2.type
            assert subType == entry2.subType
            assert amountApplied == entry2.amountApplied
            assert amountPaid == entry2.amountPaid
            assert amountWrittenOff == entry2.amountWrittenOff
        }
    }

    def "Void transaction"() {
        given:
        def txId = transactionService.addTransaction(new AddTransactionCommand(
            loanId: randomId(),
            transactionType: TransactionType.MANUAL,
            transactionSubType: "SubType",
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            principalDisbursed: 1.00g,
            principalPaid: 2.00g,
            principalWrittenOff: 3.00g,
            principalInvoiced: 0.1g,
            interestApplied: 4.00g,
            interestPaid: 5.00g,
            interestWrittenOff: 6.00g,
            interestInvoiced: 0.60g,
            penaltyApplied: 7.00g,
            penaltyPaid: 8.00g,
            penaltyWrittenOff: 9.00g,
            penaltyInvoiced:  0.90g,
            overpaymentReceived: 13.00g,
            overpaymentRefunded: 14.00g,
            overpaymentUsed: 15.00g,
            cashIn: 16.00g,
            cashOut: 17.00g,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 10.00, amountPaid: 11.00, amountWrittenOff: 12.00, amountInvoiced: 1.20g)]

        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: randomId(),
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-03"),
            bookingDate: date("2001-01-03"),
            principalDisbursed: 1.00g,
            principalPaid: 2.00g,
            principalWrittenOff: 3.00g,
            principalInvoiced: 0.3g,
            interestApplied: 4.00g,
            interestPaid: 5.00g,
            interestWrittenOff: 6.00g,
            interestInvoiced: 0.30g,
            penaltyApplied: 7.00g,
            penaltyPaid: 8.00g,
            penaltyWrittenOff: 9.00g,
            penaltyInvoiced: 0.50g,
            overpaymentReceived: 13.00g,
            overpaymentRefunded: 14.00g,
            overpaymentUsed: 15.00g,
            cashIn: 16.00g,
            cashOut: 17.00g,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 10.00, amountPaid: 11.00, amountWrittenOff: 12.00, amountInvoiced: 1.00g)]
        ))

        expect:
        with(transactionService.getBalance(BalanceQuery.all())) {
            principalDisbursed == 1.00g * 2
            principalPaid == 2.00g * 2
            principalWrittenOff == 3.00g * 2
            principalInvoiced == 0.40g
            interestApplied == 4.00g * 2
            interestPaid == 5.00g * 2
            interestWrittenOff == 6.00g * 2
            interestInvoiced == 0.90g
            penaltyApplied == 7.00g * 2
            penaltyPaid == 8.00g * 2
            penaltyWrittenOff == 9.00g * 2
            penaltyInvoiced == 1.40g
            feeApplied == 10.00g * 2
            feePaid == 11.00g * 2
            feeWrittenOff == 12.00g * 2
            feeInvoiced == 2.20g
            overpaymentReceived == 13.00g * 2
            overpaymentRefunded == 14.00g * 2
            overpaymentUsed == 15.00g * 2
            cashIn == 16.00g * 2
            cashOut == 17.00g * 2
        }

        when:
        def voidTxId = transactionService.voidTransaction(new VoidTransactionCommand(id: txId, voidedDate: date("2001-01-01"), bookingDate: date("2001-01-01")))

        then:
        with(transactionService.getBalance(BalanceQuery.all())) {
            principalDisbursed == 1.00g
            principalPaid == 2.00g
            principalWrittenOff == 3.00g
            principalInvoiced == 0.30g
            interestApplied == 4.00g
            interestPaid == 5.00g
            interestWrittenOff == 6.00g
            interestInvoiced == 0.30g
            penaltyApplied == 7.00g
            penaltyPaid == 8.00g
            penaltyWrittenOff == 9.00g
            penaltyInvoiced == 0.50g
            feeApplied == 10.00g
            feePaid == 11.00g
            feeWrittenOff == 12.00g
            feeInvoiced == 1.00g
            overpaymentReceived == 13.00g
            overpaymentRefunded == 14.00g
            overpaymentUsed == 15.00g
            cashIn == 16.00g
            cashOut == 17.00g
        }

        and:
        with(transactionService.getTransaction(txId)) {
            voided
            voidedDate == date("2001-01-01")
            principalDisbursed == 1.00g
        }

        and:
        with(transactionService.getTransaction(voidTxId)) {
            voided
            valueDate == date("2001-01-02")
            voidedDate == date("2001-01-01")
            postDate == date("2001-01-01")
            principalDisbursed == -1.00g
            voidsTransactionId == txId
            transactionType == TransactionType.VOID_MANUAL
            transactionSubType == "SubType"
        }


        and:
        transactionService.countTransactions(TransactionQuery.builder().build()) == 3
        transactionService.countTransactions(TransactionQuery.builder().voided(true).build()) == 2
        transactionService.countTransactions(TransactionQuery.builder().voided(false).build()) == 1
        transactionService.countTransactions(TransactionQuery.builder().transactionType(TransactionType.MANUAL).build()) == 2
        transactionService.countTransactions(TransactionQuery.builder().transactionType(TransactionType.VOID_MANUAL).build()) == 1
        transactionService.findTransactions(TransactionQuery.builder().transactionType(TransactionType.VOID_MANUAL).build()).size() == 1
        transactionService.findTransactions(TransactionQuery.builder().transactionType(TransactionType.VOID_MANUAL).build()).get(0).id == voidTxId
    }

    def "void transaction with fee entries"() {
        given:
        def loanId = randomId()
        def entry1 = new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
            amountApplied: 100.00, amountPaid: 10.00, amountWrittenOff: 20.00, amountInvoiced: 1.00)
        def entry2 = new AddTransactionCommand.TransactionEntry(type: FEE, subType: "REGISTRATION_FEE",
            amountApplied: 20.00)
        def tx1 = transactionService.addTransaction(new AddTransactionCommand(
            loanId: loanId,
            entries: [entry1, entry2],
            transactionType: TransactionType.APPLY_FEE,
            transactionSubType: "SubType",
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            postDate: date("2001-01-02")
        ))

        and:
        def entry3 = new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
            amountApplied: 40.00, amountInvoiced: 15.00)
        def tx2 = transactionService.addTransaction(new AddTransactionCommand(
            loanId: loanId,
            entries: [entry3],
            transactionType: TransactionType.APPLY_FEE,
            transactionSubType: "SubType",
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            postDate: date("2001-01-02")
        ))

        expect:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(loanId, FEE, "SERVICING_FEE"))[0]) {
            assert amountApplied == 140.00
            assert amountPaid == 10.00
            assert amountWrittenOff == 20.00
            assert amountInvoiced == 16.00
            assert amountOutstanding == 110.00
        }

        when:
        transactionService.voidTransaction(new VoidTransactionCommand(tx1, date("2001-01-01"), date("2001-01-01")))

        then:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(loanId, FEE, "SERVICING_FEE"))[0]) {
            assert amountApplied == 40.00
            assert amountPaid == 0.00
            assert amountWrittenOff == 0.00
            assert amountOutstanding == 40.00
            assert amountInvoiced == 15.00
        }

        when:
        transactionService.voidTransaction(new VoidTransactionCommand(tx2, date("2001-01-01"), date("2001-01-01")))

        then:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(loanId, FEE, "SERVICING_FEE"))[0]) {
            assert amountApplied == 00.00
            assert amountPaid == 0.00
            assert amountWrittenOff == 0.00
            assert amountOutstanding == 0.00
            assert amountInvoiced == 0.00
        }
    }

    def "Loan balance"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            principalDisbursed: 100.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            principalPaid: 80.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            principalWrittenOff: 5.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountApplied: 1.00)]

        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            penaltyApplied: 0.50g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
        ))


        then:
        with(transactionService.getBalance(BalanceQuery.byLoan(1L))) {
            totalOutstanding == 16.50g
            principalDisbursed == 100.00g
            principalPaid == 80.00g
            principalWrittenOff == 5.00g
            feeOutstanding == 1.00g
            penaltyOutstanding == 0.50g
        }
        transactionService.getBalance(BalanceQuery.byLoan(2L)).totalOutstanding == 0.00g
    }

    def "Overpayment balance"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            clientId: 1L,
            overpaymentReceived: 100.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            clientId: 1L,
            overpaymentUsed: 80.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            clientId: 1L,
            overpaymentRefunded: 5.00g
        ))


        then:
        with(transactionService.getBalance(BalanceQuery.byClient(1L))) {
            overpaymentAvailable == 15.00g
            overpaymentReceived == 100.00g
            overpaymentUsed == 80.00g
            overpaymentRefunded == 5.00g
        }
        transactionService.getBalance(BalanceQuery.byClient(2L)).overpaymentAvailable == 0.00g
    }

    def "Fee balance"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountApplied: 100.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountPaid: 80.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountWrittenOff: 5.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountInvoiced: 1.00)]
        ))

        then:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(1L, FEE, "SERVICING_FEE"))[0]) {
            type == FEE
            subType == "SERVICING_FEE"
            amountOutstanding == 15.00g
            amountApplied == 100.00g
            amountPaid == 80.00g
            amountWrittenOff == 5.00g
            amountInvoiced == 1.00g
        }
    }

    def "Payment balance"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            paymentId: 1L,
            cashIn: 100.00g,
            cashOut: 200.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            paymentId: 1L,
            cashIn: 10.00g,
            cashOut: 20.00g
        ))

        then:
        with(transactionService.getBalance(BalanceQuery.byPayment(1L))) {
            cashIn == 110.00g
            cashOut == 220.00g
        }
        transactionService.getBalance(BalanceQuery.byPayment(2L)).overpaymentAvailable == 0.00g
    }

    def "Query by value date"() {
        given:
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: randomId(),
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountApplied: 1.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: randomId(),
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-03"),
            bookingDate: date("2001-01-03"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE", amountApplied: 2.00)]
        ))

        expect:
        transactionService.getBalance(BalanceQuery.builder().valueDateFrom(date("2001-01-01")).valueDateTo(date("2001-01-04")).build()).feeApplied == 3.00g
        transactionService.getBalance(BalanceQuery.builder().valueDateFrom(date("2001-01-01")).valueDateTo(date("2001-01-02")).build()).feeApplied == 1.00g
        transactionService.getBalance(BalanceQuery.builder().valueDateFrom(date("2001-01-03")).valueDateTo(date("2001-01-04")).build()).feeApplied == 2.00g
        transactionService.getBalance(BalanceQuery.builder().valueDateFrom(date("2001-01-01")).valueDateTo(date("2001-01-01")).build()).feeApplied == 0.00g
        transactionService.getBalance(BalanceQuery.builder().valueDateFrom(date("2001-01-04")).valueDateTo(date("2001-01-10")).build()).feeApplied == 0.00g
    }

    def "fee entry balance"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountApplied: 100.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountPaid: 80.00)]
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "SERVICING_FEE",
                amountWrittenOff: 5.00)]
        ))

        then:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(1L, FEE, "SERVICING_FEE"))[0]) {
            amountApplied == 100.00
            amountPaid == 80.00
            amountWrittenOff == 5.00
            amountOutstanding == 15.00
        }

        when:
        transactionService.addTransaction(new AddTransactionCommand(
            loanId: 1L,
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            entries: [new AddTransactionCommand.TransactionEntry(type: FEE, subType: "LATE_FEE",
                amountApplied: 99.00)]
        ))

        then:
        with(transactionService.getEntryBalance(TransactionEntryQuery.byLoan(1L, FEE, "LATE_FEE"))[0]) {
            amountApplied == 99.00
        }

        and:
        transactionService.getEntryBalance(TransactionEntryQuery.byLoan(1L, FEE)).size() == 2
    }


    def "Credit limit change"() {
        when:
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            creditLimit: 100.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            creditLimit: 80.00g
        ))
        transactionService.addTransaction(new AddTransactionCommand(
            transactionType: TransactionType.MANUAL,
            valueDate: date("2001-01-02"),
            bookingDate: date("2001-01-02"),
            loanId: 1L,
            creditLimit: -5.00g
        ))

        then:
        with(transactionService.getBalance(BalanceQuery.byLoan(1L))) {
            creditLimit == 175.00g
        }
    }
}
