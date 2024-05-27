package fintech.bo.components.activity;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.Validate;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.activity.AddActivityRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import retrofit2.Call;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AddActivityComponent extends VerticalLayout {

    public static final int FIELD_WIDTH = 400;

    private final ActivityApiClient apiClient;
    private final ActivitySettingsJson settings;
    private final Long clientId;
    private final Map<String, Supplier<BulkActionComponent>> bulkActionHandlers;
    private final AddActivityRequest model;

    private ComboBox<String> resolutionField;
    private ComboBox<String> topicField;
    private Button saveButton;
    private VerticalLayout bulkActionsLayout;
    private final Map<ActivitySettingsJson.BulkAction, BulkActionComponent> bulkActionComponents = new LinkedHashMap<>();
    private Runnable onSavedCallback;

    public AddActivityComponent(ActivityApiClient apiClient, ActivitySettingsJson settings, Long clientId, Map<String, Supplier<BulkActionComponent>> bulkActionHandlers) {
        this.apiClient = apiClient;
        this.settings = settings;
        this.clientId = clientId;
        this.bulkActionHandlers = bulkActionHandlers;
        this.model = new AddActivityRequest();
        this.model.setClientId(clientId);

        addComponent(buildUi());
    }

    private Component buildUi() {
        Panel mainPanel = buildActionForm();

        bulkActionsLayout = new VerticalLayout();
        bulkActionsLayout.setMargin(new MarginInfo(true, false, true, false));

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(e -> save());
        saveButton.setEnabled(false);

        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setMargin(false);
        rootLayout.setSpacing(false);
        rootLayout.addComponent(mainPanel);
        rootLayout.addComponent(bulkActionsLayout);
        rootLayout.addComponent(saveButton);

        return rootLayout;
    }

    private void save() {
        model.getBulkActions().clear();
        bulkActionComponents.forEach((k, v) -> {
            AddActivityRequest.BulkAction data = v.saveData();
            if (data != null) {
                model.getBulkActions().add(data);
            }
        });
        Call<IdResponse> call = apiClient.addActivity(model);
        BackgroundOperations.callApi("Saving", call, r -> {
            Notifications.trayNotification("Activity saved");
            if (onSavedCallback != null) {
                onSavedCallback.run();
            }
        }, Notifications::errorNotification);
    }

    private Panel buildActionForm() {
        Binder<AddActivityRequest> binder = new Binder<>(AddActivityRequest.class);
        binder.setBean(this.model);

        ComboBox<String> activityField = new ComboBox<>("Activity");
        activityField.setEmptySelectionAllowed(false);
        activityField.setTextInputAllowed(false);
        activityField.setItems(settings.getAgentActions().stream().map(ActivitySettingsJson.AgentAction::getName).collect(Collectors.toList()));
        activityField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        binder.bind(activityField, AddActivityRequest::getAction, AddActivityRequest::setAction);
        activityField.addValueChangeListener(this::activityUpdated);

        topicField = new ComboBox<>("Topic");
        topicField.setEmptySelectionAllowed(false);
        topicField.setTextInputAllowed(false);
        topicField.setItems(settings.getTopics());
        topicField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        topicField.setPageLength(20);
        binder.bind(topicField, AddActivityRequest::getTopic, AddActivityRequest::setTopic);

        TextArea commentsField = new TextArea("Comments");
        commentsField.setRows(3);
        commentsField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(commentsField, AddActivityRequest::getComments, AddActivityRequest::setComments);

        resolutionField = new ComboBox<>("Resolution");
        resolutionField.setEmptySelectionAllowed(false);
        resolutionField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        resolutionField.setTextInputAllowed(false);
        resolutionField.setVisible(false);
        binder.bind(resolutionField, AddActivityRequest::getResolution, AddActivityRequest::setResolution);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        formLayout.addComponents(
            activityField,
            resolutionField,
            topicField,
            commentsField);

        Panel mainPanel = new Panel("New Activity");
        mainPanel.setContent(formLayout);
        return mainPanel;
    }

    private void activityUpdated(HasValue.ValueChangeEvent<String> event) {
        bulkActionsLayout.removeAllComponents();
        bulkActionComponents.clear();
        bulkActionsLayout.setVisible(false);
        saveButton.setEnabled(event.getValue() != null);
        resolutionField.setValue(null);
        topicField.clear();
        topicField.setValue(null);

        if (event.getValue() == null) {
            resolutionField.setValue(null);
            resolutionField.setVisible(false);
            return;
        }

        ActivitySettingsJson.AgentAction activity = settings.getAgentActions().stream().filter(a -> a.getName().equals(event.getValue())).findFirst().orElseThrow(() -> new IllegalArgumentException("Unknown activity"));

        resolutionField.setItems(activity.getResolutions());
        resolutionField.setVisible(!activity.getResolutions().isEmpty());
        if ((activity.getTopics() != null) && !activity.getTopics().isEmpty()) {
            topicField.setItems(activity.getTopics());
        } else {
            topicField.setItems(settings.getTopics());
        }

        for (ActivitySettingsJson.BulkAction bulkAction : activity.getBulkActions()) {
            addBulkAction(bulkAction);
        }
        bulkActionsLayout.setVisible(!bulkActionComponents.isEmpty());
    }

    private void addBulkAction(ActivitySettingsJson.BulkAction bulkAction) {
        Supplier<BulkActionComponent> supplier = bulkActionHandlers.get(bulkAction.getType());
        Validate.notNull(supplier, "Bulk action not found by type %s", bulkAction.getType());
        BulkActionComponent component = supplier.get();
        component.build(this, bulkAction);
        bulkActionComponents.put(bulkAction, component);

        Panel panel = new Panel(bulkAction.getType());
        panel.setCaption(bulkAction.getType());
        panel.setContent(component);
        bulkActionsLayout.addComponent(panel);
    }

    public void setOnSavedCallback(Runnable onSavedCallback) {
        this.onSavedCallback = onSavedCallback;
    }

    public Long getClientId() {
        return clientId;
    }
}
