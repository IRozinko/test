package fintech.spain.alfa.product.dc.spi;

import fintech.dc.spi.ConditionContext;
import fintech.dc.spi.ConditionHandler;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.InstallmentStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class InstallmentStatusDetailCondition implements ConditionHandler {

    private final ScheduleService scheduleService;

    @Autowired
    public InstallmentStatusDetailCondition(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Override
    public boolean apply(ConditionContext context) {
        Integer sequenceFrom = context.getParam("installmentSequenceFrom", Integer.class).orElse(Integer.MIN_VALUE);
        Integer sequenceTo = context.getParam("installmentSequenceTo", Integer.class).orElse(Integer.MAX_VALUE);
        String statusDetail = context.getRequiredParam("installmentStatusDetail", String.class);

        Contract contract = scheduleService.getCurrentContract(context.getDebt().getLoanId());
        List<Installment> installments = scheduleService.findInstallments(new InstallmentQuery().setContractId(contract.getId()));
        if (installments.isEmpty()) {
            return false;
        }

        List<Installment> overdueInstallments = installments.stream()
            .filter(installment -> installment.getInstallmentSequence() >= sequenceFrom && installment.getInstallmentSequence() <= sequenceTo)
            .filter(installment -> installment.getStatusDetail() == InstallmentStatusDetail.valueOf(statusDetail))
            .collect(Collectors.toList());
        return !overdueInstallments.isEmpty();
    }
}
