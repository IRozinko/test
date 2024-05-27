package fintech.activity.impl;

import fintech.PredicateBuilder;
import fintech.activity.ActivityService;
import fintech.activity.commands.AddActivityCommand;
import fintech.activity.db.ActivityLogEntity;
import fintech.activity.db.ActivityLogRepository;
import fintech.activity.model.Activity;
import fintech.activity.model.FindActivitiesQuery;
import fintech.activity.spi.ActivityRegistry;
import fintech.activity.spi.BulkActionContext;
import fintech.activity.spi.BulkActionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static fintech.activity.db.Entities.activity;

@Slf4j
@Transactional
@Component
public class ActivityServiceBean implements ActivityService {

    @Autowired
    private ActivityLogRepository repository;

    @Autowired
    private ActivityRegistry activityRegistry;

    @Override
    public Long addActivity(AddActivityCommand command) {
        ActivityLogEntity entity = new ActivityLogEntity();
        entity.setClientId(command.getClientId());
        entity.setAgent(command.getAgent());
        entity.setAction(command.getAction());
        entity.setResolution(command.getResolution());
        entity.setSource(command.getSource());
        entity.setTopic(command.getTopic());
        entity.setComments(command.getComments());
        entity.setDetails(command.getDetails());
        entity.setApplicationId(command.getApplicationId());
        entity.setLoanId(command.getLoanId());
        entity.setTaskId(command.getTaskId());
        entity.setDebtId(command.getDebtId());
        entity.setDebtActionId(command.getDebtActionId());
        entity.setPaymentId(command.getPaymentId());
        entity = repository.saveAndFlush(entity);

        Activity valueObject = entity.toValueObject();
        for (AddActivityCommand.BulkAction bulkAction : command.getBulkActions()) {
            BulkActionHandler handler = activityRegistry.getBulkActionHandler(bulkAction.getType());
            BulkActionContext context = new BulkActionContextImpl(bulkAction.getType(), valueObject, bulkAction.getParams());
            handler.handle(context);
        }
        return entity.getId();
    }

    @Override
    public List<Activity> findActivities(FindActivitiesQuery query) {
        return repository.findAll(toPredicate(query).allOf())
            .stream()
            .map(ActivityLogEntity::toValueObject)
            .collect(Collectors.toList());
    }

    private PredicateBuilder toPredicate(FindActivitiesQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), activity.clientId::eq)
            .addIfPresent(query.getAction(), activity.action::eq)
            .addIfPresent(query.getTopic(), activity.topic::eq);
    }
}
