package fintech.spain.alfa.product.lending.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import fintech.dc.DcSettingsService;
import fintech.dc.model.DcSettings;
import fintech.lending.core.loan.*;
import fintech.lending.core.loan.db.LoanRepository;
import fintech.lending.core.loan.events.LoanPaymentEvent;
import fintech.spain.alfa.product.lending.LoanRescheduling;
import fintech.spain.alfa.product.lending.LoanReschedulingService;
import fintech.spain.alfa.product.lending.LoanReschedulingStatus;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.spain.alfa.product.db.LoanReschedulingEntity;
import fintech.spain.alfa.product.db.LoanReschedulingRepository;
import fintech.spain.alfa.product.lending.LoanReschedulingQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fintech.spain.alfa.product.db.Entities.reschedulingLoan;

@Service
@Transactional
@Slf4j
public class LoanReschedulingServiceBean implements LoanReschedulingService {

    @Autowired
    private LoanReschedulingRepository loanReschedulingRepository;

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private LoanService loanService;

    @Autowired
    private DcSettingsService dcSettingsService;

    @Autowired
    private ScheduleService scheduleService;

    @Override
    public void createLoanRescheduling(RescheduleCommand command) {
        ReschedulingPreview preview = command.getPreview();
        DcSettings.ReschedulingSettings reschedulingSettings = dcSettingsService.getSettings().getReschedulingSettings();
        loanReschedulingRepository.findFirst(reschedulingLoan.loan.id.eq(command.getLoanId())
                .and(reschedulingLoan.status.eq(LoanReschedulingStatus.RESCHEDULED))
            , reschedulingLoan.createdAt.asc())
            .ifPresent(pc -> {
                throw new IllegalArgumentException("Loan rescheduling is in progress");
            });
        LocalDate rescheduleDate = command.getWhen();
        LoanReschedulingEntity loanReschedulingEntity = new LoanReschedulingEntity()
            .setLoan(loanRepository.getRequired(command.getLoanId()))
            .setStatus(LoanReschedulingStatus.RESCHEDULED)
            .setRepaymentDueDays(reschedulingSettings.getRepaymentDueDays())
            .setGracePeriodDays(reschedulingSettings.getGracePeriodDays())
            .setInstallmentAmount(preview.getItems().get(0).getTotalScheduled())
            .setRescheduleDate(rescheduleDate)
            .setExpireDate(rescheduleDate.plusDays(reschedulingSettings.getRepaymentDueDays()))
            .setNumberOfPayments(preview.getItems().size());
        loanReschedulingRepository.save(loanReschedulingEntity);
    }

    @Override
    public void cancel(Long loanId, LocalDate expireDate) {
        Optional<LoanRescheduling> loanRescheduling = findLoanRescheduling(LoanReschedulingQuery.rescheduled(loanId));
        loanRescheduling.ifPresent(r -> {
            LoanReschedulingEntity entity = loanReschedulingRepository.getRequired(r.getId());
            entity.setStatus(LoanReschedulingStatus.CANCELLED);
            entity.setExpireDate(expireDate);
        });

    }

    @Override
    public void updateExpireDate(Long reschedulingLoanId, LocalDate date) {
        LoanReschedulingEntity loanReschedulingEntity = get(reschedulingLoanId);
        loanReschedulingEntity.setStatus(LoanReschedulingStatus.RESCHEDULED);
        loanReschedulingEntity.setExpireDate(date);
    }

    @Override
    public Optional<LoanRescheduling> findLoanRescheduling(LoanReschedulingQuery query) {
        Optional<LoanReschedulingEntity> entity = loanReschedulingRepository.findFirst(
            ExpressionUtils.allOf(toPredicates(query)), reschedulingLoan.id.desc()
        );
        return entity.map(LoanReschedulingEntity::toValueObject);
    }

    @Override
    public List<LoanReschedulingEntity> findRescheduledLoans(LoanReschedulingStatus status) {
        return loanReschedulingRepository.findAll(reschedulingLoan.status.eq(status));
    }

    @EventListener
    public void handleRescheduledLoan(LoanPaymentEvent event) {
        Loan loan = loanService.getLoan(event.getLoanId());
        if (loan.getStatusDetail().equals(LoanStatusDetail.RESCHEDULED) || loan.getStatusDetail().equals(LoanStatusDetail.RESCHEDULED_PAID)) {
            Optional<LoanRescheduling> loanRescheduling = findLoanRescheduling(LoanReschedulingQuery.rescheduled(event.getLoanId()));
            loanRescheduling.ifPresent(r -> {
                Optional<Installment> activeInstallment = findFirstActiveInstallment(loan.getId());
                if (activeInstallment.isPresent()) {
                    Installment installment = activeInstallment.get();
                    updateExpireDate(r.getId(), installment.getDueDate().plusDays(r.getGracePeriodDays()));
                } else {
                    close(r.getId());
                }
            });
        }
    }

    @Override
    public void close(Long id) {
        LoanReschedulingEntity loanRescheduling = get(id);
        loanRescheduling.setStatus(LoanReschedulingStatus.RESCHEDULED_PAID);
    }

    @Override
    public void pending(Long reschedulingLoanId, LocalDate reschedulingDueDate) {
        LoanReschedulingEntity loanRescheduling = get(reschedulingLoanId);
        loanRescheduling.setStatus(LoanReschedulingStatus.PENDING_TO_BREAK);
        loanRescheduling.setExpireDate(reschedulingDueDate);
    }

    private LoanReschedulingEntity get(Long id) {
        return loanReschedulingRepository.getRequired(id);
    }

    private Optional<Installment> findFirstActiveInstallment(Long loanId) {
        Contract contract = scheduleService.getCurrentContract(loanId);
        List<Installment> installments = scheduleService.findInstallments(InstallmentQuery.openContractInstallments(contract.getId()));
        return installments.stream().findFirst();
    }

    private List<Predicate> toPredicates(LoanReschedulingQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getLoanId() != null) {
            predicates.add(reschedulingLoan.loan.id.eq(query.getLoanId()));
        }
        if (query.getStatus() != null) {
            predicates.add(reschedulingLoan.status.eq(query.getStatus()));
        }
        if (query.getStatuses() != null) {
            predicates.add(reschedulingLoan.status.in(query.getStatuses()));
        }
        return predicates;
    }

}
