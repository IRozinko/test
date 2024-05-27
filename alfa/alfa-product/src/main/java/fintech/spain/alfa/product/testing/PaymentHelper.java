package fintech.spain.alfa.product.testing;

import fintech.TimeMachine;
import fintech.lending.core.overpayment.OverpaymentService;
import fintech.lending.core.overpayment.RefundOverpaymentCommand;
import fintech.lending.core.payments.AddPaymentTransactionCommand;
import fintech.lending.core.payments.LendingPaymentsService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.commands.AddPaymentCommand;
import fintech.payments.model.Institution;
import fintech.payments.model.PaymentType;
import fintech.payments.spi.BatchPaymentAutoProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Component
public class PaymentHelper {

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BatchPaymentAutoProcessor batchPaymentAutoProcessor;

    @Autowired
    private LendingPaymentsService lendingPaymentsService;

    @Autowired
    private OverpaymentService overpaymentService;

    @Autowired
    private ApplicationContext applicationContext;

    public TestPayment newIncomingPayment(BigDecimal amount, LocalDate valueDate, String details) {
        Long id = paymentService.addPayment(new AddPaymentCommand()
            .setAccountId(getPrimaryAccountId())
            .setValueDate(valueDate)
            .setPaymentType(PaymentType.INCOMING)
            .setAmount(amount)
            .setDetails(details)
            .setKey(UUID.randomUUID().toString())
        );
        return testPayment(id);
    }

    public TestPayment newIncomingPayment(BigDecimal amount, LocalDate valueDate) {
        return newIncomingPayment(amount, valueDate, "");
    }

    public TestPayment newOutgoingPayment(BigDecimal amount, LocalDate valueDate) {
        return newOutgoingPayment(amount, valueDate, "");
    }

    public TestPayment newOutgoingPayment(BigDecimal amount, LocalDate valueDate, String details) {
        Long id = paymentService.addPayment(new AddPaymentCommand()
            .setAccountId(getPrimaryAccountId())
            .setValueDate(valueDate)
            .setPaymentType(PaymentType.OUTGOING)
            .setAmount(amount)
            .setDetails(details)
            .setKey(UUID.randomUUID().toString())
        );
        return testPayment(id);
    }

    public TestPayment newPayment(AddPaymentCommand command) {
        return testPayment(paymentService.addPayment(command));
    }

    public TestPayment testPayment(Long id) {
        return applicationContext.getBean(TestPayment.class, id);
    }

    public Long getPrimaryAccountId() {
        Institution primaryInstitution = institutionService.getPrimaryInstitution();
        return primaryInstitution.getPrimaryAccount().getId();
    }

    public Long getAccountId(String accountNumber) {
        return institutionService.findAccountByNumber(accountNumber).get().getId();
    }

    public void autoProcessPendingPayments() {
        batchPaymentAutoProcessor.autoProcessPending(100, TimeMachine.today());
    }

    public void autoProcessPendingPayments(LocalDate when) {
        batchPaymentAutoProcessor.autoProcessPending(100, when);
    }

    public Long addTransactionToPayment(Long paymentId, BigDecimal amount, String transactionSubType, Long clientId) {
        return lendingPaymentsService.addPaymentTransaction(new AddPaymentTransactionCommand(paymentId, amount, transactionSubType, clientId, ""));
    }

    public Long refundOverpayment(Long paymentId, Long clientId, Long loanId, BigDecimal amount) {
        RefundOverpaymentCommand command = RefundOverpaymentCommand.builder()
            .clientId(clientId)
            .paymentId(paymentId)
            .loanId(loanId)
            .amount(amount)
            .build();
        return overpaymentService.refundOverpayment(command);
    }
}
