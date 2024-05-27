package fintech.bo.spain.alfa.viventor;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ViventorLoanDataRecord;
import org.jooq.DSLContext;
import org.jooq.SelectWhereStep;

import static fintech.bo.spain.alfa.db.jooq.alfa.tables.ViventorLoanData.VIVENTOR_LOAN_DATA;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class ViventorLoanDataProvider extends JooqDataProvider<ViventorLoanDataRecord> {

    private Long loanId;

    private String status;

    public ViventorLoanDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<ViventorLoanDataRecord> buildSelect(Query<ViventorLoanDataRecord, String> query) {
        SelectWhereStep<ViventorLoanDataRecord> select = db.selectFrom(VIVENTOR_LOAN_DATA);
        if (loanId != null) {
            select.where(VIVENTOR_LOAN_DATA.LOAN_ID.eq(loanId));
        }
        if (isNotBlank(status)) {
            select.where(VIVENTOR_LOAN_DATA.STATUS.eq(status));
        }

        return select;
    }

    @Override
    protected Object id(ViventorLoanDataRecord item) {
        return item.getId();
    }

    public void setLoanId(Long loanId) {
        this.loanId = loanId;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
