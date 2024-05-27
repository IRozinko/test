package fintech.bo.api.server.services;


import fintech.TimeMachine;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.transaction.VoidTransactionRequest;
import fintech.dc.DcService;
import fintech.dc.model.Debt;
import fintech.lending.core.util.TransactionBuilder;
import fintech.transactions.Transaction;
import fintech.transactions.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@Transactional
public class TransactionApiService {

    private final TransactionService transactionService;
    private final TransactionBuilder transactionBuilder;
    private final DcService dcService;

    @Autowired
    public TransactionApiService(TransactionService transactionService, TransactionBuilder transactionBuilder, DcService dcService) {
        this.transactionService = transactionService;
        this.transactionBuilder = transactionBuilder;
        this.dcService = dcService;
    }

    public IdResponse voidTransaction(VoidTransactionRequest request) {
        long txId = transactionService.voidTransaction(
            transactionBuilder.voidCommand(request.getTransactionId(), TimeMachine.today()));

        Transaction transaction = transactionService.getTransaction(request.getTransactionId());
        // Avoiding of calling triggerActionsOnVoidTransaction for transactions without loans
        Optional.ofNullable(transaction.getLoanId())
            .flatMap(dcService::findByLoanId)
            .map(Debt::getId)
            .ifPresent(dcService::triggerActionsOnVoidTransaction);

        return new IdResponse(txId);
    }
}
