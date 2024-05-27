package fintech.lending.creditline.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.BigDecimalUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.db.Entities;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import fintech.lending.core.loan.LoanDerivedValuesResolver;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.db.InstallmentEntity;
import fintech.lending.core.loan.db.InstallmentRepository;
import fintech.lending.core.loan.db.LoanEntity;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.loan.events.LoanDerivedValuesUpdated;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.transactions.Balance;
import fintech.transactions.BalanceQuery;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.isPositive;
import static fintech.transactions.db.Entities.transaction;
import static org.apache.commons.lang3.ObjectUtils.firstNonNull;

@Component
@Deprecated //TODO remove this and move all tests calling this to spain-product module
/**
 * Like the CreditLineLoanDerivedValuesResolver, copied here to keep tests running
 */
public class TestLoanDerivedValuesResolver implements LoanDerivedValuesResolver {

    private final TransactionService transactionService;
    private final JPAQueryFactory queryFactory;
    private final LoanRepository loanRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final InstallmentRepository installmentRepository;
    private final ScheduleService scheduleService;
    private final DisbursementService disbursementService;

    @Autowired
    public TestLoanDerivedValuesResolver(TransactionService transactionService, JPAQueryFactory queryFactory, LoanRepository loanRepository, ApplicationEventPublisher eventPublisher, InstallmentRepository installmentRepository, ScheduleService scheduleService, DisbursementService disbursementService) {
        this.transactionService = transactionService;
        this.queryFactory = queryFactory;
        this.loanRepository = loanRepository;
        this.eventPublisher = eventPublisher;
        this.installmentRepository = installmentRepository;
        this.scheduleService = scheduleService;
        this.disbursementService = disbursementService;
    }

    @Override
    @Transactional
    public void resolveDerivedValues(Long loanId) {
        resolveDerivedValues(loanId, null, null, TimeMachine.today());
    }

    @Override
    @Transactional
    public void resolveDerivedValues(Long loanId,String state,String status, LocalDate when) {
        List<Installment> installments = scheduleService.findInstallments(InstallmentQuery.allLoanInstallments(loanId));
        for (Installment installment : installments) {
            resolveInstallmentDerivedValues(installment.getId(), when);
        }

        resolveLoanDerivedValues(loanId,state, status, when);
    }

    @Override
    public void validateLoanBalance(Long loanId, Balance balance) {
        Validate.isLoe(balance.getCashOut(), balance.getPrincipalDisbursed().add(balance.getOverpaymentRefunded()), "Cash out become more than principal disbursed + overpayment refunded, loan: [%s]", loanId);

        BigDecimal expectedCashIn = balance.getTotalPaid().add(balance.getOverpaymentReceived()).subtract(balance.getOverpaymentUsed());
        Validate.isEqual(balance.getCashIn(), expectedCashIn, "Cash in [%s] is not equal to total paid + overpayment received - overpayment used [%s], loan: [%s]", balance.getCashIn(), expectedCashIn, loanId);

        Validate.isZeroOrPositive(balance.getPrincipalDisbursed(), "Principal disbursed becomes negative [%s], loan: [%s]", balance.getPrincipalDisbursed(), loanId);
        Validate.isZeroOrPositive(balance.getInterestApplied(), "Interest applied becomes negative [%s], loan: [%s]", balance.getInterestApplied(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyApplied(), "Penalty applied becomes negative [%s], loan: [%s]", balance.getPenaltyApplied(), loanId);
        Validate.isZeroOrPositive(balance.getFeeApplied(), "Fee applied becomes negative [%s], loan: [%s]", balance.getFeeApplied(), loanId);

        Validate.isZeroOrPositive(balance.getPrincipalInvoiced(), "Principal invoiced becomes negative [%s], loan: [%s]", balance.getPrincipalInvoiced(), loanId);
        Validate.isZeroOrPositive(balance.getInterestInvoiced(), "Interest invoiced becomes negative [%s], loan: [%s]", balance.getInterestInvoiced(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyInvoiced(), "Penalty invoiced becomes negative [%s], loan: [%s]", balance.getPenaltyInvoiced(), loanId);
        Validate.isZeroOrPositive(balance.getFeeInvoiced(), "Fee invoiced becomes negative [%s], loan: [%s]", balance.getFeeInvoiced(), loanId);

        Validate.isZeroOrPositive(balance.getPrincipalPaid(), "Principal paid becomes negative [%s], loan: [%s]", balance.getPrincipalPaid(), loanId);
        Validate.isZeroOrPositive(balance.getInterestPaid(), "Interest paid becomes negative [%s], loan: [%s]", balance.getInterestPaid(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyPaid(), "Penalty paid becomes negative [%s], loan: [%s]", balance.getPenaltyPaid(), loanId);
        Validate.isZeroOrPositive(balance.getFeePaid(), "Fee paid becomes negative [%s], loan: [%s]", balance.getFeePaid(), loanId);

        Validate.isZeroOrPositive(balance.getPrincipalWrittenOff(), "Principal written-off becomes negative [%s], loan: [%s]", balance.getPrincipalWrittenOff(), loanId);
        Validate.isZeroOrPositive(balance.getInterestWrittenOff(), "Interest written-off becomes negative [%s], loan: [%s]", balance.getInterestWrittenOff(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyWrittenOff(), "Penalty written-off becomes negative [%s], loan: [%s]", balance.getPenaltyWrittenOff(), loanId);
        Validate.isZeroOrPositive(balance.getFeeWrittenOff(), "Fee written-off becomes negative [%s], loan: [%s]", balance.getFeeWrittenOff(), loanId);

        Validate.isZeroOrPositive(balance.getPrincipalDue(), "Principal due becomes negative [%s], loan: [%s]", balance.getPrincipalDue(), loanId);
        Validate.isZeroOrPositive(balance.getInterestDue(), "Interest due becomes negative [%s], loan: [%s]", balance.getInterestDue(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyDue(), "Penalty due becomes negative [%s], loan: [%s]", balance.getPenaltyDue(), loanId);
        Validate.isZeroOrPositive(balance.getFeeDue(), "Fee due becomes negative [%s], loan: [%s]", balance.getFeeDue(), loanId);

        Validate.isZeroOrPositive(balance.getPrincipalOutstanding(), "Principal outstanding becomes negative [%s], loan: [%s]", balance.getPrincipalOutstanding(), loanId);
        Validate.isZeroOrPositive(balance.getInterestOutstanding(), "Interest outstanding becomes negative [%s], loan: [%s]", balance.getInterestOutstanding(), loanId);
        Validate.isZeroOrPositive(balance.getPenaltyOutstanding(), "Penalty outstanding becomes negative [%s], loan: [%s]", balance.getPenaltyOutstanding(), loanId);
        Validate.isZeroOrPositive(balance.getFeeOutstanding(), "Fee outstanding becomes negative [%s], loan: [%s]", balance.getFeeOutstanding(), loanId);

        Validate.isLoe(balance.getPrincipalDue(), balance.getPrincipalOutstanding(), "Principal due [%s] becomes more than outstanding [%s], loan: [%s]", balance.getPrincipalDue(), balance.getPrincipalOutstanding(), loanId);
        Validate.isLoe(balance.getInterestDue(), balance.getInterestOutstanding(), "Interest due [%s] becomes more than outstanding [%s], loan: [%s]", balance.getInterestDue(), balance.getInterestOutstanding(), loanId);
        Validate.isLoe(balance.getPenaltyDue(), balance.getPenaltyOutstanding(), "Penalty due [%s] becomes more than outstanding [%s], loan: [%s]", balance.getPenaltyDue(), balance.getPenaltyOutstanding(), loanId);
        Validate.isLoe(balance.getFeeDue(), balance.getFeeOutstanding(), "Fee due [%s] becomes more than outstanding [%s], loan: [%s]", balance.getFeeDue(), balance.getFeeOutstanding(), loanId);

        Validate.isLoe(balance.getPrincipalInvoiced(), balance.getPrincipalDisbursed(), "Principal invoiced [%s] becomes more than disbursed [%s], loan: [%s]", balance.getPrincipalInvoiced(), balance.getPrincipalDisbursed(), loanId);
        Validate.isLoe(balance.getInterestInvoiced(), balance.getInterestApplied(), "Interest invoiced [%s] becomes more than applied [%s], loan: [%s]", balance.getInterestInvoiced(), balance.getInterestApplied(), loanId);
        Validate.isLoe(balance.getPenaltyInvoiced(), balance.getPenaltyApplied(), "Penalty invoiced [%s] becomes more than applied [%s], loan: [%s]", balance.getPenaltyInvoiced(), balance.getPenaltyApplied(), loanId);
        Validate.isLoe(balance.getFeeInvoiced(), balance.getFeeApplied(), "Fee invoiced [%s] becomes more than applied [%s], loan: [%s]", balance.getFeeInvoiced(), balance.getFeeApplied(), loanId);

        Validate.isLoe(balance.getPrincipalPaid(), balance.getPrincipalInvoiced(), "Principal paid [%s] becomes more than invoiced [%s], loan: [%s]", balance.getPrincipalPaid(), balance.getPrincipalInvoiced(), loanId);
        Validate.isLoe(balance.getInterestPaid(), balance.getInterestInvoiced(), "Interest paid [%s] becomes more than invoiced [%s], loan: [%s]", balance.getInterestPaid(), balance.getInterestInvoiced(), loanId);
        Validate.isLoe(balance.getPenaltyPaid(), balance.getPenaltyInvoiced(), "Penalty paid [%s] becomes more than invoiced [%s], loan: [%s]", balance.getPenaltyPaid(), balance.getPenaltyInvoiced(), loanId);
        Validate.isLoe(balance.getFeePaid(), balance.getFeeInvoiced(), "Fee paid [%s] becomes more than invoiced [%s], loan: [%s]", balance.getFeePaid(), balance.getFeeInvoiced(), loanId);
    }

    private void resolveLoanDerivedValues(Long loanId, String state,String status, LocalDate when) {
        LoanEntity loan = loanRepository.getRequired(loanId);

        resolve(loanId, when, loan);
        loanRepository.saveAndFlush(loan);
        eventPublisher.publishEvent(new LoanDerivedValuesUpdated(loan.toValueObject()));
    }

    private void resolve(Long loanId, LocalDate when, LoanEntity loan) {
        Balance totalBalance = transactionService.getBalance(BalanceQuery.byLoan(loanId));
        validateLoanBalance(loanId, totalBalance);
        updateBalances(loan, totalBalance);

        LocalDate lastTransactionDate = lastTransactionDate(loanId);
        LocalDate voidedDate = findTransactionMaxValueDate(loanId, TransactionType.VOID_LOAN);
        LocalDate renounceDate = findTransactionMaxValueDate(loanId, TransactionType.RENOUNCE_LOAN);
        LocalDate brokenDate = findTransactionMaxValueDate(loanId, TransactionType.BREAK_LOAN);
        LocalDate rescheduledDate = findTransactionMaxValueDate(loanId, TransactionType.RESCHEDULE_LOAN);
        LocalDate rescheduleBrokenDate = findTransactionMaxValueDate(loanId, TransactionType.BREAK_LOAN_RESCHEDULE);
        LocalDate movedToLegalDate = findTransactionMaxValueDate(loanId, TransactionType.MOVE_LOAN_TO_LEGAL);
        LocalDate soldDate = findTransactionMaxValueDate(loanId, TransactionType.SOLD_LOAN);
        LocalDate repurchaseDate = findVoidedTransactionMaxValueDate(loanId, TransactionType.VOID_SOLD_LOAN);
        LocalDate firstDisbursementDate = findTransactionMinValueDate(loanId, TransactionType.DISBURSEMENT);
        Contract contract = scheduleService.getCurrentContract(loanId);


        List<InstallmentEntity> installments = installmentRepository.findAll(
            Entities.installment.loanId.eq(loanId).and(Entities.installment.statusDetail.ne(InstallmentStatusDetail.CANCELLED)),
            Entities.installment.dueDate.asc(), Entities.installment.id.asc());
        LocalDate maturityDate = contract.getMaturityDate();
        LocalDate dueDate = installments.stream()
            .filter(installment -> installment.getStatus() == InstallmentStatus.OPEN)
            .map(InstallmentEntity::getDueDate)
            .min(LocalDate::compareTo).orElse(maturityDate);
        List<InstallmentEntity> closedInstallments = installments.stream()
            .filter(installment -> installment.getStatus() == InstallmentStatus.CLOSED)
            .collect(Collectors.toList());

        final Long dpd;
        if (isPositive(totalBalance.getTotalOutstanding())) {
            long daysTillMaturity = ChronoUnit.DAYS.between(maturityDate, when);
            dpd = installments.stream()
                .filter(installment -> installment.getStatus() == InstallmentStatus.OPEN)
                .map(InstallmentEntity::getDpd)
                .max(Long::compare)
                .orElse(daysTillMaturity);
        } else if (!closedInstallments.isEmpty()) {
            InstallmentEntity lastClosedInstallment = Collections.max(closedInstallments, Comparator.comparing(InstallmentEntity::getDueDate));
            dpd = lastClosedInstallment.getDpd();
        } else {
            LocalDate closeDate = firstNonNull(voidedDate, renounceDate, brokenDate, rescheduledDate, rescheduleBrokenDate, movedToLegalDate, lastTransactionDate);
            dpd = ChronoUnit.DAYS.between(maturityDate, closeDate);
        }

        Long maxDpd = installments.stream()
            .map(InstallmentEntity::getDpd)
            .max(Long::compare)
            .orElse(dpd);


        loan.setMaturityDate(maturityDate);
        loan.setPaymentDueDate(dueDate);
        loan.setBrokenDate(brokenDate);
        loan.setRescheduledDate(rescheduledDate);
        loan.setRescheduleBrokenDate(rescheduleBrokenDate);
        loan.setMovedToLegalDate(movedToLegalDate);
        loan.setOverdueDays(dpd.intValue());
        loan.setMaxOverdueDays(maxDpd.intValue());
        loan.setExtensions(totalBalance.getExtensions());
        loan.setExtendedByDays(totalBalance.getExtensionDays());
        loan.setFirstDisbursementDate(firstDisbursementDate);

        if (repurchaseDate != null) {
            loan.open(LoanStatusDetail.REPURCHASED);
            return;
        }
        if (soldDate != null) {
            loan.close(LoanStatusDetail.SOLD, soldDate);
            return;
        }

        if (voidedDate != null) {
            loan.close(LoanStatusDetail.VOIDED, lastTransactionDate);
            return;
        }
        if (renounceDate != null) {
            if (BigDecimalUtils.isZero(loan.getTotalOutstanding())) {
                loan.close(LoanStatusDetail.RENOUNCED_PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.RENOUNCED);
            }
            return;
        }
        if (movedToLegalDate != null) {
            if (BigDecimalUtils.isZero(loan.getTotalOutstanding())) {
                loan.close(LoanStatusDetail.LEGAL_PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.LEGAL);
            }
            return;
        }
        if (rescheduleBrokenDate != null) {
            if (BigDecimalUtils.isZero(loan.getTotalOutstanding())) {
                loan.close(LoanStatusDetail.BROKEN_PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.BROKEN);
            }
            return;
        }
        if (rescheduledDate != null) {
            if (BigDecimalUtils.isZero(loan.getTotalOutstanding())) {
                loan.close(LoanStatusDetail.RESCHEDULED_PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.RESCHEDULED);
            }
            return;
        }
        if (brokenDate != null) {
            if (BigDecimalUtils.isZero(loan.getTotalOutstanding())) {
                loan.close(LoanStatusDetail.BROKEN_PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.BROKEN);
            }
            return;
        }
        if (BigDecimalUtils.isZero(loan.getPrincipalDisbursed())) {
            Optional<Disbursement> pendingDisbursement = disbursementService.getOptional(DisbursementService.DisbursementQuery.byLoan(loanId, DisbursementStatusDetail.PENDING));
            if (pendingDisbursement.isPresent()) {
                loan.open(LoanStatusDetail.DISBURSING);
            } else {
                loan.open(LoanStatusDetail.ISSUED);
            }
            return;
        }
        if (isPositive(loan.getTotalOutstanding())) {
            loan.open(LoanStatusDetail.ACTIVE);
        } else {
            if (contract.isCloseLoanOnPaid()) {
                loan.close(LoanStatusDetail.PAID, lastTransactionDate);
            } else {
                loan.open(LoanStatusDetail.PAID);
            }
        }
    }

    private LocalDate findTransactionMaxValueDate(Long loanId, TransactionType transactionType) {
        return queryFactory.select(transaction.valueDate.max())
            .from(transaction)
            .where(
                transaction.loanId.eq(loanId),
                transaction.voided.isFalse(),
                transaction.transactionType.eq(transactionType))
            .fetchOne();
    }

    private LocalDate findVoidedTransactionMaxValueDate(Long loanId, TransactionType transactionType) {
        return queryFactory.select(transaction.valueDate.max())
            .from(transaction)
            .where(
                transaction.loanId.eq(loanId),
                transaction.voided.isTrue(),
                transaction.transactionType.eq(transactionType))
            .fetchOne();
    }

    private LocalDate findTransactionMinValueDate(Long loanId, TransactionType transactionType) {
        return queryFactory.select(transaction.valueDate.min())
            .from(transaction)
            .where(
                transaction.loanId.eq(loanId),
                transaction.voided.isFalse(),
                transaction.transactionType.eq(transactionType))
            .fetchOne();
    }

    private LocalDate lastTransactionDate(Long loanId) {
        return queryFactory.select(transaction.valueDate.max())
            .from(transaction)
            .where(transaction.loanId.eq(loanId))
            .fetchOne();
    }

    private void updateBalances(LoanEntity loan, Balance totalBalance) {
        loan.setCashIn(totalBalance.getCashIn());
        loan.setCashOut(totalBalance.getCashOut());

        loan.setCreditLimit(totalBalance.getCreditLimit());
        loan.setCreditLimitAvailable(totalBalance.getCreditLimitAvailable());

        loan.setPrincipalDisbursed(totalBalance.getPrincipalDisbursed());
        loan.setPrincipalPaid(totalBalance.getPrincipalPaid());
        loan.setPrincipalWrittenOff(totalBalance.getPrincipalWrittenOff());
        loan.setPrincipalDue(totalBalance.getPrincipalDue());
        loan.setPrincipalOutstanding(totalBalance.getPrincipalOutstanding());

        loan.setInterestApplied(totalBalance.getInterestApplied());
        loan.setInterestPaid(totalBalance.getInterestPaid());
        loan.setInterestWrittenOff(totalBalance.getInterestWrittenOff());
        loan.setInterestDue(totalBalance.getInterestDue());
        loan.setInterestOutstanding(totalBalance.getInterestOutstanding());

        loan.setPenaltyApplied(totalBalance.getPenaltyApplied());
        loan.setPenaltyPaid(totalBalance.getPenaltyPaid());
        loan.setPenaltyWrittenOff(totalBalance.getPenaltyWrittenOff());
        loan.setPenaltyDue(totalBalance.getPenaltyDue());
        loan.setPenaltyOutstanding(totalBalance.getPenaltyOutstanding());

        loan.setFeeApplied(totalBalance.getFeeApplied());
        loan.setFeePaid(totalBalance.getFeePaid());
        loan.setFeeWrittenOff(totalBalance.getFeeWrittenOff());
        loan.setFeeDue(totalBalance.getFeeDue());
        loan.setFeeOutstanding(totalBalance.getFeeOutstanding());

        loan.setOverpaymentReceived(totalBalance.getOverpaymentReceived());
        loan.setOverpaymentUsed(totalBalance.getOverpaymentUsed());
        loan.setOverpaymentRefunded(totalBalance.getOverpaymentRefunded());
        loan.setOverpaymentAvailable(totalBalance.getOverpaymentAvailable());

        loan.setTotalDue(totalBalance.getTotalDue());
        loan.setTotalOutstanding(totalBalance.getTotalOutstanding());
    }

    private void resolveInstallmentDerivedValues(Long installmentId, LocalDate when) {
        Balance balance = transactionService.getBalance(TransactionQuery.byInstallment(installmentId));

        BigDecimal totalPaid = balance.getPrincipalPaid().add(balance.getInterestPaid()).add(balance.getPenaltyPaid()).add(balance.getFeePaid());
        BigDecimal totalInvoiced = balance.getPrincipalInvoiced().add(balance.getInterestInvoiced()).add(balance.getPenaltyInvoiced()).add(balance.getFeeInvoiced());

        BigDecimal totalDue = balance.getTotalDue();
        Validate.isZeroOrPositive(totalDue, "Total due becomes negative for installment id [%s]: [%s]", installmentId, totalDue);

        LocalDate cancelDate = cancelDate(installmentId);

        InstallmentEntity installment = installmentRepository.getRequired(installmentId);

        installment.setPrincipalPaid(balance.getPrincipalPaid());
        installment.setPrincipalWrittenOff(balance.getPrincipalWrittenOff());
        installment.setPrincipalInvoiced(balance.getPrincipalInvoiced());
        installment.setInterestApplied(balance.getInterestApplied());
        installment.setInterestPaid(balance.getInterestPaid());
        installment.setInterestWrittenOff(balance.getInterestWrittenOff());
        installment.setInterestInvoiced(balance.getInterestInvoiced());
        installment.setPenaltyApplied(balance.getPenaltyApplied());
        installment.setPenaltyPaid(balance.getPenaltyPaid());
        installment.setPenaltyWrittenOff(balance.getPenaltyWrittenOff());
        installment.setPenaltyInvoiced(balance.getPenaltyInvoiced());
        installment.setFeeApplied(balance.getFeeApplied());
        installment.setFeePaid(balance.getFeePaid());
        installment.setFeeWrittenOff(balance.getFeeWrittenOff());
        installment.setFeeInvoiced(balance.getFeeInvoiced());
        installment.setOverpaymentUsed(balance.getOverpaymentUsed());
        installment.setCashIn(balance.getCashIn());
        installment.setTotalInvoiced(totalInvoiced);
        installment.setTotalPaid(totalPaid);
        installment.setTotalDue(totalDue);

        Contract contract = scheduleService.getContract(installment.getContractId());
        if (cancelDate != null) {
            Validate.isZero(totalDue, "Installment [%s] is cancelled but has total due left [%s]", installmentId, totalDue);
            installment.setStatus(InstallmentStatus.CLOSED);
            installment.setStatusDetail(InstallmentStatusDetail.CANCELLED);
            installment.setCloseDate(cancelDate);
            Long nominalDpd = ChronoUnit.DAYS.between(installment.getDueDate(), cancelDate);
            installment.setDpd(dpd(contract, nominalDpd));
        } else if (BigDecimalUtils.isZero(totalDue)) {
            installment.setStatus(InstallmentStatus.CLOSED);
            installment.setStatusDetail(InstallmentStatusDetail.PAID);
            LocalDate closeDate = lastInstallmentTransactionDate(installmentId);
            installment.setCloseDate(closeDate);
            long nominalDpd = ChronoUnit.DAYS.between(installment.getDueDate(), closeDate);
            installment.setDpd(dpd(contract, nominalDpd));
        } else {
            installment.setStatus(InstallmentStatus.OPEN);
            installment.setStatusDetail(InstallmentStatusDetail.PENDING);
            installment.setCloseDate(null);
            long nominalDpd = ChronoUnit.DAYS.between(installment.getDueDate(), when);
            installment.setDpd(dpd(contract, nominalDpd));
        }
    }

    private Long dpd(Contract contract, Long nominalDpd) {
        return contract.getBaseOverdueDays() > 0 ?
            Math.max(nominalDpd, 0) + contract.getBaseOverdueDays() : nominalDpd;
    }

    private LocalDate cancelDate(Long installmentId) {
        return queryFactory.select(transaction.valueDate.max())
            .from(transaction)
            .where(
                transaction.installmentId.eq(installmentId),
                transaction.voided.isFalse(),
                transaction.transactionType.eq(TransactionType.CANCEL_INSTALLMENT))
            .fetchOne();
    }

    private LocalDate lastInstallmentTransactionDate(Long installmentId) {
        return queryFactory.select(transaction.valueDate.max())
            .from(transaction)
            .where(transaction.installmentId.eq(installmentId))
            .fetchOne();
    }


}
