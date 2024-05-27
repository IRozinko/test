package fintech.transactions.db;


public abstract class Entities {

    public static final String SCHEMA = "transaction";

    public static final QTransactionEntity transaction = QTransactionEntity.transactionEntity;

    public static final QTransactionEntryEntity transactionEntry = QTransactionEntryEntity.transactionEntryEntity;
}
