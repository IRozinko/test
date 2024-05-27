package fintech.transactions;


import java.util.List;
import java.util.Optional;

public interface TransactionService {

    Transaction getTransaction(Long id);

    Balance getBalance(TransactionQuery query);

    List<EntryBalance> getEntryBalance(TransactionEntryQuery query);

    Long addTransaction(AddTransactionCommand command);

    Long voidTransaction(VoidTransactionCommand command);

    Long voidDisbursementTransaction(long disbursementId, TransactionType type);

    Optional<Transaction> lastPaidTransaction(Long loanId);

    long countTransactions(TransactionQuery query);

    List<Transaction> findTransactions(TransactionQuery query);

    Optional<Transaction> findFirst(TransactionQuery query);

    List<Transaction> findTransactions(TransactionEntryQuery query);
}
