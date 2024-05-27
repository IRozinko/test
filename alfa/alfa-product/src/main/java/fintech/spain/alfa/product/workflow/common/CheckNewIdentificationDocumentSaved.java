package fintech.spain.alfa.product.workflow.common;

import fintech.spain.alfa.product.db.Entities;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import fintech.task.model.Task;
import fintech.task.spi.TaskContext;
import fintech.task.spi.TaskListener;
import fintech.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CheckNewIdentificationDocumentSaved implements TaskListener {

    @Autowired
    protected WorkflowService workflowService;

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    @Override
    public void handle(TaskContext context) {
        Task task = context.getTask();

        long newIdentificationDocumentCount = identificationDocumentRepository.count(
            Entities.identificationDocument.clientId.eq(task.getClientId())
                .and(Entities.identificationDocument.createdAt.gt(task.getCreatedAt()))
        );

        if (newIdentificationDocumentCount == 0) {
            throw new IllegalStateException("Please review attachments and type in ID document details in ID Docs");
        }
    }
}
