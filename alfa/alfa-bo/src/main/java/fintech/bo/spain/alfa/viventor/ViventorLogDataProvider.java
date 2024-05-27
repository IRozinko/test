package fintech.bo.spain.alfa.viventor;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.db.jooq.viventor.tables.records.LogRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.viventor.Tables.LOG;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ViventorLogDataProvider extends JooqDataProvider<LogRecord> {

    private Long loanId;

    private String viventorLoanId;

    public ViventorLogDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<LogRecord> buildSelect(Query<LogRecord, String> query) {
        SelectWhereStep<LogRecord> select = db.selectFrom(LOG);
        if (loanId != null) {
            select.where(LOG.LOAN_ID.eq(loanId));
        }
        if (isNotBlank(viventorLoanId)) {
            select.where(LOG.VIVENTOR_LOAN_ID.eq(viventorLoanId));
        }
        return select;
    }

    @Override
    protected Object id(LogRecord item) {
        return item.getId();
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setViventorLoanId(String viventorLoanId) {
        this.viventorLoanId = viventorLoanId;
    }
}
