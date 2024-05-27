package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationSourceType;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class IsAffiliateSource implements AutoCompletePrecondition {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        LoanApplication loanApplication = loanApplicationService.get(context.getWorkflow().getApplicationId());
        if (loanApplication != null) {
            return LoanApplicationSourceType.AFFILIATE == loanApplication.getSourceType();
        } else {
            log.warn("No application found for id {}", context.getWorkflow().getApplicationId());
            return false;
        }
    }
}
