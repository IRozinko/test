package fintech.bo.components.workflow;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.data.converter.StringToIntegerConverter;
import com.vaadin.ui.*;
import fintech.bo.api.client.WorkflowApiClient;
import fintech.bo.api.model.workflow.ActivityInfoResponse;
import fintech.bo.api.model.workflow.AddEditDynamicActivityListenerRequest;
import fintech.bo.api.model.workflow.WorkflowInfoResponse;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.workflow.tables.records.ActivityListenerRecord;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import retrofit2.Call;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fintech.bo.api.model.workflow.AddEditDynamicActivityListenerRequest.STATUS_COMPLETED;
import static fintech.bo.api.model.workflow.AddEditDynamicActivityListenerRequest.STATUS_STARTED;

@Slf4j
public class EditActivityListenerDialog extends ActionDialog {

    private final WorkflowApiClient workflowApiClient;
    private final List<String[]> params;
    private final List<ActivityInfoResponse> activities;
    private final String workflowName;
    private final int wfVersion;

    private final Binder<AddEditDynamicActivityListenerRequest> binder = new Binder<>();

    @SneakyThrows
    public EditActivityListenerDialog(WorkflowApiClient workflowApiClient,
                                      ActivityListenerRecord item,
                                      @Nonnull String workflowName,
                                      int wfVersion,
                                      String triggerName,
                                      List<String[]> params) {
        super(item.getId() == null ? "Add item" : "Edit item", "Save");
        this.workflowApiClient = workflowApiClient;
        this.params = params;
        this.workflowName = workflowName;
        this.wfVersion = wfVersion;
        this.activities = workflowApiClient.listWorkflows().execute().body().stream()
            .filter(wf -> wf.getWorkflowName().equals(workflowName) && wf.getWorkflowVersion() == wfVersion)
            .findFirst().map(WorkflowInfoResponse::getActivities)
            .orElseThrow(() -> new IllegalStateException(String.format("No activities found for workflow [%s] and version [%s]", workflowName, wfVersion)));

        AddEditDynamicActivityListenerRequest request = new AddEditDynamicActivityListenerRequest();
        request.setId(item.getId());
        request.setTriggerName(triggerName);
        request.setWorkflowName(workflowName);
        request.setVersion(wfVersion);
        request.setName(item.getName());
        request.setActivityName(firstNonNull(item.getActivityName(), activities.isEmpty() ? null : activities.get(0).getActivityName()));
        request.setDelaySec(firstNonNull(item.getDelaySec(), 0));
        request.setFromMidnight(firstNonNull(item.getFromMidnight(), false));
        request.setResolution(item.getResolution());
        request.setParams(item.getParams());
        request.setListenerStatus(firstNonNull(item.getActivityStatus(), STATUS_STARTED));
        binder.setBean(request);

        setDialogContent(editor());
        setWidth(800, Unit.PIXELS);
        setHeight(800, Unit.PIXELS);
    }

    private Component editor() {
        FormLayout layout = new FormLayout();
        layout.setSizeFull();

        layout.addComponent(new Label(String.format("Workflow [%s], version [%s]", workflowName, wfVersion)));

        TextField name = new TextField("Name");
        name.setWidth(100, Unit.PERCENTAGE);

        binder.forField(name)
            .asRequired()
            .bind(AddEditDynamicActivityListenerRequest::getName, AddEditDynamicActivityListenerRequest::setName);
        layout.addComponent(name);

        ComboBox<String> status = new ComboBox<>("Status");
        status.setWidth(100, Unit.PERCENTAGE);
        status.setEmptySelectionAllowed(false);
        status.setTextInputAllowed(false);
        status.setItems(STATUS_STARTED, STATUS_COMPLETED);

        ComboBox<String> resolution = new ComboBox<>("Resolution");
        resolution.setWidth(100, Unit.PERCENTAGE);
        resolution.setEmptySelectionAllowed(false);
        resolution.setTextInputAllowed(false);

        ComboBox<String> activity = new ComboBox<>("Activity");

        activity.addValueChangeListener((HasValue.ValueChangeListener<String>) event -> {
                List<String> resolutions = Lists.newLinkedList(activities.stream()
                    .filter(a -> a.getActivityName().equals(event.getValue()))
                    .findFirst()
                    .map(ActivityInfoResponse::getResolutions).orElse(Sets.newHashSet()));
                resolution.setItems(resolutions);
                if (!resolutions.isEmpty()) {
                    resolution.setValue(resolutions.iterator().next());
                }
            }
        );

        activity.setItems(activities.stream().map(ActivityInfoResponse::getActivityName).collect(Collectors.toList()));
        activity.setWidth(100, Unit.PERCENTAGE);
        activity.setEmptySelectionAllowed(false);
        activity.setTextInputAllowed(false);

        TextField delay = new TextField("Delay, sec");
        delay.setWidth(100, Unit.PERCENTAGE);
        CheckBox fromMidnight = new CheckBox("From midnight");

        binder.forField(activity)
            .asRequired()
            .bind(AddEditDynamicActivityListenerRequest::getActivityName, AddEditDynamicActivityListenerRequest::setActivityName);

        status.addValueChangeListener((HasValue.ValueChangeListener<String>) event -> {
                if (event.getValue().equals(STATUS_STARTED)) {
                    resolution.setEnabled(false);
                    delay.setEnabled(true);
                    fromMidnight.setEnabled(true);
                } else {
                    resolution.setEnabled(true);
                    delay.setEnabled(false);
                    fromMidnight.setEnabled(false);
                }
            }
        );

        binder.bind(resolution, AddEditDynamicActivityListenerRequest::getResolution, AddEditDynamicActivityListenerRequest::setResolution);
        binder.bind(status, AddEditDynamicActivityListenerRequest::getListenerStatus, AddEditDynamicActivityListenerRequest::setListenerStatus);

        layout.addComponent(activity);
        layout.addComponent(status);
        layout.addComponent(resolution);

        binder.forField(delay)
            .asRequired()
            .withConverter(new StringToIntegerConverter("Positive number"))
            .withValidator(v -> v >= 0, "Invalid value")
            .bind(AddEditDynamicActivityListenerRequest::getDelaySec, AddEditDynamicActivityListenerRequest::setDelaySec);
        layout.addComponent(delay);

        binder.forField(fromMidnight)
            .bind(AddEditDynamicActivityListenerRequest::getFromMidnight, AddEditDynamicActivityListenerRequest::setFromMidnight);

        layout.addComponent(fromMidnight);

        ComboBox<String[]> value = new ComboBox<>("Value");
        value.setItems(params);
        value.setWidth(100, Unit.PERCENTAGE);
        value.setEmptySelectionAllowed(false);
        value.setTextInputAllowed(false);
        value.setItemCaptionGenerator((ItemCaptionGenerator<String[]>) Arrays::toString);
        binder.forField(value)
            .asRequired()
            .bind(AddEditDynamicActivityListenerRequest::getParams, AddEditDynamicActivityListenerRequest::setParams);
        layout.addComponent(value);

        return layout;
    }

    @Override
    protected void executeAction() {
        BinderValidationStatus<AddEditDynamicActivityListenerRequest> validationStatus = binder.validate();
        if (validationStatus.isOk()) {
            AddEditDynamicActivityListenerRequest bean = validationStatus.getBinder().getBean();
            AddEditDynamicActivityListenerRequest request = new AddEditDynamicActivityListenerRequest();
            BeanUtils.copyProperties(bean, request);
            if (request.getListenerStatus().equals(STATUS_STARTED)) {
                request.setResolution(null);
            } else {
                if (request.getResolution() == null) {
                    Notifications.errorNotification("Set resolution");
                    return;
                }
                request.setDelaySec(null);
                request.setFromMidnight(null);
            }
            Call<Void> call = workflowApiClient.addEditDynamicActivityListener(request);
            BackgroundOperations.callApi(request.getId() == null ? "Adding listener" : "Editing listener", call, v -> {
                Notifications.trayNotification("Done");
                close();
            }, Notifications::errorNotification);
        } else {
            Notifications.errorNotification("Fix validation errors");
        }
    }
}
