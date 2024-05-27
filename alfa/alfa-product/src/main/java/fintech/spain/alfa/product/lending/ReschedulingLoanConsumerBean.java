package fintech.spain.alfa.product.lending;

import fintech.DateUtils;
import fintech.dc.DcService;
import fintech.dc.DcSettingsService;
import fintech.dc.model.DcSettings;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.spain.alfa.product.db.LoanReschedulingEntity;
import fintech.spain.alfa.product.dc.DcFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static fintech.TimeMachine.today;

@Slf4j
@Component
public
class ReschedulingLoanConsumerBean {

    @Autowired
    private DcFacade dcFacade;

    @Autowired
    private LoanReschedulingService loanReschedulingService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private DcService dcService;

    @Autowired
    private AlfaNotificationBuilderFactory alfaNotificationBuilderFactory;

    @Autowired
    private DcSettingsService dcSettingsService;

    public void consume() {
        List<LoanReschedulingEntity> reschedulingLoans = loanReschedulingService.findRescheduledLoans(LoanReschedulingStatus.RESCHEDULED);
        log.info("Found {} rescheduled loans", reschedulingLoans.size());
        DcSettings.ReschedulingSettings reschedulingSettings = dcSettingsService.getSettings().getReschedulingSettings();
        reschedulingLoans.forEach(loanReschedulingEntity -> {
            Loan loan = loanReschedulingEntity.getLoan().toValueObject();
            findFirstActiveInstallment(loan).ifPresent(installment -> rescheduleNotifications(loanReschedulingEntity, installment));
            if (DateUtils.loe(loanReschedulingEntity.getExpireDate(), today()) &&
                loan.getStatusDetail() == LoanStatusDetail.RESCHEDULED) {
                LocalDate reschedulingDueDate = loanReschedulingEntity.getExpireDate().plusDays(reschedulingSettings.getHoldToBreakDays());
                loanReschedulingService.pending(loanReschedulingEntity.getId(), reschedulingDueDate);

            }
        });

        List<LoanReschedulingEntity> rescheduledLoansToBreak = loanReschedulingService.findRescheduledLoans(LoanReschedulingStatus.PENDING_TO_BREAK);
        log.info("Found {} rescheduled loans to break ", rescheduledLoansToBreak.size());
        rescheduledLoansToBreak.forEach(loanReschedulingEntity -> {
            if (DateUtils.lt(loanReschedulingEntity.getExpireDate(), today())) {
                cancelRescheduling(loanReschedulingEntity.getLoan().getId());
            }
        });
    }

    private void rescheduleNotifications(LoanReschedulingEntity loanReschedulingEntity, Installment installment) {
        Long loanId = loanReschedulingEntity.getLoan().getId();
        LocalDate dueDate = installment.getDueDate();
        if (dueDate.equals(today().plusDays(2))) {
            sendNotification(loanId, CmsSetup.RESCHEDULING_REMINDER_48_HOURS);
        } else if (dueDate.equals(today().minusDays(2))) {
            sendNotification(loanId, CmsSetup.RESCHEDULING_EXPIRED_2_DAYS);
        } else if (dueDate.equals(today().minusDays(3))) {
            sendNotification(loanId, CmsSetup.RESCHEDULING_EXPIRED_3_DAYS);
        } else if (dueDate.equals(today().minusDays(4))) {
            sendNotification(loanId, CmsSetup.RESCHEDULING_EXPIRED_4_DAYS);
        }
    }

    private Optional<Installment> findFirstActiveInstallment(Loan loan) {
        Contract contract = scheduleService.getCurrentContract(loan.getId());
        List<Installment> installments = scheduleService.findInstallments(InstallmentQuery.openContractInstallments(contract.getId()));
        return installments.stream().findFirst();
    }

    private void cancelRescheduling(Long loanId) {
        dcFacade.breakRescheduling(new BreakReschedulingCommand()
            .setLoanId(loanId)
            .setWhen(today()));
    }

    private void sendNotification(Long loanId, String template) {
        Map<String, Object> cmsContext = dcService.findByLoanId(loanId)
            .map(debt -> cmsModels.debtContext(debt.getId()))
            .orElseGet(() -> cmsModels.loanContext(loanId));
        alfaNotificationBuilderFactory.fromLoan(loanId)
            .loanId(loanId)
            .render(template, cmsContext)
            .send();
    }
}
