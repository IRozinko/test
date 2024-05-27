package fintech.spain.alfa.product.accounting.bookings;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fintech.BigDecimalUtils;
import fintech.accounting.AccountingService;
import fintech.accounting.BookTransactionCommand;
import fintech.accounting.EntryType;
import fintech.accounting.PostEntry;
import fintech.lending.creditline.TransactionConstants;
import fintech.payments.InstitutionService;
import fintech.spain.alfa.product.accounting.Accounts;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class TransactionBooking {

    private final Map<TransactionType, String> accountsByTransactionTypeForCash = new ImmutableMap.Builder<TransactionType, String>()
        .put(TransactionType.OVERPAYMENT, Accounts.OVERPAYMENT)
        .put(TransactionType.REFUND_OVERPAYMENT, Accounts.OVERPAYMENT)
        .put(TransactionType.DISBURSEMENT_SETTLEMENT, Accounts.FUNDS_IN_TRANSFER)
        .build();

    private final Map<String, String> accountsByTransactionSubTypeForCash = new ImmutableMap.Builder<String, String>()
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_PRINCIPAL_VIVENTOR, Accounts.VIVENTOR_PRINCIPAL)
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS, Accounts.UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS)
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_FAULTY_OUT, Accounts.FAULTY_OUT)
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_OTHER, Accounts.OTHER)
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER, Accounts.INTERCOMPANY_TRANSFER)
        .put(TransactionConstants.TRANSACTION_SUB_TYPE_BANK_COMMISSION, Accounts.ALL_TYPE_BANK_FEES)
        .build();

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private AccountingService accountingService;

    public void book(Transaction transaction) {
        List<PostEntry> entries = new ArrayList<>();

        if (transaction.getInstitutionAccountId() != null) {
            entries.add(debitBankAccount(transaction));
            entries.add(creditBankAccount(transaction));
        }

        if (BigDecimalUtils.isPositive(transaction.getOverpaymentUsed())) {
            entries.add(debit(transaction, Accounts.OVERPAYMENT, transaction.getOverpaymentUsed()));
        }

        String accountByTransactionType = accountsByTransactionTypeForCash.get(transaction.getTransactionType());
        if (accountByTransactionType != null) {
            entries.add(debit(transaction, accountByTransactionType, transaction.getCashOut()));
            entries.add(credit(transaction, accountByTransactionType, transaction.getCashIn()));
        }

        if (transaction.getTransactionType() != null) {
            String accountByTransactionSubType = accountsByTransactionSubTypeForCash.get(transaction.getTransactionSubType());
            if (accountByTransactionSubType != null) {
                entries.add(debit(transaction, accountByTransactionSubType, transaction.getCashOut()));
                entries.add(credit(transaction, accountByTransactionSubType, transaction.getCashIn()));
            }
        }

        // principal disbursed
        entries.add(debit(transaction, Accounts.LOANS_PRINCIPAL, transaction.getPrincipalDisbursed()));
        entries.add(credit(transaction, Accounts.FUNDS_IN_TRANSFER, transaction.getPrincipalDisbursed()));

        // principal paid
        entries.add(credit(transaction, Accounts.LOANS_PRINCIPAL, transaction.getPrincipalPaid()));

        // initial commission applied
        entries.add(debit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestApplied()));
        entries.add(credit(transaction, Accounts.INITIAL_COMMISSION, transaction.getInterestApplied()));

        // initial commission paid
        entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestPaid()));

        // penalty
        entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getPenaltyPaid()));
        entries.add(debit(transaction, Accounts.LOANS_CHARGED, transaction.getPenaltyPaid()));
        entries.add(credit(transaction, Accounts.PENALTIES, transaction.getPenaltyPaid()));

        // prolong
        if (transaction.getTransactionType() == TransactionType.LOAN_EXTENSION) {
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(debit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(credit(transaction, Accounts.PROLONGS, transaction.getFeePaid()));
        }

        // prepayment
        if (TransactionConstants.TRANSACTION_SUB_TYPE_PREPAYMENT.equals(transaction.getTransactionSubType())) {
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(debit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(credit(transaction, Accounts.PREPAYMENT_COMMISSION, transaction.getFeePaid()));
        }

        // reschedule
        if (TransactionConstants.TRANSACTION_SUB_TYPE_RESCHEDULE_REPAYMENT.equals(transaction.getTransactionSubType())) {
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(debit(transaction, Accounts.LOANS_CHARGED, transaction.getFeePaid()));
            entries.add(credit(transaction, Accounts.RESCHEDULE_COMMISSION, transaction.getFeePaid()));
        }

        // Write off sales portfolio
        if (transaction.getTransactionType() == TransactionType.SOLD_LOAN) {
            entries.add(debit(transaction, Accounts.WRITE_OFF_SALES_PORTFOLIO, transaction.getPrincipalWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_PRINCIPAL, transaction.getPrincipalWrittenOff()));

            entries.add(debit(transaction, Accounts.WRITE_OFF_SALES_PORTFOLIO, transaction.getInterestWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestWrittenOff()));
        } else {
            // write off
            entries.add(debit(transaction, Accounts.WRITE_OFF_PRINCIPAL, transaction.getPrincipalWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_PRINCIPAL, transaction.getPrincipalWrittenOff()));
        }

        // Write off commissions dc discount
        if (transaction.getTransactionType() == TransactionType.WRITE_OFF) {
            entries.add(debit(transaction, Accounts.WRITE_OFF_COMMISSIONS_DC_DISCOUNT, transaction.getInterestWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestWrittenOff()));
        }

        // Write off commissions early payment
        if (TransactionConstants.TRANSACTION_SUB_TYPE_PREPAYMENT.equals(transaction.getTransactionSubType())) {
            entries.add(debit(transaction, Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT, transaction.getInterestWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestWrittenOff()));
        }

        // Write off commissions renounce
        if (transaction.getTransactionType() == TransactionType.RENOUNCE_LOAN) {
            entries.add(debit(transaction, Accounts.WRITE_OFF_COMMISSIONS_EARLY_PAYMENT, transaction.getInterestWrittenOff()));
            entries.add(credit(transaction, Accounts.LOANS_CHARGED, transaction.getInterestWrittenOff()));
        }

         book(transaction, entries);
    }

    public void bookVoid(Transaction transaction) {
        accountingService.bookVoid(transaction);
    }

    PostEntry debitBankAccount(Transaction transaction) {
        PostEntry debit = new PostEntry();
        debit.setValueDate(transaction.getValueDate());
        debit.setBookingDate(transaction.getBookingDate());
        debit.setAccountCode(getAccountCode(transaction));
        debit.setEntryType(EntryType.D);
        debit.setAmount(transaction.getCashIn());
        return debit;
    }

    private PostEntry creditBankAccount(Transaction transaction) {
        PostEntry credit = new PostEntry();
        credit.setValueDate(transaction.getValueDate());
        credit.setBookingDate(transaction.getBookingDate());
        credit.setAccountCode(getAccountCode(transaction));
        credit.setEntryType(EntryType.C);
        credit.setAmount(transaction.getCashOut());
        return credit;
    }

    private void book(Transaction transaction, List<PostEntry> entries) {
        BookTransactionCommand command = new BookTransactionCommand();
        command.setTransactionId(transaction.getId());
        command.setEntries(Lists.newArrayList(entries));
        accountingService.book(command);
    }

    private PostEntry debit(Transaction transaction, String account, BigDecimal amount) {
        return entry(transaction, EntryType.D, account, amount);
    }

    private PostEntry credit(Transaction transaction, String account, BigDecimal amount) {
        return entry(transaction, EntryType.C, account, amount);
    }

    private PostEntry entry(Transaction transaction, EntryType debitOrCredit, String account, BigDecimal amount) {
        PostEntry entry = new PostEntry();
        entry.setValueDate(transaction.getValueDate());
        entry.setBookingDate(transaction.getBookingDate());
        entry.setAccountCode(account);
        entry.setEntryType(debitOrCredit);
        entry.setAmount(amount);
        return entry;
    }

    private String getAccountCode(Transaction transaction) {
        return institutionService.getAccount(transaction.getInstitutionAccountId()).getAccountingAccountCode();
    }

}
