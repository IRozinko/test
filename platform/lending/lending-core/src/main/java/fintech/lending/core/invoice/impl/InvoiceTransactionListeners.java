package fintech.lending.core.invoice.impl;


import fintech.Validate;
import fintech.lending.core.invoice.InvoiceStatusDetail;
import fintech.lending.core.invoice.db.InvoiceEntity;
import fintech.lending.core.invoice.db.InvoiceItemEntity;
import fintech.lending.core.invoice.db.InvoiceItemType;
import fintech.lending.core.invoice.db.InvoiceRepository;
import fintech.lending.core.invoice.events.InvoiceClosedEvent;
import fintech.transactions.Balance;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionAddedEvent;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static fintech.BigDecimalUtils.isPositive;
import static fintech.transactions.TransactionQuery.byInvoice;
import static fintech.transactions.TransactionType.APPLY_PENALTY;
import static fintech.transactions.TransactionType.INVOICE;
import static fintech.transactions.TransactionType.REPAYMENT;
import static fintech.transactions.TransactionType.WRITE_OFF;

@Slf4j
@Component
public class InvoiceTransactionListeners {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @EventListener(TransactionAddedEvent.class)
    public void transactionAdded(TransactionAddedEvent event) {
        Transaction transaction = event.getTransaction();

        if (transaction.getInvoiceId() == null) {
            return;
        }

        process(transaction);
    }

    private void process(Transaction tx) {
        InvoiceEntity invoice = invoiceRepository.getRequired(tx.getInvoiceId());
        requireValidTransaction(tx, invoice);
        updatePaidAmounts(invoice);
        updateStatus(tx, invoice);
    }

    private void requireValidTransaction(Transaction tx, InvoiceEntity invoice) {
        Validate.isTrue(tx.getTransactionType() == REPAYMENT
                || tx.getTransactionType() == REPAYMENT.getVoidTransactionType()
                || tx.getTransactionType() == INVOICE
                || tx.getTransactionType() == INVOICE.getVoidTransactionType()
                || tx.getTransactionType() == APPLY_PENALTY
                || tx.getTransactionType() == APPLY_PENALTY.getVoidTransactionType()
                || tx.getTransactionType() == WRITE_OFF
                || tx.getTransactionType() == WRITE_OFF.getVoidTransactionType()
            , "Invalid transaction type for invoice");
        Validate.isZero(tx.getCashOut(), "Cash out not allowed for invoice transactions");

        Validate.isZero(tx.getPrincipalDisbursed(), "Principal disbursed not allowed");
        Validate.isZero(tx.getInterestApplied(), "Interest applied not allowed");
        Validate.isZero(tx.getFeeApplied(), "Fee applied not allowed");
        Validate.isZero(tx.getOverpaymentReceived(), "Overpayment received not allowed");
        Validate.isZero(tx.getOverpaymentRefunded(), "Overpayment refunded not allowed");
        Validate.isZero(tx.getOverpaymentReceived(), "Overpayment received not allowed");

        if (tx.getTransactionType() == INVOICE) {
            Validate.isZero(tx.getCashIn(), "Cash in not allowed for invoice transactions");
            Validate.isZero(tx.getPrincipalPaid(), "Principal paid not allowed for invoice transactions");
            Validate.isZero(tx.getInterestPaid(), "Interest paid not allowed for invoice transactions");
            Validate.isZero(tx.getPenaltyPaid(), "Penalty paid not allowed for invoice transactions");
            Validate.isZero(tx.getFeePaid(), "Fee paid not allowed for invoice transactions");
        } else if (tx.getTransactionType() == REPAYMENT) {
            Balance invoiceBalance = transactionService.getBalance(byInvoice(tx.getInvoiceId(), tx.getValueDate()));
            Validate.isLoe(invoiceBalance.getTotalPaid(), invoice.getTotal(), "Repayment amount [%s] exceeds invoice total amount [%s]");

            Validate.isTrue(isPositive(tx.getCashIn()) || isPositive(tx.getOverpaymentUsed()), "Cash in or overpayment used is required for invoice repayment transactions");
            Validate.isZero(tx.getPrincipalInvoiced(), "Principal invoiced not allowed for invoice repayment transactions");
            Validate.isZero(tx.getInterestInvoiced(), "Interest invoiced not allowed for invoice repayment transactions");
            Validate.isZero(tx.getPenaltyInvoiced(), "Penalty invoiced not allowed for invoice repayment transactions");
            Validate.isZero(tx.getFeeInvoiced(), "Fee invoiced not allowed for invoice repayment transactions");
        }
    }

    private void updatePaidAmounts(InvoiceEntity invoice) {
        Balance invoiceBalance = transactionService.getBalance(byInvoice(invoice.getId()));

        invoice.setTotal(invoiceBalance.getTotalInvoiced());
        invoice.setTotalPaid(invoiceBalance.getTotalPaid());

        for (InvoiceItemEntity invoiceItem : invoice.getItems()) {
            updateItemAmountPaid(invoiceItem, invoiceBalance);
        }
    }

    private void updateItemAmountPaid(InvoiceItemEntity invoiceItem, Balance invoiceBalance) {
        if (invoiceItem.getType() == InvoiceItemType.PRINCIPAL) {
            invoiceItem.setAmountPaid(invoiceBalance.getPrincipalPaid());
        } else if (invoiceItem.getType() == InvoiceItemType.INTEREST) {
            invoiceItem.setAmountPaid(invoiceBalance.getInterestPaid());
        } else if (invoiceItem.getType() == InvoiceItemType.FEE) {
            invoiceItem.setAmountPaid(invoiceBalance.getFeePaid());
        } else if (invoiceItem.getType() == InvoiceItemType.PENALTY) {
            invoiceItem.setAmountPaid(invoiceBalance.getPenaltyPaid());
        }
    }

    private void updateStatus(Transaction tx, InvoiceEntity invoiceEntity) {
        if (tx.getTransactionType() == TransactionType.REPAYMENT
            || tx.getTransactionType() == TransactionType.WRITE_OFF
            || tx.getTransactionType() == TransactionType.INVOICE) {
            if (invoiceEntity.isAmountPaid()) {
                invoiceEntity.close(InvoiceStatusDetail.PAID, tx.getPostDate());
                eventPublisher.publishEvent(new InvoiceClosedEvent(invoiceEntity.toValueObject()));
            } else if (invoiceEntity.isAmountPartiallyPaid()) {
                invoiceEntity.open(InvoiceStatusDetail.PARTIALLY_PAID);
            } else {
                invoiceEntity.open(InvoiceStatusDetail.PENDING);
            }
        } else if (tx.getTransactionType().equals(TransactionType.REPAYMENT.getVoidTransactionType())
            || tx.getTransactionType() == TransactionType.WRITE_OFF.getVoidTransactionType()) {
            if (invoiceEntity.isAmountPartiallyPaid()) {
                invoiceEntity.open(InvoiceStatusDetail.PARTIALLY_PAID);
            } else {
                invoiceEntity.open(InvoiceStatusDetail.PENDING);
            }
        }
    }

}
