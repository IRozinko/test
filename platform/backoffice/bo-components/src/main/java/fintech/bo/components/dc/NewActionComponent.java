package fintech.bo.components.dc;

import com.google.common.base.MoreObjects;
import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.calendar.BusinessDaysRequest;
import fintech.bo.api.model.calendar.BusinessDaysResponse;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import org.apache.commons.lang3.StringUtils;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class NewActionComponent extends VerticalLayout {

    public static final int FIELD_WIDTH = 400;

    private final LogDebtActionRequest request;
    private final DebtRecord debt;
    private final DcApiClient dcApiClient;
    private final CalendarApiClient calendarApiClient;

    private final List<DcSettingsJson.AgentAction> agentActions;
    private List<String> actionItems;
    private DcSettingsJson.AgentAction action;
    private DcSettingsJson.AgentActionStatus status;

    private VerticalLayout bulkActionsLayout;
    private Map<DcSettingsJson.BulkAction, BulkActionComponent> bulkActionComponents = new LinkedHashMap<>();
    private VerticalLayout statusBulkActionsLayout;
    private Map<DcSettingsJson.BulkAction, BulkActionComponent> statusBulkActionComponents = new LinkedHashMap<>();
    private ComboBox<String> statusField;
    private ComboBox<String> subStatusField;
    private ComboBox<String> resolutionField;
    private ComboBox<String> nextActionField;
    private DateTimeField nextActionAtField;
    private ComboBox<String> actionField;

    private final Map<String, Supplier<BulkActionComponent>> bulkActionHandlers;
    private Button saveButton;
    private Button saveAndCloseButton;
    private TextArea commentsField;

    public NewActionComponent(DcSettingsJson settings, DebtRecord debt, DcApiClient dcApiClient, CalendarApiClient calendarApiClient, Map<String, Supplier<BulkActionComponent>> bulkActionHandlers) {
        this.debt = debt;
        this.dcApiClient = dcApiClient;
        this.calendarApiClient = calendarApiClient;
        this.bulkActionHandlers = bulkActionHandlers;
        this.request = new LogDebtActionRequest();
        this.request.setDebtId(debt.getId());
        this.agentActions = filterByUserRole(settings.getAgentActionsByPortfolio(this.debt.getPortfolio()));

        addComponent(buildUi());
    }

    private List<DcSettingsJson.AgentAction> filterByUserRole(List<DcSettingsJson.AgentAction> agentActionsByPortfolio) {
        return agentActionsByPortfolio.stream()
            .filter(a -> a.getUserRoles().isEmpty() || LoginService.isInAnyRole(a.getUserRoles()))
            .collect(Collectors.toList());
    }

    private Component buildUi() {
        Panel mainPanel = buildActionForm();

        bulkActionsLayout = new VerticalLayout();
        bulkActionsLayout.setMargin(new MarginInfo(true, false, true, false));

        statusBulkActionsLayout = new VerticalLayout();
        statusBulkActionsLayout.setMargin(new MarginInfo(true, false, true, false));

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(e -> save(false));
        saveButton.setEnabled(false);

        saveAndCloseButton = new Button("Save & Close");
        saveAndCloseButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveAndCloseButton.addClickListener(e -> save(true));
        saveAndCloseButton.setEnabled(false);


        VerticalLayout rootLayout = new VerticalLayout();
        rootLayout.setMargin(false);
        rootLayout.setSpacing(false);
        rootLayout.addComponent(mainPanel);
        rootLayout.addComponent(bulkActionsLayout);
        rootLayout.addComponent(statusBulkActionsLayout);
        rootLayout.addComponent(new HorizontalLayout(saveButton, saveAndCloseButton));

        actionField.addValueChangeListener(this::actionUpdated);
        statusField.addValueChangeListener(this::statusUpdated);
        subStatusField.addValueChangeListener(this::subStatusUpdated);
        nextActionAtField.addValueChangeListener(this::nextActionDateUpdated);

        if (actionItems.contains(this.debt.getNextAction())) {
            actionField.setValue(this.debt.getNextAction());
        }

        return rootLayout;
    }

    private Panel buildActionForm() {
        Binder<LogDebtActionRequest> binder = new Binder<>(LogDebtActionRequest.class);
        binder.setBean(this.request);

        actionField = new ComboBox<>("Action");
        actionField.setEmptySelectionAllowed(false);
        actionField.setTextInputAllowed(false);
        actionItems = agentActions.stream().map(DcSettingsJson.AgentAction::getType).collect(Collectors.toList());
        actionField.setItems(actionItems);
        actionField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        binder.bind(actionField, LogDebtActionRequest::getActionName, LogDebtActionRequest::setActionName);

        commentsField = new TextArea("Comments");
        commentsField.setRows(3);
        commentsField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(commentsField, LogDebtActionRequest::getComments, LogDebtActionRequest::setComments);

        resolutionField = new ComboBox<>("Resolution");
        resolutionField.setEmptySelectionAllowed(false);
        resolutionField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        resolutionField.setTextInputAllowed(false);
        resolutionField.setVisible(false);
        binder.bind(resolutionField, LogDebtActionRequest::getResolution, LogDebtActionRequest::setResolution);

        TextField currentStatusField = new TextField("Current status");
        currentStatusField.setValue(MoreObjects.firstNonNull(debt.getStatus(), "-"));
        currentStatusField.setReadOnly(true);
        currentStatusField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        currentStatusField.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);

        TextField currentSubStatusField = new TextField("Current Sub status");
        currentSubStatusField.setReadOnly(true);
        currentSubStatusField.setVisible(false);
        currentSubStatusField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        currentSubStatusField.addStyleName(ValoTheme.TEXTAREA_BORDERLESS);

        if (StringUtils.isNotEmpty(debt.getSubStatus())) {
            currentSubStatusField.setValue(MoreObjects.firstNonNull(debt.getSubStatus(), "-"));
            currentSubStatusField.setVisible(true);
        }

        statusField = new ComboBox<>("New status");
        statusField.setEmptySelectionAllowed(false);
        statusField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        statusField.setTextInputAllowed(false);
        statusField.setVisible(false);
        binder.bind(statusField, LogDebtActionRequest::getStatus, LogDebtActionRequest::setStatus);

        subStatusField = new ComboBox<>("New Sub status");
        subStatusField.setEmptySelectionAllowed(false);
        subStatusField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        subStatusField.setTextInputAllowed(false);
        subStatusField.setVisible(false);
        binder.bind(subStatusField, LogDebtActionRequest::getSubStatus, LogDebtActionRequest::setSubStatus);


        nextActionField = new ComboBox<>("Next action");
        nextActionField.setEmptySelectionAllowed(false);
        nextActionField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        nextActionField.setTextInputAllowed(false);
        nextActionField.setVisible(false);
        binder.bind(nextActionField, LogDebtActionRequest::getNextAction, LogDebtActionRequest::setNextAction);

        nextActionAtField = new DateTimeField("Next action date");
        nextActionAtField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        nextActionAtField.setDateFormat(Formats.LONG_DATETIME_FORMAT);
        nextActionAtField.setVisible(false);
        binder.forField(nextActionAtField)
            .bind(LogDebtActionRequest::getNextActionAt, LogDebtActionRequest::setNextActionAt);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        formLayout.addComponents(
            actionField,
            resolutionField,
            currentStatusField,
            currentSubStatusField,
            statusField,
            subStatusField,
            commentsField,
            nextActionField,
            nextActionAtField);

        Panel mainPanel = new Panel("Action");
        mainPanel.setContent(formLayout);
        return mainPanel;
    }

    private void nextActionDateUpdated(HasValue.ValueChangeEvent<LocalDateTime> event) {
        saveButton.setEnabled(isFormValid());
        saveAndCloseButton.setEnabled(isFormValid());
    }

    private void subStatusUpdated(HasValue.ValueChangeEvent<String> event) {
        if (!status.getAgentActionSubStatuses().isEmpty()) {
            Optional<DcSettingsJson.AgentActionSubStatus> agentActionSubStatus = status.getAgentActionSubStatuses().stream()
                .filter(s -> s.getName().equals(event.getValue()))
                .findFirst();
            if (!agentActionSubStatus.isPresent()) {
                throw new IllegalArgumentException("Sub status not found");
            }
        }
    }

    private void statusUpdated(HasValue.ValueChangeEvent<String> event) {
        statusBulkActionsLayout.removeAllComponents();
        statusBulkActionsLayout.setVisible(false);
        statusBulkActionComponents.clear();

        boolean saveEnabled = isFormValid();
        saveButton.setEnabled(saveEnabled);
        saveAndCloseButton.setEnabled(saveEnabled);

        if (event.getValue() == null) {
            nextActionField.setValue(null);
            nextActionField.setVisible(true);
            nextActionAtField.setValue(null);
            nextActionAtField.setVisible(true);
            return;
        }

        status = action.getStatuses().stream().filter(s -> s.getName().equals(event.getValue())).findFirst().orElseThrow(() -> new IllegalArgumentException("Status not found"));

        if (!status.getNextActions().isEmpty()) {
            DcSettingsJson.AgentNextAction first = status.getNextActions().get(0);


            nextActionField.setVisible(true);
            nextActionField.setItems(status.getNextActions().stream().map(DcSettingsJson.AgentNextAction::getType));
            nextActionField.setValue(first.getType());

            nextActionAtField.setVisible(true);
            nextActionAtField.setRangeStart(TimeMachine.now());

            // Resolving dueDate for next action date through WEB API
            resolveBusinessTime(first.getNextActionInDays(), time -> {
                this.request.setNextActionAt(time);
                nextActionAtField.setValue(time);
                nextActionAtField.setRangeEnd(time.plusHours(1));

            });
        } else {
            nextActionField.setVisible(false);
            nextActionAtField.setVisible(false);
        }

        if (!StringUtils.isBlank(status.getDefaultResolution())) {
            resolutionField.setValue(status.getDefaultResolution());
        }

        Validate.notNull(status.getBulkActions(), "Null bulk actions for status [%s]", status.getName());
        for (DcSettingsJson.BulkAction bulkAction : status.getBulkActions()) {
            addBulkAction(bulkAction, statusBulkActionComponents, statusBulkActionsLayout);
        }
        statusBulkActionsLayout.setVisible(!statusBulkActionComponents.isEmpty());

        List<String> subStatuses = getSubStatuses(event.getValue());
        if (subStatuses.isEmpty()) {
            clearSubStatus();
        } else {
            addSubStatus(subStatuses);
        }
    }

    private void resolveBusinessTime(int daysToAdd, Consumer<LocalDateTime> timeConsumer) {
        BusinessDaysRequest request = new BusinessDaysRequest()
            .setOrigin(TimeMachine.now()).setAmountToAdd(daysToAdd).setUnit(ChronoUnit.DAYS);

        Call<BusinessDaysResponse> call = calendarApiClient.resolveBusinessTime(request);
        BackgroundOperations.callApiSilent(call, r -> timeConsumer.accept(r.getBusinessTime()), Notifications::errorNotification);
    }


    private void actionUpdated(HasValue.ValueChangeEvent<String> event) {
        bulkActionsLayout.removeAllComponents();
        bulkActionComponents.clear();
        bulkActionsLayout.setVisible(false);

        saveButton.setEnabled(isFormValid());
        saveAndCloseButton.setEnabled(isFormValid());

        if (event.getValue() == null) {
            clearStatus();
            clearSubStatus();
            resolutionField.setValue(null);
            resolutionField.setVisible(false);
            return;
        }

        action = agentActions.stream().filter(a -> a.getType().equals(event.getValue())).findFirst().orElseThrow(() -> new IllegalArgumentException("Action not found"));
        if (action.getStatuses().isEmpty()) {
            clearStatus();
            clearSubStatus();
        } else {
            List<String> items = action.getStatuses().stream().map(DcSettingsJson.AgentActionStatus::getName).collect(Collectors.toList());
            statusField.setItems(items);
            statusField.setPageLength(items.size());
            statusField.setVisible(true);
        }
        if (action.getResolutions().isEmpty()) {
            resolutionField.setVisible(false);
            resolutionField.setValue(null);
        } else {
            resolutionField.setItems(action.getResolutions());
            resolutionField.setVisible(true);
            resolutionField.setValue(action.getResolutions().get(0));
        }
        for (DcSettingsJson.BulkAction bulkAction : action.getBulkActions()) {
            addBulkAction(bulkAction, bulkActionComponents, bulkActionsLayout);
        }
        bulkActionsLayout.setVisible(!bulkActionComponents.isEmpty());
    }

    private List<String> getSubStatuses(String selectedStatus) {
        return action.getStatuses()
            .stream().filter(s -> s.getName().equals(selectedStatus))
            .flatMap(s -> s.getAgentActionSubStatuses().stream())
            .map(DcSettingsJson.AgentActionSubStatus::getName)
            .collect(Collectors.toList());
    }

    private void clearStatus() {
        statusField.setVisible(false);
        statusField.setValue(null);
    }

    private void clearSubStatus() {
        subStatusField.setVisible(false);
        subStatusField.setValue(null);
    }

    private void addSubStatus(List<String> items) {
        subStatusField.setVisible(true);
        subStatusField.setValue(items.get(0));
        subStatusField.setItems(items);
    }

    private void addBulkAction(DcSettingsJson.BulkAction bulkAction, Map<DcSettingsJson.BulkAction, BulkActionComponent> components, VerticalLayout layout) {
        Supplier<BulkActionComponent> supplier = bulkActionHandlers.get(bulkAction.getType());
        Validate.notNull(supplier, "Bulk action not found by type %s", bulkAction.getType());
        BulkActionComponent component = supplier.get();
        component.build(this, bulkAction);
        components.put(bulkAction, component);

        if (component.getComponentCount() > 0) {
            Panel panel = new Panel(bulkAction.getType());
            panel.setCaption(bulkAction.getType());
            panel.setContent(component);
            layout.addComponent(panel);
        }
    }

    private void save(boolean closeTab) {
        request.getBulkActions().clear();

        List<String> errors = new ArrayList<>();
        bulkActionComponents.forEach((k, v) -> {
            v.validate().ifPresent(errors::add);
        });
        statusBulkActionComponents.forEach((k, v) -> {
            v.validate().ifPresent(errors::add);
        });
        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().collect(Collectors.joining("\n"));
            Notifications.errorNotification(errorMessage);
            return;
        }

        bulkActionComponents.forEach((k, v) -> {
            LogDebtActionRequest.BulkAction data = v.saveData();
            if (data != null) {
                request.getBulkActions().put(k.getType(), data);
            }
        });
        statusBulkActionComponents.forEach((k, v) -> {
            LogDebtActionRequest.BulkAction data = v.saveData();
            if (data != null) {
                request.getBulkActions().put(k.getType(), data);
            }
        });

        Call<IdResponse> call = dcApiClient.logDebtAction(request);
        BackgroundOperations.callApi("Saving", call, r -> {
            Notifications.trayNotification("Action saved");
            onActionSaved();
            if (closeTab) {
                AbstractBackofficeUI ui = (AbstractBackofficeUI) UI.getCurrent();
                ui.getTabSheetNavigator().closeCurrentTab();
            }
        }, Notifications::errorNotification);
    }

    //Have no idea why Vaadin doesn't provide any abstract form validation checks
    private boolean isFormValid() {
        return nextActionAtField.getErrorMessage() == null
            && nextActionField.getErrorMessage() == null
            && actionField.getErrorMessage() == null;
    }

    protected void onActionSaved() {
    }

    public LogDebtActionRequest getRequest() {
        return request;
    }

    public DebtRecord getDebt() {
        return debt;
    }

    public DateTimeField getNextActionAtField() {
        return nextActionAtField;
    }
}
