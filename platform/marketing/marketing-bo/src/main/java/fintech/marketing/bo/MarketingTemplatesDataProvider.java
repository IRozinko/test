package fintech.marketing.bo;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.marketing.tables.MarketingTemplate;
import fintech.bo.db.jooq.marketing.tables.records.MarketingTemplateRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;


public class MarketingTemplatesDataProvider extends JooqDataProvider<MarketingTemplateRecord> {

    private String key;

    public MarketingTemplatesDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<MarketingTemplateRecord> buildSelect(Query<MarketingTemplateRecord, String> query) {
        SelectWhereStep<MarketingTemplateRecord> select = db
            .selectFrom(MarketingTemplate.MARKETING_TEMPLATE);

        if (key != null) {
            select.where(MarketingTemplate.MARKETING_TEMPLATE.NAME.likeIgnoreCase("%" + key + "%"));
        }
        return select;
    }

    @Override
    protected Object id(MarketingTemplateRecord item) {
        return item.getId();
    }


    public void setKey(String key) {
        this.key = key;
    }
}
