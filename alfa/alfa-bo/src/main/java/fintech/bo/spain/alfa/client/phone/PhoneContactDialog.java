package fintech.bo.spain.alfa.client.phone;

import com.vaadin.data.Binder;
import com.vaadin.data.BinderValidationStatus;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.bo.api.model.client.PhoneContactRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.db.jooq.crm.tables.records.PhoneContactRecord;
import fintech.bo.spain.alfa.api.PhoneContactApiClient;
import lombok.SneakyThrows;
import retrofit2.Call;

import java.util.List;

import static java.util.Arrays.asList;

public abstract class PhoneContactDialog extends ActionDialog {

    private static final List<String> PHONE_SOURCE = asList("REGISTRATION", "CLIENT", "INCOMING", "DETECTIVE_AGENCY", "SEARCH", "OTHER");
    private static final List<String> PHONE_TYPE = asList("MOBILE", "LANDLINE", "OTHER");

    private Binder<PhoneContactRequest> binder;
    private Runnable callback;
    protected PhoneContactRequest request;
    protected PhoneContactApiClient api;

    public static PhoneContactDialog createPhone(long clientId, PhoneContactApiClient phoneContactApiClient, Runnable callback) {
        PhoneContactRequest req = new PhoneContactRequest(clientId)
            .setType(PHONE_TYPE.get(0))
            .setSource(PHONE_SOURCE.get(0));
        return new CreatePhoneContactDialog(req, phoneContactApiClient, callback);
    }

    public static PhoneContactDialog updatePhone(PhoneContactRecord record, PhoneContactApiClient phoneContactApiClient, Runnable callback) {
        return new UpdatePhoneContactDialog(toRequest(record), phoneContactApiClient, callback);
    }

    protected PhoneContactDialog(PhoneContactRequest request, PhoneContactApiClient phoneContactApiClient, Runnable callback) {
        super("Phone Contact", "Save");
        this.api = phoneContactApiClient;
        this.callback = callback;
        setModal(true);
        setWidth(400, Unit.PIXELS);

        this.request = request;
        binder = new Binder<>();
        binder.setBean(request);
        setDialogContent(buildForm());
    }

    private Component buildForm() {
        FormLayout form = new FormLayout();
        form.setWidthUndefined();

        TextField code = new TextField("Country code");
        code.setWidth(100, Unit.PERCENTAGE);
        binder.forField(code)
            .asRequired()
            .withValidator(new StringLengthValidator("Not valid", 1, 3))
            .bind(PhoneContactRequest::getCountryCode, PhoneContactRequest::setCountryCode);
        code.setValue("34");
        code.setMaxLength(3);
        form.addComponent(code);

        TextField number = new TextField("Number");
        number.setWidth(100, Unit.PERCENTAGE);
        binder.forField(number)
            .asRequired()
            .withValidator(new StringLengthValidator("Not valid", 9, 14))
            .bind(PhoneContactRequest::getPhoneNumber, PhoneContactRequest::setPhoneNumber);
        form.addComponent(number);

        CheckBox primary = new CheckBox("Primary");
        binder.forField(primary)
            .bind(PhoneContactRequest::isPrimary, PhoneContactRequest::setPrimary);
        form.addComponent(primary);

        ComboBox<String> type = new ComboBox<>("Phone type");
        type.setWidth(100, Unit.PERCENTAGE);
        type.setItems(PHONE_TYPE);
        binder.forField(type)
            .bind(PhoneContactRequest::getType, PhoneContactRequest::setType);
        type.setEmptySelectionAllowed(false);
        form.addComponent(type);

        ComboBox<String> source = new ComboBox<>("Source");
        source.setWidth(100, Unit.PERCENTAGE);
        source.setItems(PHONE_SOURCE);
        source.setEmptySelectionAllowed(false);
        binder.forField(source)
            .bind(PhoneContactRequest::getSource, PhoneContactRequest::setSource);
        form.addComponent(source);

        return form;
    }

    protected abstract Call<?> getCall();

    @Override
    @SneakyThrows
    protected void executeAction() {
        BinderValidationStatus<PhoneContactRequest> result = binder.validate();
        if (result.isOk()) {
            BackgroundOperations.callApi("Saving phone", getCall(), t -> {
                Notifications.trayNotification("Phone saved");
                close();
                callback.run();
            }, Notifications::errorNotification);
        } else {
            result.notifyBindingValidationStatusHandlers();
        }
    }

    private static PhoneContactRequest toRequest(PhoneContactRecord record) {
        return new PhoneContactRequest()
            .setPhoneContactId(record.getId())
            .setClientId(record.getClientId())
            .setActiveTill(record.getActiveTill())
            .setSource(record.getSource())
            .setType(record.getPhoneType())
            .setCountryCode(record.getCountryCode())
            .setPhoneNumber(record.getLocalNumber())
            .setPrimary(record.getIsPrimary())
            .setLegalConsent(record.getLegalConsent());
    }

    private static class CreatePhoneContactDialog extends PhoneContactDialog {

        public CreatePhoneContactDialog(PhoneContactRequest request, PhoneContactApiClient phoneContactApiClient, Runnable callback) {
            super(request, phoneContactApiClient, callback);
        }

        @Override
        protected Call<?> getCall() {
            return api.create(request);
        }
    }

    private static class UpdatePhoneContactDialog extends PhoneContactDialog {

        protected UpdatePhoneContactDialog(PhoneContactRequest request, PhoneContactApiClient phoneContactApiClient, Runnable callback) {
            super(request, phoneContactApiClient, callback);
        }

        @Override
        protected Call<?> getCall() {
            return api.update(request.getPhoneContactId(), request);
        }


    }
}
