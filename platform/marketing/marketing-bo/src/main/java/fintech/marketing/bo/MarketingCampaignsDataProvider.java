package fintech.marketing.bo;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.marketing.Tables;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;


public class MarketingCampaignsDataProvider extends JooqDataProvider<MarketingCampaignRecord> {

    private String key;
    private boolean automated;

    public MarketingCampaignsDataProvider(DSLContext db, boolean automated) {
        super(db);
        this.automated = automated;
    }

    @Override
    protected SelectWhereStep<MarketingCampaignRecord> buildSelect(Query<MarketingCampaignRecord, String> query) {
        SelectWhereStep<MarketingCampaignRecord> select = db.selectFrom(Tables.MARKETING_CAMPAIGN);
        select.where(
            automated ? Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE.isNotNull() : Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE.isNull()
        );
        if (!StringUtils.isBlank(key)) {
            select.where(
                Tables.MARKETING_CAMPAIGN.NAME.likeIgnoreCase("%" + key + "%").and(Tables.MARKETING_CAMPAIGN.SCHEDULE_TYPE.isNotNull())
            );
        }
        return select;
    }

    @Override
    protected Object id(MarketingCampaignRecord item) {
        return item.getId();
    }

    public void setKey(String key) {
        this.key = key;
    }
}
