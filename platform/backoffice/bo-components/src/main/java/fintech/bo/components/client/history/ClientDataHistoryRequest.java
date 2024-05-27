package fintech.bo.components.client.history;

import fintech.bo.db.jooq.crm.tables.records.EmailContactRecord;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import lombok.AllArgsConstructor;
import org.jooq.Record;
import org.jooq.ResultQuery;
import org.jooq.impl.DSL;

import java.util.function.Function;

import static fintech.bo.components.client.ClientQueries.PHONE_TYPE_MOBILE;
import static fintech.bo.components.client.ClientQueries.PHONE_TYPE_OTHER;
import static fintech.bo.db.jooq.crm.Crm.CRM;

@AllArgsConstructor
public class ClientDataHistoryRequest<T extends Record> {

    public static final ClientDataHistoryRequest<PhoneContactRecord> MOBILE_PHONE = new ClientDataHistoryRequest<>(
        id -> DSL.selectFrom(CRM.PHONE_CONTACT)
            .where(CRM.PHONE_CONTACT.CLIENT_ID.eq(id)
                .and(CRM.PHONE_CONTACT.PHONE_TYPE.eq(PHONE_TYPE_MOBILE)))
            .orderBy(CRM.PHONE_CONTACT.CREATED_AT.desc()),
        record -> new ClientDataHistory(record.getCreatedAt(), record.getLocalNumber())
    );

    public static final ClientDataHistoryRequest<PhoneContactRecord> OTHER_PHONE = new ClientDataHistoryRequest<>(
        id -> DSL.selectFrom(CRM.PHONE_CONTACT)
            .where(CRM.PHONE_CONTACT.CLIENT_ID.eq(id)
                .and(CRM.PHONE_CONTACT.PHONE_TYPE.eq(PHONE_TYPE_OTHER)))
            .orderBy(CRM.PHONE_CONTACT.CREATED_AT.desc()),
        record -> new ClientDataHistory(record.getCreatedAt(), record.getLocalNumber())
    );

    public static final ClientDataHistoryRequest<EmailContactRecord> EMAIL = new ClientDataHistoryRequest<>(
        id -> DSL.selectFrom(CRM.EMAIL_CONTACT)
            .where(CRM.EMAIL_CONTACT.CLIENT_ID.eq(id))
            .orderBy(CRM.EMAIL_CONTACT.CREATED_AT.desc()),
        record -> new ClientDataHistory(record.getCreatedAt(), record.getEmail())
    );

    private Function<Long, ResultQuery<T>> query;
    private Function<T, ClientDataHistory> mapper;

    public ResultQuery<T> getQuery(Long clientId) {
        return query.apply(clientId);
    }

    public Function<T, ClientDataHistory> getMapper() {
        return mapper;
    }
}
