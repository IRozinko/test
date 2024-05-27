package fintech.spain.alfa.product.workflow.common;

import fintech.lending.core.application.LoanApplicationService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class RejectApplication implements ActivityListener {

    @Autowired
    private LoanApplicationService applicationService;

    @Override
    public void handle(ActivityContext context) {
        Long applicationId = context.getWorkflow().getApplicationId();
        String reason = context.getActivity().getResolutionDetail();
        applicationService.reject(applicationId, reason);
    }
}
