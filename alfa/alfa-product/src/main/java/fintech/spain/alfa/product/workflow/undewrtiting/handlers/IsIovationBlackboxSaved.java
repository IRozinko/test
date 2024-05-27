package fintech.spain.alfa.product.workflow.undewrtiting.handlers;


import fintech.iovation.IovationService;
import fintech.iovation.model.IovationBlackboxQuery;
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
public class IsIovationBlackboxSaved implements AutoCompletePrecondition {

    @Autowired
    private IovationService iovationService;

    @Override
    public boolean isTrueFor(ActivityContext context) {
        Long clientId = context.getWorkflow().getClientId();
        Long applicationId = context.getWorkflow().getApplicationId();

        return iovationService.findLatestBlackBox(new IovationBlackboxQuery().setClientId(clientId).setApplicationId(applicationId)).isPresent();
    }
}
