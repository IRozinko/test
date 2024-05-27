package fintech.bo.components.admintools;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.admin_tools.tables.records.LogRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.admin_tools.tables.Log.LOG;

public class AdminActionLogDataProvider extends JooqDataProvider<LogRecord> {

    public AdminActionLogDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<LogRecord> buildSelect(Query<LogRecord, String> query) {
        return db.selectFrom(LOG);
    }

    @Override
    protected Object id(LogRecord item) {
        return item.getId();
    }
}
