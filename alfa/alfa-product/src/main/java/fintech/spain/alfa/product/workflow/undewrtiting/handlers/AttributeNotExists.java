package fintech.spain.alfa.product.workflow.undewrtiting.handlers;


import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.AutoCompletePrecondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class AttributeNotExists implements AutoCompletePrecondition {

    private final String attributeName;

    public AttributeNotExists(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public boolean isTrueFor(ActivityContext context) {
        return !context.getWorkflow().hasAttribute(attributeName);
    }
}
