package fintech.bo.spain.alfa.marketing;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.crm.tables.records.MarketingConsentLogRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.Tables.MARKETING_CONSENT_LOG;

public class MarketingConsentLogProvider extends JooqDataProvider<MarketingConsentLogRecord> {

    private final long clientId;

    public MarketingConsentLogProvider(long clientId, DSLContext db) {
        super(db);
        this.clientId = clientId;
    }

    @Override
    protected SelectWhereStep<MarketingConsentLogRecord> buildSelect(Query<MarketingConsentLogRecord, String> query) {
        SelectWhereStep<MarketingConsentLogRecord> select = db.selectFrom(MARKETING_CONSENT_LOG);
        select.where(MARKETING_CONSENT_LOG.CLIENT_ID.eq(clientId));
        return select;
    }

    @Override
    protected Object id(MarketingConsentLogRecord item) {
        return item.getId();
    }
}
