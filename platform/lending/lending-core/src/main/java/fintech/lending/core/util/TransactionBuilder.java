package fintech.lending.core.util;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.loan.Loan;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.Payment;
import fintech.transactions.AddTransactionCommand;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import fintech.transactions.VoidTransactionCommand;
import fintech.transactions.spi.BookingDateResolver;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class TransactionBuilder {

    private final PaymentService paymentService;
    private final InstitutionService institutionService;
    private final DisbursementService disbursementService;
    private final TransactionService transactionService;
    private final BookingDateResolver bookingDateResolver;

    public TransactionBuilder(BookingDateResolver bookingDateResolver, PaymentService paymentService,
                              InstitutionService institutionService, DisbursementService disbursementService,
                              TransactionService transactionService) {
        this.bookingDateResolver = bookingDateResolver;
        this.paymentService = paymentService;
        this.institutionService = institutionService;
        this.disbursementService = disbursementService;
        this.transactionService = transactionService;
    }

    public void addPaymentValues(Long paymentId, AddTransactionCommand command) {
        Validate.notNull(paymentId);
        Validate.notNull(command);
        Payment payment = paymentService.getPayment(paymentId);
        InstitutionAccount account = institutionService.getAccount(payment.getAccountId());
        addPaymentValues(account, payment, command);
    }

    public void addDisbursementValues(Long disbursementId, AddTransactionCommand command) {
        Validate.notNull(disbursementId);
        Validate.notNull(command);
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        InstitutionAccount account = institutionService.getAccount(disbursement.getInstitutionAccountId());
        addDisbursementValues(account, disbursement, command);
    }

    public VoidTransactionCommand voidCommand(Long id, LocalDate voidedDate) {
        VoidTransactionCommand command = new VoidTransactionCommand();
        command.setId(id);
        command.setVoidedDate(voidedDate);
        Transaction tx = transactionService.getTransaction(command.getId());
        command.setBookingDate(bookingDateResolver.get(tx.getValueDate()));
        return command;
    }

    public void addPaymentValues(InstitutionAccount account, Payment payment, AddTransactionCommand command) {
        Validate.notNull(account);
        Validate.notNull(payment);
        Validate.notNull(command);
        command.setPaymentId(payment.getId());
        command.setInstitutionId(account.getInstitutionId());
        command.setInstitutionAccountId(account.getId());
        if (command.getValueDate() == null) {
            command.setValueDate(payment.getValueDate());
        }
        addBookingDate(command);
    }

    public void addDisbursementValues(InstitutionAccount account, Disbursement disbursement, AddTransactionCommand command) {
        Validate.notNull(account);
        Validate.notNull(disbursement);
        Validate.notNull(command);
        command.setDisbursementId(disbursement.getId());
        command.setInstitutionId(account.getInstitutionId());
        command.setInstitutionAccountId(account.getId());
        command.setValueDate(disbursement.getValueDate());
        addBookingDate(command);
    }

    public void addLoanValues(Loan loan, AddTransactionCommand command) {
        Validate.notNull(loan);
        Validate.notNull(command);
        command.setLoanId(loan.getId());
        command.setProductId(loan.getProductId());
        command.setClientId(loan.getClientId());
        command.setDpd(loan.getOverdueDays());
        addBookingDate(command);
    }

    private void addBookingDate(AddTransactionCommand command) {
        if (command.getValueDate() == null) {
            command.setValueDate(TimeMachine.today());
        }
        Validate.notNull(command.getValueDate());
        command.setBookingDate(bookingDateResolver.get(command.getValueDate()));
    }

}
