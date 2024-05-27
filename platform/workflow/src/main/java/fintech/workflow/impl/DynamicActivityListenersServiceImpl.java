package fintech.workflow.impl;

import com.google.common.collect.Lists;
import fintech.Validate;
import fintech.workflow.Activity;
import fintech.workflow.DynamicActivityListenersService;
import fintech.workflow.TriggerRegistry;
import fintech.workflow.UpdateDynamicActivityListenerCommand;
import fintech.workflow.Workflow;
import fintech.workflow.db.ActivityListenerEntity;
import fintech.workflow.db.ActivityListenerRepository;
import fintech.workflow.spi.TriggerHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static fintech.workflow.ActivityListenerStatus.COMPLETED;
import static fintech.workflow.ActivityListenerStatus.STARTED;

@RequiredArgsConstructor
@Service
@Transactional
public class DynamicActivityListenersServiceImpl implements DynamicActivityListenersService {

    private final ActivityListenerRepository repository;
    private final TriggerRegistry triggerRegistry;
    private final ApplicationContext applicationContext;

    @Override
    public void addOrEditListener(UpdateDynamicActivityListenerCommand cmd) {
        Validate.isTrue(cmd.getListenerStatus() == COMPLETED || cmd.getListenerStatus() == STARTED, "Unknown ActivityListenerStatus [%s]", cmd.getListenerStatus());
        Validate.isTrue(cmd.getDelay() == null || cmd.getListenerStatus() == STARTED, "Delay is available only for STARTED activities");
        Validate.isTrue((cmd.getDelay() == null && cmd.getFromMidnight() == null) || (cmd.getDelay() != null && cmd.getFromMidnight() != null), "Set 'fromMidnight' option");
        Validate.isTrue(cmd.getListenerStatus() == COMPLETED || cmd.getResolution() == null, "STARTED types should not have resolution");
        Validate.isTrue(cmd.getListenerStatus() == STARTED || cmd.getDelay() == null, "COMPLETED types should not have delay");

        Optional<ActivityListenerEntity> entityMaybe;
        if (cmd.getId() != null) {
            entityMaybe = Optional.of(repository.getRequired(cmd.getId()));
        } else {
            List<ActivityListenerEntity> entities = cmd.getListenerStatus() == COMPLETED ?
                repository.findExistedOnActivityCompleted(cmd.getWorkflowName(), cmd.getVersion(), cmd.getActivityName(), cmd.getResolution()) :
                repository.findExistedOnActivityStarted(cmd.getWorkflowName(), cmd.getVersion(), cmd.getActivityName());

            entityMaybe = entities.stream()
                .filter(e -> e.getTriggerName().equals(cmd.getTriggerName()) && Arrays.equals(cmd.getArgs(), e.getParams()))
                .findFirst();
        }

        ActivityListenerEntity entity = entityMaybe.orElseGet(ActivityListenerEntity::new);

        entity.setActivityName(cmd.getActivityName());
        entity.setDelaySec(cmd.getDelay() == null ? null : (int) cmd.getDelay().getSeconds());
        entity.setName(cmd.getActivityName());
        entity.setParams(cmd.getArgs());
        entity.setActivityStatus(cmd.getListenerStatus());
        entity.setName(cmd.getName());
        entity.setWorkflowVersion(cmd.getVersion());
        entity.setTriggerName(cmd.getTriggerName());
        entity.setWorkflowName(cmd.getWorkflowName());
        entity.setResolution(cmd.getResolution());
        entity.setFromMidnight(cmd.getFromMidnight());

        repository.saveAndFlush(entity);
    }

    @Override
    public void removeDynamicListener(Long id) {
        repository.delete(id);
    }

    @Override
    public void runOnStartedListenerIfPresent(Workflow workflow, Activity activity) {
        repository.findExistedOnActivityStarted(workflow.getName(), workflow.getVersion(), activity.getName()).forEach(e -> {
            runTrigger(workflow, activity, e.getTriggerName(), e.getDelaySec(), e.getFromMidnight(), e.getParams());
        });
    }

    @Override
    public void runOnCompletedListenerIfPresent(Workflow workflow, Activity activity, String resolution) {
        repository.findExistedOnActivityCompleted(workflow.getName(), workflow.getVersion(), activity.getName(), resolution).forEach(e -> {
            runTrigger(workflow, activity, e.getTriggerName(), e.getDelaySec(), e.getFromMidnight(), e.getParams());
        });
    }

    private void runTrigger(Workflow workflow, Activity activity, String trigger, Integer delaySec, Boolean fromMidnight, String[] params) {
        Class<? extends TriggerHandler> type = triggerRegistry.getTriggerHandler(trigger);
        List<Object> paramsNew = Lists.newArrayList();
        paramsNew.add(delaySec == null ? Duration.ZERO : Duration.ofSeconds(delaySec));
        paramsNew.add(fromMidnight == null ? false : fromMidnight);
        paramsNew.addAll(Lists.newArrayList(params));
        TriggerHandler handler = applicationContext.getBean(type, paramsNew.toArray());
        handler.handle(new TriggerContextImpl(workflow, activity));
    }
}
