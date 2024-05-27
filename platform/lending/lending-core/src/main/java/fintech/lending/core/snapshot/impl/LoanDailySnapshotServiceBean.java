package fintech.lending.core.snapshot.impl;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQueryFactory;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.snapshot.LoanDailySnapshotService;
import fintech.lending.core.snapshot.db.LoanDailySnapshotEntity;
import fintech.lending.core.snapshot.db.LoanDailySnapshotRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.Optional;

import static fintech.DateUtils.date;
import static fintech.lending.core.db.Entities.loan;

@Slf4j
@Component
public class LoanDailySnapshotServiceBean implements LoanDailySnapshotService {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanDailySnapshotRepository loanDailySnapshotRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private JPQLQueryFactory queryFactory;

    @Transactional
    @Override
    public boolean makeSnapshot(Long loanId, LocalDate when) {
        LoanEntity loan = loanRepository.getRequired(loanId);
        if (when.isBefore(loan.getIssueDate())) {
            return false;
        }

        long alreadyHasSnapshotFromToday = loanDailySnapshotRepository.count(
            Entities.loanDailySnapshot.loanId.eq(loanId).and(Entities.loanDailySnapshot.effectiveFrom.goe(when)));
        if (alreadyHasSnapshotFromToday > 0) {
            return false;
        }

        Optional<LoanDailySnapshotEntity> previousMaybe = loanDailySnapshotRepository.getOptional(
            Entities.loanDailySnapshot.loanId.eq(loanId).and(Entities.loanDailySnapshot.latest.isTrue()));
        if (previousMaybe.isPresent()) {
            LoanDailySnapshotEntity previous = previousMaybe.get();
            previous.setEffectiveTo(when.minusDays(1));
            previous.setLatest(false);
            loanDailySnapshotRepository.saveAndFlush(previous);
        }

        LoanDailySnapshotEntity snapshot = new LoanDailySnapshotEntity();

        // calculated fields
        snapshot.setEffectiveFrom(when);
        snapshot.setEffectiveTo(date("2100-01-01"));
        snapshot.setLatest(true);

        // loan fields
        snapshot.setLoanId(loan.getId());
        snapshot.setStatus(loan.getStatus());
        snapshot.setStatusDetail(loan.getStatusDetail());
        snapshot.setProductId(loan.getProductId());
        snapshot.setClientId(loan.getClientId());
        snapshot.setLoanApplicationId(loan.getLoanApplicationId());
        snapshot.setIssueDate(loan.getIssueDate());
        snapshot.setCloseDate(loan.getCloseDate());
        snapshot.setCreditLimit(loan.getCreditLimit());
        snapshot.setMaturityDate(loan.getMaturityDate());
        snapshot.setPaymentDueDate(loan.getPaymentDueDate());
        snapshot.setNumber(loan.getNumber());
        snapshot.setLoansPaid(loan.getLoansPaid());
        snapshot.setPrincipalDisbursed(loan.getPrincipalDisbursed());
        snapshot.setPrincipalPaid(loan.getPrincipalPaid());
        snapshot.setPrincipalWrittenOff(loan.getPrincipalWrittenOff());
        snapshot.setPrincipalDue(loan.getPrincipalDue());
        snapshot.setPrincipalOutstanding(loan.getPrincipalOutstanding());
        snapshot.setInterestApplied(loan.getInterestApplied());
        snapshot.setInterestPaid(loan.getInterestPaid());
        snapshot.setInterestWrittenOff(loan.getInterestWrittenOff());
        snapshot.setInterestDue(loan.getInterestDue());
        snapshot.setInterestOutstanding(loan.getInterestOutstanding());
        snapshot.setPenaltyApplied(loan.getPenaltyApplied());
        snapshot.setPenaltyPaid(loan.getPenaltyPaid());
        snapshot.setPenaltyWrittenOff(loan.getPenaltyWrittenOff());
        snapshot.setPenaltyDue(loan.getPenaltyDue());
        snapshot.setPenaltyOutstanding(loan.getPenaltyOutstanding());
        snapshot.setFeeApplied(loan.getFeeApplied());
        snapshot.setFeePaid(loan.getFeePaid());
        snapshot.setFeeWrittenOff(loan.getFeeWrittenOff());
        snapshot.setFeeDue(loan.getFeeDue());
        snapshot.setFeeOutstanding(loan.getFeeOutstanding());
        snapshot.setTotalDue(loan.getTotalDue());
        snapshot.setTotalOutstanding(loan.getTotalOutstanding());
        snapshot.setCashIn(loan.getCashIn());
        snapshot.setCashOut(loan.getCashOut());
        snapshot.setInvoicePaymentDay(loan.getInvoicePaymentDay());
        snapshot.setId(loan.getId());
        snapshot.setOverdueDays(loan.getOverdueDays());
        snapshot.setMaxOverdueDays(loan.getMaxOverdueDays());

        loanDailySnapshotRepository.saveAndFlush(snapshot);
        return true;
    }

    @Transactional(propagation = Propagation.NEVER)
    @Override
    public void makeSnapshotOfAllLoans(LocalDate when, boolean force) {
        log.info("Creating loan daily snapshots");
        int total = 0;
        BooleanExpression predicate = loan.closeDate.isNull()
            .or(loan.closeDate.isNotNull().and(loan.updatedAt.goe(when.minusDays(force ? 36500 : 5).atStartOfDay())));
        try (CloseableIterator<Long> iterator = queryFactory.select(loan.id).from(loan).where(predicate).iterate()) {
            while (iterator.hasNext()) {
                Long loanId = iterator.next();
                if (transactionTemplate.execute(status -> makeSnapshot(loanId, when))) {
                    total++;
                    if (total % 1000 == 0) {
                        log.info("Created {} loan daily snapshots so far...", total);
                    }
                }
            }
        }
        log.info("Created total {} loan daily snapshots", total);
    }
}
