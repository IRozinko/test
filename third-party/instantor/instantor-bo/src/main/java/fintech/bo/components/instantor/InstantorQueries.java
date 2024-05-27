package fintech.bo.components.instantor;

import org.jooq.DSLContext;
import org.jooq.Record1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static fintech.bo.db.jooq.instantor.tables.Transaction.TRANSACTION;

@Component
public class InstantorQueries {

    @Autowired
    private DSLContext db;

    public String findInstantorResponseAccountHolderName(Long clientId, String accountNumber) {
        return db.selectDistinct(TRANSACTION.ACCOUNT_HOLDER_NAME)
            .from(TRANSACTION)
            .where(TRANSACTION.CLIENT_ID.eq(clientId).and(TRANSACTION.ACCOUNT_NUMBER.eq(accountNumber))).stream()
            .map(Record1::value1)
            .collect(Collectors.joining(", "));
    }
}
