package fintech.bo.components.client;

import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectForUpdateStep;

public abstract class JooqClientDataProvider<T extends Record> extends JooqDataProvider<T> {

    private final JooqClientDataService jooqClientDataService;

    public JooqClientDataProvider(DSLContext db, JooqClientDataService jooqClientDataService) {
        super(db);
        this.jooqClientDataService = jooqClientDataService;
    }

    protected Result<T> runQuery(SelectForUpdateStep<T> select) {
        return jooqClientDataService.runQueryHidingDeletedClients(select);
    }
}
