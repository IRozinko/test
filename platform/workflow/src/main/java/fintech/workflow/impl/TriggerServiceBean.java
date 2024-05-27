package fintech.workflow.impl;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import fintech.JsonUtils;
import fintech.workflow.AddTriggerCommand;
import fintech.workflow.TriggerRegistry;
import fintech.workflow.TriggerService;
import fintech.workflow.db.ActivityEntity;
import fintech.workflow.db.ActivityRepository;
import fintech.workflow.db.TriggerEntity;
import fintech.workflow.db.TriggerRepository;
import fintech.workflow.db.WorkflowEntity;
import fintech.workflow.db.WorkflowRepository;
import fintech.workflow.spi.TriggerHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Component
@Transactional
class TriggerServiceBean implements TriggerService {

    @Autowired
    private WorkflowRepository workflowRepository;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    private TriggerRepository triggerRepository;

    @Autowired
    private TriggerRegistry triggerRegistry;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Long addTrigger(AddTriggerCommand command) {
        ActivityEntity activity = activityRepository.getRequired(command.getActivityId());

        TriggerEntity trigger = new TriggerEntity();
        trigger.setWorkflowId(activity.getWorkflow().getId());
        trigger.setActivityId(activity.getId());
        trigger.setName(command.getName());
        trigger.setParams(command.getParams());
        trigger.setNextAttemptAt(command.getNextAttemptAt());
        return triggerRepository.save(trigger).getId();
    }

    @Override
    public void executeTrigger(Long triggerId) {
        TriggerEntity trigger = triggerRepository.getRequired(triggerId);
        WorkflowEntity workflow = workflowRepository.getRequired(trigger.getWorkflowId());
        ActivityEntity activity = activityRepository.getRequired(trigger.getActivityId());

        Preconditions.checkState(trigger.isWaiting(), "Can not execute trigger [%s], trigger not in status WAITING", trigger);
        Preconditions.checkState(workflow.isActive(), "Can not execute trigger [%s], workflow [%s] not in status ACTIVE", trigger, workflow);
        Preconditions.checkState(activity.isActive(), "Can not execute trigger [%s], activity [%s] not in status ACTIVE", trigger, workflow);

        Class<? extends TriggerHandler> type = triggerRegistry.getTriggerHandler(trigger.getName());
        Map<String, ?> params = Optional.ofNullable(trigger.getParams()).map(JsonUtils::readValueAsMap).orElse(ImmutableMap.of());

        TriggerHandler handler = applicationContext.getBean(type, params.values().toArray());
        handler.handle(new TriggerContextImpl(workflow.toValueObject(), activity.toValueObject()));

        trigger.completed();
    }

    @Override
    public void failTrigger(Long triggerId, String error) {
        TriggerEntity trigger = triggerRepository.getRequired(triggerId);

        Preconditions.checkState(trigger.isWaiting(), "Can not execute trigger [%s], trigger not in status WAITING", trigger);

        trigger.failed(error);
    }
}
