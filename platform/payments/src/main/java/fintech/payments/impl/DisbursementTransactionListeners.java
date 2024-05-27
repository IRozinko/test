package fintech.payments.impl;

import com.google.common.collect.ImmutableList;
import fintech.Validate;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.transactions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.BigDecimalUtils.isZero;

@Component
public class DisbursementTransactionListeners {

    private final static List<TransactionType> TRANSACTION_TYPES_SUPPORTING_DISBURSEMENT = ImmutableList.of(TransactionType.DISBURSEMENT, TransactionType.DISBURSEMENT_SETTLEMENT, TransactionType.VOID_DISBURSEMENT, TransactionType.VOID_DISBURSEMENT_SETTLEMENT);
    
    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private TransactionService transactionService;

    @EventListener(TransactionAddedEvent.class)
    public void transactionAdded(TransactionAddedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getDisbursementId() == null) {
            return;
        }

        Disbursement disbursement = disbursementService.getDisbursement(transaction.getDisbursementId());
        Validate.isTrue(TRANSACTION_TYPES_SUPPORTING_DISBURSEMENT.contains(transaction.getTransactionType()), "Unsupported transaction type [%s] with disbursement [%s]", transaction.getTransactionType(), disbursement);
        
        if (TransactionType.DISBURSEMENT_SETTLEMENT == transaction.getTransactionType()) {
            handleDisbursementSettlement(transaction, disbursement);
        } else if (TransactionType.VOID_DISBURSEMENT_SETTLEMENT == transaction.getTransactionType()) {
            handleDisbursementSettlementVoid(transaction);
        }
            
    }

    private void handleDisbursementSettlementVoid(Transaction transaction) {
        Balance trBalance = transactionService.getBalance(BalanceQuery.byDisbursement(transaction.getDisbursementId()));
        if (!isZero(trBalance.getUnsettledDisbursement())) {
            disbursementService.revertSettled(transaction.getDisbursementId());
        }
    }

    private void handleDisbursementSettlement(Transaction transaction, Disbursement disbursement) {
        Balance trBalance = transactionService.getBalance(BalanceQuery.byDisbursement(transaction.getDisbursementId()));
        Validate.isZeroOrPositive(trBalance.getUnsettledDisbursement(), "Can't settle more than due: [%s]\"", disbursement);
        if (isZero(trBalance.getUnsettledDisbursement())) {
            disbursementService.settled(transaction.getDisbursementId());
        }
    }

}
