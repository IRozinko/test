package fintech.bo.spain.asnef;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.spain.db.jooq.asnef.tables.records.LogRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.db.jooq.asnef.tables.Log.LOG;
import static fintech.bo.spain.db.jooq.asnef.tables.LogRow.LOG_ROW;

public class AsnefLogsDataProvider extends JooqDataProvider<LogRecord> {

    private String type;

    private String status;

    private Long clientId;

    public AsnefLogsDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<LogRecord> buildSelect(Query<LogRecord, String> query) {
        SelectWhereStep<LogRecord> select = db.selectFrom(LOG);

        if (StringUtils.isNotBlank(type)) {
            select.where(LOG.TYPE.eq(type));
        }

        if (StringUtils.isNotBlank(status)) {
            select.where(LOG.STATUS.eq(status));
        }

        if (clientId != null) {
            select.where(LOG.ID.in(db.select(LOG_ROW.LOG_ID).from(LOG_ROW).where(LOG_ROW.CLIENT_ID.eq(clientId))));
        }

        select.orderBy(LOG.ID.desc());

        return select;
    }

    @Override
    protected Object id(LogRecord item) {
        return item.getId();
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
