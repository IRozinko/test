package fintech.bo.components.task;

import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.TaskApiClient;
import fintech.bo.api.model.task.AssignTaskRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import retrofit2.Call;

import java.util.List;

import static fintech.bo.db.jooq.task.Tables.AGENT;

public class ReassignTaskDialog extends ActionDialog {

    private Long taskId;
    private DSLContext db;
    private TaskApiClient taskApiClient;
    private ComboBox<String> comboBox;
    private TextArea comment;
    private boolean reassigned;

    public ReassignTaskDialog(Long taskId, DSLContext db, TaskApiClient taskApiClient) {
        super("Reassign task", "Reassign");
        this.taskId = taskId;
        this.db = db;
        this.taskApiClient = taskApiClient;
        setDialogContent(content());
        setModal(true);
        setWidth(400, Sizeable.Unit.PIXELS);
    }

    private Component content() {
        List<String> agents = db.select(AGENT.EMAIL).from(AGENT).where(AGENT.DISABLED.isFalse()).fetchInto(String.class);
        comboBox = new ComboBox<>("Select agent");
        comboBox.setItems(agents);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setRequiredIndicatorVisible(true);
        comboBox.setWidth(100, Unit.PERCENTAGE);

        comment = new TextArea("Comment");
        comment.setRows(3);
        comment.setWidth(100, Unit.PERCENTAGE);

        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(comboBox, comment);
        return layout;
    }


    @Override
    protected void executeAction() {
        String agent = comboBox.getValue();
        if (StringUtils.isBlank(agent)) {
            Notifications.errorNotification("Select agent first");
            return;
        }
        AssignTaskRequest request = new AssignTaskRequest();
        request.setTaskId(taskId);
        request.setAgent(agent);
        request.setComment(comment.getValue());
        Call<Void> call = taskApiClient.assign(request);
        BackgroundOperations.callApi("Assigning task", call, t -> {
            Notifications.trayNotification("Task assigned");
            this.reassigned = true;
            close();
        }, Notifications::errorNotification);
    }

    public boolean isReassigned() {
        return reassigned;
    }
}
