package fintech.admintools.impl;

import fintech.admintools.AdminActionStatus;
import fintech.admintools.ExecuteAdminActionCommand;
import fintech.admintools.db.AdminActionLogEntity;
import fintech.admintools.db.AdminActionLogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class AdminActionsHelper {

    @Autowired
    private AdminActionLogRepository repository;

    public Long newAction(ExecuteAdminActionCommand command) {
        AdminActionLogEntity entity = new AdminActionLogEntity();
        entity.setName(command.getName());
        entity.setParams(command.getParams());
        entity.setStatus(AdminActionStatus.RUNNING);
        Long id = repository.saveAndFlush(entity).getId();
        log.info("Action [{}] started, name [{}], params: [{}]", id, command.getName(), command.getParams());
        return id;
    }

    public void completed(Long id) {
        log.info("Action [{}] completed", id);
        AdminActionLogEntity entity = repository.getRequired(id);
        entity.setStatus(AdminActionStatus.COMPLETED);
    }

    public void failed(Long id, String message) {
        log.info("Action [{}] failed with message [{}]", id, message);
        AdminActionLogEntity entity = repository.getRequired(id);
        entity.setStatus(AdminActionStatus.FAILED);
        entity.setError(message);
    }

    public void updateProgress(Long id, String message) {
        log.info("Action [{}] updated with message [{}]", id, message);
        AdminActionLogEntity entity = repository.getRequired(id);
        entity.setMessage(message);
    }

    public boolean isRunning(Long id) {
        AdminActionLogEntity entity = repository.getRequired(id);
        return entity.getStatus() == AdminActionStatus.RUNNING;
    }
}
