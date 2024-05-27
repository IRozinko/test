package fintech.lending.core.repayments;

import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InstallmentsRepayment {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private InstallmentRepayment installmentRepayment;

    public List<Long> repay(RepayLoanCommand command, RunningAmount runningAmount) {
        List<Long> txIds = new ArrayList<>();
        for (Installment installment : findOpenInstallments(command.getLoanId())) {
            installmentRepayment.repay(command, installment, runningAmount).ifPresent(txIds::add);
        }
        return txIds;
    }

    private List<Installment> findOpenInstallments(Long loanId) {
        return scheduleService.findInstallments(InstallmentQuery.openInstallments(loanId));
    }
}
