package fintech.bo.components.risk.checklist;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.checklist.Tables.CHECKLIST_TYPE;
import static fintech.bo.db.jooq.checklist.tables.ChecklistEntry.CHECKLIST_ENTRY;

public class ChecklistDataProvider extends JooqDataProvider<Record> {

    private String typeFilter;

    private String textFilter;

    public ChecklistDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(CHECKLIST_ENTRY.fields(), CHECKLIST_TYPE.TYPE, CHECKLIST_TYPE.ACTION))
            .from(CHECKLIST_ENTRY
                .leftJoin(CHECKLIST_TYPE).on(CHECKLIST_ENTRY.TYPE.eq(CHECKLIST_TYPE.TYPE))
            );

        if (typeFilter != null) {
            select.where(CHECKLIST_TYPE.TYPE.eq(typeFilter));
        }

        if (textFilter != null) {
            select.where(CHECKLIST_ENTRY.VALUE1.likeIgnoreCase("%" + textFilter + "%"));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CHECKLIST_ENTRY.ID);
    }

    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

    public void setTextFilter(String textFilter) {
        this.textFilter = textFilter;
    }
}
