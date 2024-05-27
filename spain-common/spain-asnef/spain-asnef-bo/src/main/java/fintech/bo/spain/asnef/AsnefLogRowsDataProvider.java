package fintech.bo.spain.asnef;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.components.client.JooqClientDataProvider;
import fintech.bo.components.client.JooqClientDataService;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.spain.db.jooq.asnef.tables.LogRow.LOG_ROW;

public class AsnefLogRowsDataProvider extends JooqClientDataProvider<Record> {

    private Long logId;

    public AsnefLogRowsDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db, jooqClientDataService);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(JooqDataProvider.fields(LOG_ROW.fields(), FIELD_CLIENT_NAME, LOAN.LOAN_NUMBER))
            .from(LOG_ROW)
            .join(CLIENT).on(LOG_ROW.CLIENT_ID.eq(CLIENT.ID))
            .join(LOAN).on(LOG_ROW.LOAN_ID.eq(LOAN.ID));

        if (logId != null) {
            select.where(LOG_ROW.LOG_ID.eq(logId));
        }

        select.orderBy(LOG_ROW.ID.desc());

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(LOG_ROW.ID);
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }
}
