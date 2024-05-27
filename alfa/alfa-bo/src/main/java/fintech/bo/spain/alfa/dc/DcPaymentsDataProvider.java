package fintech.bo.spain.alfa.dc;

import com.vaadin.data.provider.Query;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static fintech.bo.db.jooq.crm.Tables.CLIENT;
import static fintech.bo.db.jooq.payment.Payment.PAYMENT;
import static fintech.bo.db.jooq.payment.Tables.INSTITUTION;
import static fintech.bo.db.jooq.payment.Tables.PAYMENT_;
import static fintech.bo.db.jooq.payment.tables.InstitutionAccount.INSTITUTION_ACCOUNT;
import static fintech.bo.db.jooq.transaction.Tables.TRANSACTION_;

public class DcPaymentsDataProvider extends JooqClientDataProvider<Record> {

    public static final String TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS = "UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS";

    public static final Field<Long> TRANSACTION_ID = TRANSACTION_.ID.as("transaction_id");
    public static final Field<String> INSTITUTION_NAME = INSTITUTION.NAME.as("institution_name");

    private String textFilter;
    private LocalDate valueDate;
    private Long institutionId;
    private Long clientId;

    public DcPaymentsDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(
            fields(PAYMENT_.fields(),
                TRANSACTION_.CASH_IN,
                TRANSACTION_.TRANSACTION_SUB_TYPE,
                TRANSACTION_ID,
                PAYMENT_.ACCOUNT_ID,
                INSTITUTION_ACCOUNT.ACCOUNT_NUMBER,
                INSTITUTION_NAME,
                CLIENT.FIRST_NAME,
                CLIENT.LAST_NAME,
                CLIENT.DOCUMENT_NUMBER
            )
        ).from(PAYMENT_)
            .join(INSTITUTION_ACCOUNT).on(PAYMENT_.ACCOUNT_ID.eq(INSTITUTION_ACCOUNT.ID))
            .join(INSTITUTION).on(INSTITUTION_ACCOUNT.INSTITUTION_ID.eq(INSTITUTION.ID))
            .leftJoin(TRANSACTION_).on(TRANSACTION_.PAYMENT_ID.eq(PAYMENT_.ID))
            .leftJoin(CLIENT).on(TRANSACTION_.CLIENT_ID.eq(CLIENT.ID));

        select.where(TRANSACTION_.TRANSACTION_SUB_TYPE.eq(TRANSACTION_SUB_TYPE_UNIDENTIFIED_LIABILITIES_TO_CUSTOMERS));
        select.where(TRANSACTION_.VOIDED.isFalse());

        if (!StringUtils.isBlank(textFilter)) {
            applyFilter(select, textFilter);
        }
        if (valueDate != null) {
            select.where(PAYMENT.PAYMENT_.VALUE_DATE.eq(valueDate));
        }
        if (institutionId != null) {
            select.where(INSTITUTION_ACCOUNT.INSTITUTION_ID.eq(institutionId));
        }
        if (clientId != null) {
            select.where(TRANSACTION_.CLIENT_ID.eq(clientId));
        }
        return select;
    }

    private void applyFilter(SelectWhereStep<Record> select, String filter) {
        List<Condition> conditions = new ArrayList<>();
        for (String fragment : StringUtils.split(filter, " ")) {
            conditions.add(
                PAYMENT.PAYMENT_.DETAILS.likeIgnoreCase("%" + fragment + "%")
                    .or(PAYMENT.PAYMENT_.COUNTERPARTY_ACCOUNT.likeIgnoreCase("%" + fragment + "%"))
                    .or(PAYMENT.PAYMENT_.COUNTERPARTY_NAME.likeIgnoreCase("%" + fragment + "%"))
                    .or(PAYMENT.PAYMENT_.REFERENCE.likeIgnoreCase("%" + fragment + "%"))
            );
        }
        select.where(conditions);
    }

    @Override
    protected Object id(Record item) {
        return item.get(TRANSACTION_ID);
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }

    public void setValueDate(LocalDate valueDate) {
        this.valueDate = valueDate;
    }

    public void setInstitutionId(Long institutionId) {
        this.institutionId = institutionId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
