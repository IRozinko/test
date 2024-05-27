package fintech.marketing.bo;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.marketing.Tables;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import java.util.Set;

import static fintech.bo.db.jooq.lending.Tables.PROMO_CODE;


public class MarketingCommunicationsDataProvider extends JooqDataProvider<Record> {

    public static final String CANCELLED = "CANCELLED";
    public static final String QUEUED = "QUEUED";
    public static final String SENT = "SENT";
    public static final String ERROR = "ERROR";

    private String key;
    private final Set<String> statuses;

    public MarketingCommunicationsDataProvider(DSLContext db, Set<String> statuses) {
        super(db);
        this.statuses = statuses;
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(
            JooqDataProvider.fields(
                Tables.MARKETING_COMMUNICATION.fields(), Tables.MARKETING_CAMPAIGN.NAME, Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE, PROMO_CODE.CODE
            )
        )
            .from(Tables.MARKETING_COMMUNICATION)
            .join(Tables.MARKETING_CAMPAIGN).on(Tables.MARKETING_COMMUNICATION.MARKETING_CAMPAIGN_ID.eq(Tables.MARKETING_CAMPAIGN.ID))
            .leftJoin(PROMO_CODE).on(PROMO_CODE.ID.eq(Tables.MARKETING_COMMUNICATION.PROMO_CODE_ID));
        if (!StringUtils.isBlank(key)) {
            select.where(
                Tables.MARKETING_CAMPAIGN.NAME.likeIgnoreCase("%" + key + "%")
            );
        }
        select.where(
            Tables.MARKETING_COMMUNICATION.STATUS.in(statuses)
        );
        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(Tables.MARKETING_COMMUNICATION.ID);
    }

    public void setKey(String key) {
        this.key = key;
    }
}
