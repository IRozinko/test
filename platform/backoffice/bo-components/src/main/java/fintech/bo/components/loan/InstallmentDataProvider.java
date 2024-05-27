package fintech.bo.components.loan;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.lending.Tables.INSTALLMENT;


@Slf4j
public class InstallmentDataProvider extends JooqDataProvider<Record> {

    private Long contractId;

    public InstallmentDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(fields(INSTALLMENT.fields()))
            .from(INSTALLMENT);

        if (contractId != null) {
            select.where(INSTALLMENT.CONTRACT_ID.eq(contractId));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(INSTALLMENT.ID);
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

}
