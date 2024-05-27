package fintech.instantor.db;

public class Entities {
    public static final String SCHEMA = "instantor";

    public static final fintech.instantor.db.QInstantorResponseEntity response = fintech.instantor.db.QInstantorResponseEntity.instantorResponseEntity;
    public static final QInstantorTransactionEntity transaction = QInstantorTransactionEntity.instantorTransactionEntity;
}
