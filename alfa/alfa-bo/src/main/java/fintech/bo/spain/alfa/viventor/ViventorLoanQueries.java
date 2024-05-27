package fintech.bo.spain.alfa.viventor;

import fintech.bo.spain.alfa.db.jooq.alfa.tables.records.ViventorLoanDataRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.spain.alfa.db.jooq.alfa.tables.ViventorLoanData.VIVENTOR_LOAN_DATA;

@Component
public class ViventorLoanQueries {

    @Autowired
    private DSLContext db;

    public ViventorLoanDataRecord findById(Long viventorLoanDataEntityId) {
        return db.selectFrom(VIVENTOR_LOAN_DATA).where(VIVENTOR_LOAN_DATA.ID.eq(viventorLoanDataEntityId)).fetchOne();
    }
}
