package fintech.bo.components.application;

import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import org.jooq.DSLContext;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.lending.tables.LoanApplication.LOAN_APPLICATION;

@Component
public class LoanApplicationQueries {

    @Autowired
    private DSLContext db;

    public Result<LoanApplicationRecord> findOpen(Long clientId) {
        return db.selectFrom(LOAN_APPLICATION).where(LOAN_APPLICATION.CLIENT_ID.eq(clientId).and(LOAN_APPLICATION.STATUS.eq(LoanApplicationConstants.STATUS_OPEN))).fetch();
    }

    public LoanApplicationRecord findById(Long id) {
        return db.selectFrom(LOAN_APPLICATION).where(LOAN_APPLICATION.ID.eq(id)).fetchOne();
    }

    public LoanApplicationRecord findLatestByClientId(Long clientId) {
        return db.selectFrom(LOAN_APPLICATION).where(LOAN_APPLICATION.CLIENT_ID.eq(clientId)).orderBy(LOAN_APPLICATION.ID.desc()).limit(1).fetchOne();
    }

    public List<LoanApplicationRecord> findByLoanId(Long loanId) {
        return db.selectFrom(LOAN_APPLICATION).where(LOAN_APPLICATION.LOAN_ID.eq(loanId)).orderBy(LOAN_APPLICATION.ID.desc()).fetch();
    }

    @Cacheable(value = "application_sources", unless = "#result.size() == 0")
    public List<String> findSourceNames() {
        return db.selectDistinct(LOAN_APPLICATION.SOURCE_NAME)
            .from(LOAN_APPLICATION)
            .where(LOAN_APPLICATION.SOURCE_NAME.isNotNull())
            .fetch()
            .map(record -> record.get(LOAN_APPLICATION.SOURCE_NAME));
    }

    @Cacheable(value = "application_types", unless = "#result.size() == 0")
    public List<String> findTypes() {
        return db.selectDistinct(LOAN_APPLICATION.TYPE)
            .from(LOAN_APPLICATION)
            .where(LOAN_APPLICATION.TYPE.isNotNull())
            .fetch()
            .map(record -> record.get(LOAN_APPLICATION.TYPE));
    }

    @Cacheable(value = "application_close_reasons", unless = "#result.size() == 0")
    public List<String> findCloseReasons() {
        return db.selectDistinct(LOAN_APPLICATION.CLOSE_REASON)
            .from(LOAN_APPLICATION)
            .where(LOAN_APPLICATION.TYPE.isNotNull().and(LOAN_APPLICATION.CLOSE_REASON.isNotNull()))
            .fetch()
            .map(record -> record.get(LOAN_APPLICATION.CLOSE_REASON));
    }
}
