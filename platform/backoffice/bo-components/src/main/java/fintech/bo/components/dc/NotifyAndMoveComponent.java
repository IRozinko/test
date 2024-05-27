package fintech.bo.components.dc;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.vaadin.data.Binder;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;
import fintech.TimeMachine;
import fintech.bo.api.client.CalendarApiClient;
import fintech.bo.api.client.CmsApiClient;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.calendar.BusinessDaysRequest;
import fintech.bo.api.model.calendar.BusinessDaysResponse;
import fintech.bo.api.model.dc.LogDebtActionRequest;
import fintech.bo.components.Formats;
import fintech.retrofit.RetrofitHelper;
import fintech.bo.components.background.BackgroundOperation;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.dc.tables.Debt;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Record;
import retrofit2.Call;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static fintech.bo.components.background.BackgroundOperations.run;
import static fintech.bo.components.dc.batch.BackgroundEditDebtOperation.voidProgressiveOperation;
import static fintech.bo.db.jooq.dc.Dc.DC;

@Slf4j
public class NotifyAndMoveComponent extends Window {

    private static final String SEND_NOTIFICATION = "SendNotification";
    private static final String SEND_EMAIL_BULK_ACTION = "SendEmail";
    private static final String SEND_SMS_BULK_ACTION = "SendSms";
    private static final Set<String> NEXT_ACTIONS = ImmutableSet.of("OutgoingCall");


    private static final int FIELD_WIDTH = 400;

    private DcApiClient dcApiClient;
    private CalendarApiClient calendarApiClient;
    private DcSettingsJson settings;

    private List<Record> debts;

    private Button saveButton;
    private final LogDebtActionRequest request;
    private TextArea commentsField;
    private ComboBox<String> nextActionField;
    private DateTimeField nextActionAtField;
    private Map<String, BulkActionComponent> bulkActionComponents;
    private Runnable saveCallback;

    public NotifyAndMoveComponent(CmsApiClient apiClient, CalendarApiClient calendarApiClient,
                                  DcApiClient dcApiClient, DcSettingsJson settings, Collection<Record> debts) {
        setModal(true);
        center();
        setWidth(40, Unit.PERCENTAGE);
        setHeight(90, Unit.PERCENTAGE);

        VerticalLayout layout = new VerticalLayout();

        bulkActionComponents =  new LinkedHashMap<>();
        this.request = new LogDebtActionRequest();
        this.dcApiClient = dcApiClient;
        this.calendarApiClient = calendarApiClient;
        this.settings = settings;
        this.debts = new ArrayList<>(debts);
        request.setActionName(SEND_NOTIFICATION);

        layout.addComponent(buildActionForm());

        saveButton = new Button("Save");
        saveButton.addStyleName(ValoTheme.BUTTON_PRIMARY);
        saveButton.addClickListener(e -> save());
        saveButton.setEnabled(true);

        SendEmailComponent sendEmailComponent = new SendEmailComponent(apiClient, false);
        sendEmailComponent.build(templates(SEND_EMAIL_BULK_ACTION));
        Panel emailPanel = new Panel("Email");
        emailPanel.setContent(sendEmailComponent);
        bulkActionComponents.put(SEND_EMAIL_BULK_ACTION, sendEmailComponent);

        SendSmsComponent sendSmsComponent = new SendSmsComponent(apiClient, false);
        sendSmsComponent.build(templates(SEND_SMS_BULK_ACTION));
        Panel smsPanel = new Panel("SMS");
        smsPanel.setContent(sendSmsComponent);
        bulkActionComponents.put(SEND_SMS_BULK_ACTION, sendSmsComponent);

        layout.addComponent(smsPanel);
        layout.addComponent(emailPanel);
        layout.addComponent(saveButton);

        setContent(layout);
    }

    public void setSaveCallback(Runnable callback) {
        this.saveCallback = callback;
    }

    private Panel buildActionForm() {
        Binder<LogDebtActionRequest> binder = new Binder<>(LogDebtActionRequest.class);
        binder.setBean(this.request);

        commentsField = new TextArea("Comments");
        commentsField.setRows(3);
        commentsField.setWidth(100, Unit.PERCENTAGE);
        binder.bind(commentsField, LogDebtActionRequest::getComments, LogDebtActionRequest::setComments);

        nextActionField = new ComboBox<>("Next action");
        nextActionField.setEmptySelectionAllowed(false);
        nextActionField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        nextActionField.setTextInputAllowed(false);
        nextActionField.setItems();
        nextActionField.setItems(NEXT_ACTIONS);
        binder.bind(nextActionField, LogDebtActionRequest::getNextAction, LogDebtActionRequest::setNextAction);

        int minDaysToAdd = minDaysForNextAction();
        nextActionAtField = new DateTimeField("Next action date");
        nextActionAtField.setWidth(FIELD_WIDTH, Unit.PIXELS);
        nextActionAtField.setDateFormat(Formats.LONG_DATETIME_FORMAT);
        nextActionAtField.setRangeStart(TimeMachine.now());

        // Resolving dueDate for next action date through WEB API
        resolveBusinessTime(minDaysToAdd, time -> {
            nextActionAtField.setRangeEnd(time.plusHours(1));
        });

        binder.forField(nextActionAtField)
            .bind(LogDebtActionRequest::getNextActionAt, LogDebtActionRequest::setNextActionAt);

        FormLayout formLayout = new FormLayout();
        formLayout.setMargin(true);
        formLayout.addComponents(
            commentsField,
            nextActionField,
            nextActionAtField);

        Panel mainPanel = new Panel("Action");
        mainPanel.setContent(formLayout);
        return mainPanel;
    }

    private void resolveBusinessTime(int daysToAdd, Consumer<LocalDateTime> timeConsumer) {
        BusinessDaysRequest request = new BusinessDaysRequest()
            .setOrigin(TimeMachine.now()).setAmountToAdd(daysToAdd).setUnit(ChronoUnit.DAYS);

        Call<BusinessDaysResponse> call = calendarApiClient.resolveBusinessTime(request);
        BackgroundOperations.callApiSilent(call, r -> timeConsumer.accept(r.getBusinessTime()), Notifications::errorNotification);
    }

    private int minDaysForNextAction() {
        return debts.stream()
            .map(r -> r.get(DC.DEBT.STATUS, String.class))
            .map(settings::getOptionalAgentActionStatusTemplate)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(status -> status.getNextActions().stream()
                .filter(action -> NEXT_ACTIONS.contains(action.getType())))
            .flatMap(Function.identity())
            .mapToInt(DcSettingsJson.AgentNextAction::getNextActionInDays)
            .min()
            .orElse(0);
    }


    private List<String> templates(String bulkAction) {
        Set<String> cmsKeys = debts.stream()
            .map(r -> r.get(DC.DEBT.STATUS, String.class))
            .map(status -> getStatusTemplates(status, bulkAction))
            .reduce((s1, s2) -> Sets.intersection(s1, s2).immutableCopy())
            .orElse(Collections.emptySet());

        return new ArrayList<>(cmsKeys);
    }

    private Set<String> getStatusTemplates(String status, String bulkAction) {
        Optional<DcSettingsJson.AgentActionStatus> maybeStatus = settings.getOptionalAgentActionStatusTemplate(status);

        return maybeStatus.map(DcSettingsJson.AgentActionStatus::getBulkActions)
            .orElse(Collections.emptyList())
            .stream()
            .filter(action -> bulkAction.equals(action.getType()))
            .findAny()
            .map(ba -> (Set<String>) new HashSet(ba.getRequiredParam("cmsKeys", List.class)))
            .orElse(new HashSet<>());
    }

    private void save() {
        request.getBulkActions().clear();

        List<String> errors = new ArrayList<>();
        bulkActionComponents.forEach((k, v) -> v.validate().ifPresent(errors::add));

        if (!errors.isEmpty()) {
            String errorMessage = errors.stream().collect(Collectors.joining("\n"));
            Notifications.errorNotification(errorMessage);
            return;
        }

        bulkActionComponents.forEach((k, v) -> {
            LogDebtActionRequest.BulkAction data = v.saveData();
            if (data != null) {
                request.getBulkActions().put(k, data);
            }
        });

        BackgroundOperation<Void> voidBackgroundOperation = voidProgressiveOperation(debts, record -> {
            request.setDebtId(record.get(Debt.DEBT.ID));
            Call<IdResponse> call = dcApiClient.logDebtAction(request);
            RetrofitHelper.syncCall(call);
        });

        run("Executing..", voidBackgroundOperation, exp -> Notifications.trayNotification("Completed"),
            exp -> Notifications.errorNotification("Failed"));

        saveCallback.run();
        close();
    }

}
