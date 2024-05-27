package fintech.bo.components.api;

import com.vaadin.ui.UI;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.task.CompleteTaskRequest;
import fintech.bo.api.model.task.TakeNextTaskResponse;
import fintech.bo.api.model.task.TaskCountResponse;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.attachments.AttachmentQueries;
import fintech.bo.components.attachments.AttachmentsComponents;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.CompleteTaskComponent;
import fintech.bo.components.task.TaskComponents;
import fintech.bo.components.task.TaskInput;
import fintech.bo.components.task.TaskQueries;
import fintech.bo.components.task.TaskResolution;
import fintech.bo.components.workflow.WorkflowQueries;
import fintech.bo.db.jooq.task.tables.records.TaskRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.util.List;
import java.util.Optional;

@Component
public class TaskHelper {

    @Autowired
    private ClientQueries clientQueries;

    @Autowired
    private TaskApiClient taskApiClient;

    @Autowired
    private WorkflowQueries workflowQueries;

    @Autowired
    private TaskQueries taskQueries;

    @Autowired
    private LoanApplicationQueries loanApplicationQueries;

    @Autowired
    private AttachmentQueries attachmentQueries;

    @Autowired
    private AttachmentsComponents attachmentsComponents;

    @Autowired
    private DSLContext db;

    @Autowired
    private TaskComponents taskComponents;

    @Autowired
    private ClientRepository clientRepository;

    public PropertyLayout callClientComponent(Long clientId) {
        ClientDTO client = clientRepository.getRequired(clientId);
        PropertyLayout layout = new PropertyLayout("Call client");
        layout.add("Mobile phone", client.getPhone());
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.setMargin(false);
        return layout;
    }


    private PropertyLayout clientComponent(Long clientId) {
        ClientDTO client = clientRepository.getRequired(clientId);
        PropertyLayout layout = new PropertyLayout("Client");
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Second first name", client.getSecondFirstName());
        layout.add("Maiden name", client.getMaidenName());
        layout.add("Document number", client.getDocumentNumber());
        layout.add("Date of birth", client.getDateOfBirth());
        layout.add("Gender", client.getGender());
        layout.setMargin(false);
        return layout;
    }

    public CompleteTaskComponent completeTaskComponent(TaskRecord task, List<TaskResolution> resolutions) {
        CompleteTaskComponent resolutionComponent = new CompleteTaskComponent(task, resolutions);
        resolutionComponent.getCompleteButton().addClickListener(e -> complete(task, resolutionComponent));
        return resolutionComponent;
    }

    private void complete(TaskRecord task, CompleteTaskComponent resolutionComponent) {
        TaskInput input = resolutionComponent.buildTaskInput();

        if (resolutionComponent.getPreCompleteValidation() != null) {
            Optional<String> validationResult = resolutionComponent.getPreCompleteValidation().apply(input);
            if (validationResult.isPresent()) {
                Notifications.errorNotification(validationResult.get());
                return;
            }
        }

        CompleteTaskRequest request = input.buildRequest();
        Call<TaskCountResponse> call = taskApiClient.complete(request);
        AbstractBackofficeUI ui = (AbstractBackofficeUI) UI.getCurrent();
        BackgroundOperations.callApi(request.isPostpone() ? "Postponing task" : "Task completed", call, t -> {
            Notifications.trayNotification(request.isPostpone() ? "Task postponed" : "Task completed");
            if (task.getParentTaskId() == null) {
                ui.getTabSheetNavigator().closeCurrentTab();
                if (t.getTasksDue() > 0) {
                    takeNextTask();
                }
            }
        }, Notifications::errorNotification);
    }

    public void takeNextTask() {
        Dialogs.confirm("Take next task?", e -> {
            Call<TakeNextTaskResponse> call = taskApiClient.takeNext();
            BackgroundOperations.callApi("Taking next task", call, this::openTask, Notifications::errorNotification);
        });
    }

    public WorkflowQueries getWorkflowQueries() {
        return workflowQueries;
    }

    public TaskQueries getTaskQueries() {
        return taskQueries;
    }

    public DSLContext getDb() {
        return db;
    }

    public ClientQueries getClientQueries() {
        return clientQueries;
    }

    public LoanApplicationQueries getLoanApplicationQueries() {
        return loanApplicationQueries;
    }

    private void openTask(TakeNextTaskResponse response) {
        if (response.getTaskId() == null) {
            return;
        }
        UI.getCurrent().getNavigator().navigateTo("task/" + response.getTaskId());
    }

    public AttachmentQueries getAttachmentQueries() {
        return attachmentQueries;
    }

    public AttachmentsComponents getAttachmentsComponents() {
        return attachmentsComponents;
    }

    public TaskComponents getTaskComponents() {
        return taskComponents;
    }

}
