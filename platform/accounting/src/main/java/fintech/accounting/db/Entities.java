package fintech.accounting.db;

public abstract class Entities {

    public static final String SCHEMA = "accounting";

    public static final QAccountEntity account = QAccountEntity.accountEntity;
    public static final QEntryEntity entry = QEntryEntity.entryEntity;
}
