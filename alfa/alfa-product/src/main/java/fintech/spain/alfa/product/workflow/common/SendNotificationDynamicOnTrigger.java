package fintech.spain.alfa.product.workflow.common;

import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.spain.alfa.product.workflow.Triggers;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.AddTriggerCommand;
import fintech.workflow.TriggerService;
import fintech.workflow.spi.TriggerContext;
import fintech.workflow.spi.TriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SendNotificationDynamicOnTrigger implements TriggerHandler {

    private final String cmsKey;
    private final Duration duration;

    private final boolean fromMidnight;
    @Autowired
    private TriggerService triggerService;

    @Autowired
    private CmsContextFactory cmsContextFactory;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;


    public SendNotificationDynamicOnTrigger(Duration duration, boolean fromMidnight, String cmsKey) {
        this.cmsKey = cmsKey;
        this.duration = duration;
        this.fromMidnight = fromMidnight;
    }

    @Override
    public void handle(TriggerContext context) {
        if (duration.getSeconds() == 0) {
            notificationFactory.fromCustomerService(context.getWorkflow().getClientId())
                .loanId(context.getWorkflow().getLoanId())
                .loanApplicationId(context.getWorkflow().getApplicationId())
                .render(cmsKey, cmsContextFactory.getContext(context.getWorkflow()))
                .send();
        } else {
            LocalDateTime dateTime;
            if (fromMidnight) {
                dateTime = TimeMachine.today().atStartOfDay().plus(duration);
            } else {
                dateTime = TimeMachine.now().plus(duration);
            }
            triggerService.addTrigger(
                new AddTriggerCommand()
                    .setActivityId(context.getActivity().getId())
                    .setName(Triggers.SEND_NOTIFICATION_TRIGGER)
                    .setParams(JsonUtils.writeValueAsString(ImmutableMap.of("cmsKey", cmsKey)))
                    .setNextAttemptAt(dateTime)
            );
        }
    }
}
