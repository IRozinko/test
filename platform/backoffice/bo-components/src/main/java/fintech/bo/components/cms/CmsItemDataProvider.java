package fintech.bo.components.cms;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record6;
import org.jooq.SelectHavingStep;
import org.jooq.SelectJoinStep;
import org.jooq.SelectWhereStep;

import java.time.LocalDateTime;

import static fintech.bo.db.jooq.cms.Tables.ITEM;
import static org.jooq.impl.DSL.arrayAgg;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.firstValue;
import static org.jooq.impl.DSL.partitionBy;


public class CmsItemDataProvider extends JooqDataProvider<Record6<String, String, String, String[], String, LocalDateTime>> {

    public static final String ITEM_TYPE = "item_type";
    public static final String ITEM_KEY = "item_key";
    public static final String DESCRIPTION = "description";
    public static final String LOCALES = "locales";
    public static final String UPDATED_AT = "updated_at";
    public static final String UPDATED_BY = "updated_by";

    private static final String LOCALE = "locale";

    private String type;
    private String key;

    public CmsItemDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record6<String, String, String, String[], String, LocalDateTime>> buildSelect(
        Query<Record6<String, String, String, String[], String, LocalDateTime>, String> query) {

        Field<LocalDateTime> lastUpdatedAt = firstValue(ITEM.UPDATED_AT).over(partitionBy(ITEM.ITEM_KEY).orderBy(ITEM.UPDATED_AT.desc()));
        Field<String> lastUpdatedBy = firstValue(ITEM.UPDATED_BY).over(partitionBy(ITEM.ITEM_KEY).orderBy(ITEM.UPDATED_AT.desc()));

        SelectHavingStep<Record6<String, String, String, String, LocalDateTime, String>> subSelect = db.select(
            ITEM.ITEM_TYPE.as(ITEM_TYPE),
            ITEM.ITEM_KEY.as(ITEM_KEY),
            ITEM.DESCRIPTION.as(DESCRIPTION),
            ITEM.LOCALE.as(LOCALE),
            lastUpdatedAt.as(UPDATED_AT),
            lastUpdatedBy.as(UPDATED_BY)
        )
            .from(ITEM)
            .groupBy(ITEM.ITEM_TYPE, ITEM.ITEM_KEY, ITEM.DESCRIPTION, ITEM.LOCALE, ITEM.UPDATED_BY, ITEM.UPDATED_AT);

        Field<String> itemType = subSelect.field(ITEM_TYPE, String.class);
        Field<String> itemKey = subSelect.field(ITEM_KEY, String.class);
        Field<String> description = subSelect.field(DESCRIPTION, String.class);
        Field<String[]> locales = arrayAgg(subSelect.field(LOCALE, String.class)).as(LOCALES);
        Field<String> updatedBy = field(UPDATED_BY, String.class);
        Field<LocalDateTime> updatedAt = field(UPDATED_AT, LocalDateTime.class);

        SelectJoinStep<Record6<String, String, String, String[], String, LocalDateTime>> select = db.select(
            itemType, itemKey, description, locales, updatedBy, updatedAt
        ).from(subSelect);

        if (!StringUtils.isBlank(type)) {
            select.where(itemType.eq(type));
        }
        if (!StringUtils.isBlank(key)) {
            select.where(itemKey.likeIgnoreCase("%" + key + "%"));
        }

        select.groupBy(itemType, itemKey, description, updatedBy, updatedAt);

        return select;
    }

    @Override
    protected Object id(Record6<String, String, String, String[], String, LocalDateTime> record) {
        return record.get(ITEM_KEY);
    }


    public void setType(String type) {
        this.type = type;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
