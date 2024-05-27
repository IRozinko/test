package fintech.admintools.impl;

import com.google.common.base.Throwables;
import fintech.Validate;
import fintech.admintools.AdminAction;
import fintech.admintools.AdminActionContext;
import fintech.admintools.AdminActionStatus;
import fintech.admintools.db.AdminActionLogEntity;
import fintech.admintools.db.AdminActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Transactional(propagation = Propagation.NEVER)
public class ActionExecutor {

    @Autowired
    private AdminActionLogRepository repository;

    @Autowired(required = false)
    private List<AdminAction> actions;

    @Autowired
    private AdminActionsHelper actionsHelper;

    @Async
    public void execute(Long id) {
        AdminActionLogEntity entity = repository.getRequired(id);
        Validate.isTrue(entity.getStatus() == AdminActionStatus.RUNNING);
        try {
            long count = actions.stream().filter(a -> a.getName().equals(entity.getName())).count();
            Validate.isTrue(count == 1, "Did not find exactly one action by name [%s]", entity.getName());
            AdminAction action = actions.stream().filter(a -> a.getName().equals(entity.getName())).findFirst().get();

            AdminActionContext context = new AdminActionContextImpl(id, actionsHelper, entity.getParams());
            action.execute(context);

            actionsHelper.completed(id);
        } catch (Exception e) {
            actionsHelper.failed(id, Throwables.getRootCause(e).getMessage());
            throw Throwables.propagate(e);
        }
    }
}
