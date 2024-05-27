package fintech.payments

import fintech.TimeMachine
import fintech.payments.commands.AddPaymentCommand
import fintech.payments.model.Payment
import fintech.payments.model.PaymentAutoProcessingResult
import fintech.payments.model.PaymentStatusDetail
import fintech.payments.model.PaymentType
import fintech.payments.spi.BatchPaymentAutoProcessor
import fintech.payments.spi.PaymentAutoProcessor
import fintech.payments.spi.PaymentAutoProcessorRegistry
import fintech.transactions.AddTransactionCommand
import fintech.transactions.TransactionService
import fintech.transactions.TransactionType
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate

import static fintech.DateUtils.date
import static fintech.RandomUtils.randomId
import static fintech.transactions.TransactionEntryType.FEE

class PaymentAutoProcessingTest extends BaseSpecification {

    static final BigDecimal PAYMENT_AMOUNT = 10.0000

    @Autowired
    PaymentAutoProcessorRegistry autoProcessorRegistry

    @Autowired
    BatchPaymentAutoProcessor batchPaymentAutoProcessor

    @Autowired
    TransactionService transactionService

    def cleanup() {
        autoProcessorRegistry.removeProcessors()
    }

    def "Auto process moves to MANUAL when no amount processed"() {
        given:
        def id = paymentService.addPayment(addPaymentCommand())

        when:
        paymentService.autoProcess(id, TimeMachine.today())

        then:
        with(paymentService.getPayment(id)) {
            statusDetail == PaymentStatusDetail.MANUAL
            pendingAmount == PAYMENT_AMOUNT
        }
    }

    def "Auto process all amount"() {
        given:
        autoProcessorRegistry.addProcessor(processor(PAYMENT_AMOUNT))
        def id = paymentService.addPayment(addPaymentCommand())

        when:
        paymentService.autoProcess(id, TimeMachine.today())

        then:
        with(paymentService.getPayment(id)) {
            statusDetail == PaymentStatusDetail.PROCESSED
            pendingAmount == 0.0000
        }
    }

    def "Auto process part of amount"() {
        given:
        autoProcessorRegistry.addProcessor(processor(PAYMENT_AMOUNT - 1.0001))
        def id = paymentService.addPayment(addPaymentCommand())

        when:
        paymentService.autoProcess(id, TimeMachine.today())

        then:
        with(paymentService.getPayment(id)) {
            statusDetail == PaymentStatusDetail.MANUAL
            pendingAmount == 1.0001
        }
    }

    def "Stop after auto processor returns transactions"() {
        given:
        autoProcessorRegistry.addProcessor(processor(PAYMENT_AMOUNT - 1.0000))
        autoProcessorRegistry.addProcessor(processor(PAYMENT_AMOUNT))
        def id = paymentService.addPayment(addPaymentCommand())

        when:
        paymentService.autoProcess(id, TimeMachine.today())

        then:
        with(paymentService.getPayment(id)) {
            statusDetail == PaymentStatusDetail.MANUAL
            pendingAmount == 1.0000
        }
    }

    def "Batch processing"() {
        given:
        def id1 = paymentService.addPayment(addPaymentCommand())
        def id2 = paymentService.addPayment(addPaymentCommand())
        def id3 = paymentService.addPayment(addPaymentCommand())
        autoProcessorRegistry.addProcessor(processor(PAYMENT_AMOUNT))

        when:
        batchPaymentAutoProcessor.autoProcessPending(2, TimeMachine.today())

        then:
        paymentService.getPayment(id1).statusDetail == PaymentStatusDetail.PROCESSED
        paymentService.getPayment(id2).statusDetail == PaymentStatusDetail.PROCESSED
        paymentService.getPayment(id3).statusDetail == PaymentStatusDetail.PENDING

        when:
        batchPaymentAutoProcessor.autoProcessPending(2, TimeMachine.today())

        then:
        paymentService.getPayment(id3).statusDetail == PaymentStatusDetail.PROCESSED
    }

    private PaymentAutoProcessor processor(BigDecimal amount) {
        new PaymentAutoProcessor() {
            @Override
            PaymentAutoProcessingResult autoProcessPayment(Payment payment, LocalDate when) {
                transactionService.addTransaction(new AddTransactionCommand(
                    loanId: randomId(),
                    transactionType: TransactionType.REPAYMENT,
                    cashIn: amount,
                    paymentId: payment.getId(),
                    institutionAccountId: payment.accountId,
                    institutionId: institution.id,
                    valueDate: payment.valueDate,
                    bookingDate: payment.valueDate,
                    entries: [new AddTransactionCommand.TransactionEntry(
                        type: FEE, subType: "any fee", amountPaid: amount)]
                ))
                return PaymentAutoProcessingResult.processed()
            }
        }
    }

    private AddPaymentCommand addPaymentCommand() {
        return new AddPaymentCommand(
            accountId: institution.accounts[0].id,
            paymentType: PaymentType.INCOMING,
            details: "Repayment",
            reference: "REF",
            amount: PAYMENT_AMOUNT,
            key: RandomStringUtils.randomAlphanumeric(10),
            valueDate: date("2016-01-01"),
        )
    }
}
