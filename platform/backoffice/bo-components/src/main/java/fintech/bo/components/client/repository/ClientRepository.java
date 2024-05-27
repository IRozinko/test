package fintech.bo.components.client.repository;

import com.vaadin.data.provider.Query;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqDataRepository;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.security.SecuredQuery;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectForUpdateStep;
import org.jooq.SelectOnConditionStep;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static fintech.bo.components.client.ClientDataProviderUtils.deletedClientCondition;
import static fintech.bo.components.client.ClientQueries.PHONE_TYPE_OTHER;
import static fintech.bo.components.loan.LoanConstants.STATUS_CLOSED;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_BROKEN_PAID;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_LEGAL_PAID;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_PAID;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_RENOUNCED_PAID;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_RESCHEDULED_PAID;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.crm.tables.EmailContact.EMAIL_CONTACT;
import static fintech.bo.db.jooq.crm.tables.PhoneContact.PHONE_CONTACT;
import static fintech.bo.db.jooq.lending.Tables.LOAN;
import static org.jooq.impl.DSL.count;
import static org.jooq.impl.DSL.select;

@Repository
@Transactional(readOnly = true)
public class ClientRepository extends JooqDataRepository<ClientDTO> {

    private final DSLContext jooq;
    private final JooqClientDataService jooqClientDataService;

    public ClientRepository(DSLContext jooq, JooqClientDataService jooqClientDataService) {
        super(jooq, ClientDTO.class);
        this.jooq = jooq;
        this.jooqClientDataService = jooqClientDataService;
    }

    @Override
    @SecuredQuery(permissions = BackofficePermissions.ADMIN, condition = "#deleted == true", template = "'soft_deleted_' + #id")
    public ClientDTO getRequired(Long id) {
        return jooq.select(CLIENT.fields())
            .select(EMAIL_CONTACT.EMAIL)
            .select(PHONE_CONTACT.LOCAL_NUMBER.as("additional_phone"))
            .select(select(count()).from(LOAN).where(LOAN.CLIENT_ID.eq(id).and(LOAN.STATUS.eq(STATUS_CLOSED).and(LOAN.STATUS_DETAIL.in(
                STATUS_DETAIL_PAID, STATUS_DETAIL_BROKEN_PAID, STATUS_DETAIL_RESCHEDULED_PAID, STATUS_DETAIL_LEGAL_PAID, STATUS_DETAIL_RENOUNCED_PAID
            )))).asField("paid_loans"))
            .from(CLIENT)
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY))
            .leftJoin(PHONE_CONTACT).on(PHONE_CONTACT.ID.eq(
                select(DSL.max(PHONE_CONTACT.ID))
                    .from(PHONE_CONTACT)
                    .where(PHONE_CONTACT.CLIENT_ID.eq(CLIENT.ID))
                    .and(PHONE_CONTACT.PHONE_TYPE.eq(PHONE_TYPE_OTHER))))
            .where(CLIENT.ID.eq(id))
            .limit(1)
            .fetchOneInto(ClientDTO.class);
    }

    @Override
    protected List<ClientDTO> runQuery(SelectForUpdateStep<Record> select) {
        return jooqClientDataService.runQueryIntoHidingDeletedClients(select);
    }

    @Override
    protected SelectOnConditionStep<Record> buildSelect(Query<ClientDTO, String> query) {
        SelectOnConditionStep<Record> select = jooq.select(CLIENT.fields())
            .select(EMAIL_CONTACT.EMAIL)
            .from(CLIENT)
            .leftJoin(EMAIL_CONTACT).on(EMAIL_CONTACT.CLIENT_ID.eq(CLIENT.ID).and(EMAIL_CONTACT.IS_PRIMARY));

        query.getFilter().ifPresent(filter -> applyFilter(select, filter));
        return select;
    }

    private void applyFilter(SelectOnConditionStep<Record> select, String filter) {
        List<Condition> conditions = new ArrayList<>();
        for (String fragment : StringUtils.split(filter, " ")) {
            conditions.add(
                CLIENT.CLIENT_NUMBER.likeIgnoreCase(fragment + "%")
                    .or(CLIENT.FIRST_NAME.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(CLIENT.LAST_NAME.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(CLIENT.SECOND_LAST_NAME.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(EMAIL_CONTACT.EMAIL.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(CLIENT.PHONE.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(CLIENT.DOCUMENT_NUMBER.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
                    .or(CLIENT.ACCOUNT_NUMBER.likeIgnoreCase("%" + fragment + "%").and(deletedClientCondition()))
            );
        }
        select.where(conditions);
    }

    @Override
    protected Object id(ClientDTO item) {
        return item.getId();
    }

}
