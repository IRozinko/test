package fintech.spain.alfa.product.workflow.common;

import fintech.spain.alfa.product.web.model.PopupType;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class ExhaustPopupActivity implements ActivityListener {

    @Autowired
    private PopupService service;

    private final PopupType type;

    public ExhaustPopupActivity(PopupType type) {
        this.type = type;
    }

    @Override
    public void handle(ActivityContext context) {
        service.markAsExhausted(context.getClientId(), type);
    }
}
