package fintech.bo.components.settings;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.settings.Tables;
import fintech.bo.db.jooq.settings.tables.records.PropertyRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

public class PropertyDataProvider extends JooqDataProvider<PropertyRecord> {

    private String filterText;

    public PropertyDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<PropertyRecord> buildSelect(Query<PropertyRecord, String> query) {
        SelectWhereStep<PropertyRecord> select = db.selectFrom(Tables.PROPERTY);
        if (!StringUtils.isBlank(filterText)) {
            select.where(Tables.PROPERTY.NAME.likeIgnoreCase("%" + filterText + "%"));
        }
        return select;
    }

    @Override
    protected Object id(PropertyRecord item) {
        return item.getId();
    }

    public void setFilterText(String filterText) {
        this.filterText = filterText;
    }
}
