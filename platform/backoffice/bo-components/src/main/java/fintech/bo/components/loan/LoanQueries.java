package fintech.bo.components.loan;

import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.jooq.DSLContext;
import org.jooq.Record1;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static fintech.bo.db.jooq.crm.Tables.PHONE_CONTACT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@Component
public class LoanQueries {

    @Autowired
    private DSLContext db;

    public Result<LoanRecord> findOpen(Long clientId) {
        return db.selectFrom(LOAN).where(LOAN.CLIENT_ID.eq(clientId).and(LOAN.STATUS.eq(LoanConstants.STATUS_OPEN))).fetch();
    }

    public Result<LoanRecord> findNonBroken(Long clientId) {
        return db.selectFrom(LOAN).where(LOAN.CLIENT_ID.eq(clientId).and(LOAN.STATUS.eq(LoanConstants.STATUS_OPEN))
            .and(LOAN.STATUS_DETAIL.ne(LoanConstants.STATUS_DETAIL_BROKEN))).fetch();
    }

    public LoanRecord findById(Long loanId) {
        return db.selectFrom(LOAN).where(LOAN.ID.eq(loanId)).fetchOne();
    }

    public Optional<Long> findLoanIdByPhone(String phone) {
        return Optional.ofNullable(
            db.selectDistinct(LOAN.ID)
                .from(LOAN)
                .join(PHONE_CONTACT).on(LOAN.CLIENT_ID.eq(PHONE_CONTACT.CLIENT_ID))
                .where(PHONE_CONTACT.LOCAL_NUMBER.eq(phone))
                .orderBy(LOAN.ID.desc())
                .limit(1)
                .fetchOne())
            .map(Record1::value1);
    }

    @Cacheable(value = "loan_status", unless = "#result.size() == 0")
    public List<String> findStatuses() {
        return db.selectDistinct(LOAN.STATUS_DETAIL)
            .from(LOAN)
            .where(LOAN.STATUS_DETAIL.isNotNull())
            .fetch()
            .map(record -> record.get(LOAN.STATUS_DETAIL));
    }
}
