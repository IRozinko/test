package fintech.bo.components.client;

import fintech.bo.components.client.history.ClientDataHistory;
import fintech.bo.components.client.history.ClientDataHistoryRequest;
import fintech.bo.db.jooq.crm.tables.records.ClientAddressRecord;
import fintech.bo.db.jooq.crm.tables.records.ClientAttributeRecord;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.TimeMachine.today;
import static fintech.bo.db.jooq.crm.Crm.CRM;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.PhoneContact.PHONE_CONTACT;
import static fintech.bo.db.jooq.lending.tables.CreditLimit.CREDIT_LIMIT;
import static fintech.bo.db.jooq.transaction.Transaction.TRANSACTION;
import static org.jooq.impl.DSL.sum;

@Component
public class ClientQueries {

    public static final String PHONE_TYPE_MOBILE = "MOBILE";
    public static final String PHONE_TYPE_OTHER = "OTHER";

    private final DSLContext db;

    public ClientQueries(DSLContext db) {
        this.db = db;
    }

    public BigDecimal getClientOverpaymentAvailable(Long clientId) {
        return db.select(sum(
            TRANSACTION.TRANSACTION_.OVERPAYMENT_RECEIVED
                .minus(TRANSACTION.TRANSACTION_.OVERPAYMENT_USED)
                .minus(TRANSACTION.TRANSACTION_.OVERPAYMENT_REFUNDED)))
            .from(TRANSACTION.TRANSACTION_)
            .where(TRANSACTION.TRANSACTION_.CLIENT_ID.eq(clientId))
            .fetchOneInto(BigDecimal.class);
    }

    public BigDecimal getCreditLimit(Long clientId) {
        return db
            .select(CREDIT_LIMIT.CREDIT_LIMIT_)
            .from(CREDIT_LIMIT)
            .where(CREDIT_LIMIT.CLIENT_ID.eq(clientId)
                .and(CREDIT_LIMIT.ACTIVE_FROM.lessOrEqual(today())))
            .orderBy(CREDIT_LIMIT.ACTIVE_FROM.desc(), CREDIT_LIMIT.ID.desc())
            .limit(1)
            .fetchOneInto(BigDecimal.class);
    }

    public Optional<ClientAddressRecord> findPrimaryAddress(Long clientId) {
        return db.selectFrom(CRM.CLIENT_ADDRESS)
            .where(CRM.CLIENT_ADDRESS.CLIENT_ID.eq(clientId)
                .and(CRM.CLIENT_ADDRESS.IS_PRIMARY.isTrue()))
            .fetchOptional();
    }

    public Optional<Long> findByPhone(String phone) {
        return db.selectDistinct(CLIENT.ID)
            .from(CLIENT)
            .join(PHONE_CONTACT).on(CLIENT.ID.eq(PHONE_CONTACT.CLIENT_ID))
            .where(PHONE_CONTACT.LOCAL_NUMBER.eq(phone))
            .fetchOptionalInto(Long.class);
    }

    public Optional<PhoneContactRecord> findPrimaryPhone(Long clientId) {
        return Optional.ofNullable(db.selectFrom(CRM.PHONE_CONTACT)
            .where(CRM.PHONE_CONTACT.CLIENT_ID.eq(clientId)
                .and(CRM.PHONE_CONTACT.PHONE_TYPE.eq(PHONE_TYPE_MOBILE))
                .and(CRM.PHONE_CONTACT.IS_PRIMARY.isTrue()))
            .orderBy(CRM.PHONE_CONTACT.ID.desc())
            .limit(1)
            .fetchAny());
    }

    public <T extends Record> List<ClientDataHistory> findDataHistory(Long clientId, ClientDataHistoryRequest<T> request) {
        return db.fetchStream(request.getQuery(clientId))
            .map(request.getMapper())
            .collect(Collectors.toList());
    }

    public List<ClientAttributeRecord> getAttributes(Long clientId) {
        return db.selectFrom(CRM.CLIENT_ATTRIBUTE)
            .where(CRM.CLIENT_ATTRIBUTE.CLIENT_ID.eq(clientId))
            .fetchInto(ClientAttributeRecord.class);
    }
}
