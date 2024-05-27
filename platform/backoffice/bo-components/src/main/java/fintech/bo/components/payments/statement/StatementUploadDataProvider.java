package fintech.bo.components.payments.statement;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectWhereStep;
import org.jooq.impl.DSL;

import java.time.LocalDateTime;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;
import static fintech.bo.db.jooq.payment.tables.Statement.STATEMENT;
import static java.util.Arrays.asList;

public class StatementUploadDataProvider extends JooqDataProvider<Record> {

    public static final Field<LocalDateTime> LAST_STATEMENT_IMPORTED = DSL.select(DSL.max(STATEMENT.CREATED_AT))
            .from(STATEMENT)
            .where(STATEMENT.INSTITUTION_ID.equal(INSTITUTION.ID))
            .groupBy(STATEMENT.INSTITUTION_ID)
            .asField("last_statement_imported");

    public StatementUploadDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db.select(asList(INSTITUTION.ID, INSTITUTION.NAME, LAST_STATEMENT_IMPORTED)).from(INSTITUTION);

        select.where(INSTITUTION.STATEMENT_IMPORT_FORMAT.isNotNull());

        return select;
    }


    @Override
    protected Object id(Record item) {
        return item.get(INSTITUTION.ID);
    }

}
