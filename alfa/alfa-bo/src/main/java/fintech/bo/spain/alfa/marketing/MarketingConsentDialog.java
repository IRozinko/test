package fintech.bo.spain.alfa.marketing;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.HasValue;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.ActivityApiClient;
import fintech.bo.api.model.activity.ActivityResponse;
import fintech.bo.api.model.marketing.ChangeMarketingConsentRequest;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.spain.alfa.api.MarketingConsentApiClient;
import lombok.SneakyThrows;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static fintech.bo.spain.alfa.marketing.MarketingConsentChangeSource.EMAIL;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;

class MarketingConsentDialog extends ActionDialog {

    private final MarketingConsentApiClient apiClient;
    private final ActivityApiClient activityApiClient;
    private final Runnable callback;

    private final Map<Long, ActivityResponse> incomingEmails;
    private final ComboBox<MarketingConsentChangeSource> source;
    private final TextArea note;
    private final ComboBox<ActivityResponse> incomingEmailActivity;


    private Binder<ChangeMarketingConsentRequest> binder;
    private ChangeMarketingConsentRequest req;

    public MarketingConsentDialog(MarketingConsentApiClient apiClient, ActivityApiClient activityApiClient,
                                  long clientId, boolean value, Runnable callback) {
        super("Update marketing consent", "Save");
        this.apiClient = apiClient;
        this.activityApiClient = activityApiClient;
        this.callback = callback;
        this.source = sourceCombobox();
        this.note = noteTextArea();
        this.incomingEmails = getIncomingEmails(clientId).stream().collect(toMap(ActivityResponse::getId, Function.identity()));
        this.incomingEmailActivity = incomingEmailActivityCombobox();


        this.req = new ChangeMarketingConsentRequest(clientId, EMAIL.name(), value);
        this.binder = new Binder<>();

        binder.forField(source)
            .asRequired()
            .bind(request -> MarketingConsentChangeSource.valueOf(request.getSource()), (request, src) -> request.setSource(src.name()));

        binder.forField(note)
            .bind(ChangeMarketingConsentRequest::getNote, ChangeMarketingConsentRequest::setNote);

        binder.forField(incomingEmailActivity)
            .withValidator(val -> !req.getSource().equals(EMAIL.name()) || val != null, "Can't be empty")
            .bind(request -> incomingEmails.get(request.getEmailActivityId()), (request, activity) -> {
                request.setEmailActivityId(activity.getId());
                request.setNote(activity.getComments());
            });

        binder.setBean(req);

        setDialogContent(form());
        setWidth(600, Unit.PIXELS);
    }

    private Component form() {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(source);
        layout.addComponent(incomingEmailActivity);
        layout.addComponent(note);
        return layout;
    }

    @Override
    @SneakyThrows
    protected void executeAction() {
        BinderValidationStatus<ChangeMarketingConsentRequest> validationStatus = binder.validate();
        if (validationStatus.isOk()) {
            apiClient.update(req).execute();
            this.close();
            callback.run();
        } else {
            validationStatus.notifyBindingValidationStatusHandlers();
        }
    }

    private ComboBox<MarketingConsentChangeSource> sourceCombobox() {
        ComboBox<MarketingConsentChangeSource> comboBox = new ComboBox<>("Source", asList(MarketingConsentChangeSource.values()));
        comboBox.setRequiredIndicatorVisible(true);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setSelectedItem(EMAIL);
        comboBox.addValueChangeListener(this::sourceChangeListener);
        return comboBox;
    }

    private void sourceChangeListener(HasValue.ValueChangeEvent<MarketingConsentChangeSource> src) {
        if (src.getValue() == EMAIL) {
            note.clear();
            note.setVisible(false);
            incomingEmailActivity.setVisible(true);
        } else {
            incomingEmailActivity.clear();
            incomingEmailActivity.setVisible(false);
            note.setVisible(true);
        }
    }

    @SneakyThrows
    private List<ActivityResponse> getIncomingEmails(long clientId) {
        return activityApiClient.findActivities(clientId, "IncomingEmail").execute().body();
    }

    private TextArea noteTextArea() {
        TextArea note = new TextArea("Note");
        note.setWidth(100, Unit.PERCENTAGE);
        note.setVisible(false);
        return note;
    }

    @SneakyThrows
    private ComboBox<ActivityResponse> incomingEmailActivityCombobox() {
        ComboBox<ActivityResponse> comboBox = new ComboBox<>("Choose incoming email");
        comboBox.setVisible(true);
        comboBox.setWidth(100, Unit.PERCENTAGE);
        comboBox.setEmptySelectionAllowed(false);
        comboBox.setItemCaptionGenerator(ActivityResponse::getComments);
        comboBox.setItems(incomingEmails.values());
        return comboBox;
    }

}
