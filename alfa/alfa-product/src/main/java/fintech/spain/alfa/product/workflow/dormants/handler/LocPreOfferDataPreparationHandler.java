package fintech.spain.alfa.product.workflow.dormants.handler;

import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class LocPreOfferDataPreparationHandler implements ActivityHandler {

    @Override
    public ActivityResult handle(ActivityContext context) {
        return ActivityResult.resolution(Resolutions.OK, "");
    }
}
