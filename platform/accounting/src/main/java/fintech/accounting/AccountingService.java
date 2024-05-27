package fintech.accounting;


import fintech.transactions.Transaction;

import java.util.Optional;

public interface AccountingService {

    Long addAccount(AddAccountCommand command);

    void book(BookTransactionCommand command);

    Optional<Account> findAccount(String code);

    void bookVoid(Transaction transaction);

}
