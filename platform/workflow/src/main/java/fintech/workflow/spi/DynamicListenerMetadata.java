package fintech.workflow.spi;

import fintech.workflow.ActivityListenerStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Duration;

@Getter
@AllArgsConstructor
public class DynamicListenerMetadata {

    private final ActivityListenerStatus listenerStatus;
    private final String triggerName;
    private final String resolution;
    private final String[] args;
    private final Duration delay;
    private final Boolean fromMidnight;

}
