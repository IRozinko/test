package fintech.spain.alfa.product.workflow.common;

import fintech.TimeMachine;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.command.BuildLinkCommand;
import fintech.spain.platform.web.spi.SpecialLinkService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class GenerateSpecialLinkActivity implements ActivityListener {

    private final SpecialLinkType type;

    @Autowired
    private SpecialLinkService service;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public GenerateSpecialLinkActivity(SpecialLinkType type) {
        this.type = type;
    }

    @Override
    public void handle(ActivityContext context) {
        service.buildLink(
            new BuildLinkCommand()
                .setClientId(context.getClientId())
                .setType(type)
                .setReusable(true) // ToDO: ???
                .setAutoLoginRequired(true) // ToDO: ???
                .setExpiresAt(TimeMachine.now().plusDays(1))
        );
    }
}
