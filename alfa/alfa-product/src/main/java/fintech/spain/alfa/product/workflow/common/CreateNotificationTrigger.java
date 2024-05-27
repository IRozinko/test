package fintech.spain.alfa.product.workflow.common;

import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.spain.alfa.product.workflow.Triggers;
import fintech.workflow.AddTriggerCommand;
import fintech.workflow.TriggerService;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CreateNotificationTrigger implements ActivityListener {

    private final String cmsKey;

    private final Duration duration;

    private final boolean fromMidnight;

    @Autowired
    private TriggerService triggerService;

    public CreateNotificationTrigger(String cmsKey, Duration duration) {
        this.cmsKey = cmsKey;
        this.duration = duration;
        this.fromMidnight = false;
    }

    public CreateNotificationTrigger(String cmsKey, Duration duration, boolean fromMidnight) {
        this.cmsKey = cmsKey;
        this.duration = duration;
        this.fromMidnight = fromMidnight;
    }

    @Override
    public void handle(ActivityContext context) {
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
