package fintech.bo.spain.alfa.client;

import com.vaadin.data.Binder;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.TimeMachine;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.spain.alfa.bo.model.UpdateClientDataRequest;

public class EditClientDataDialog extends ActionDialog {

    private final ClientDTO client;

    private final AlfaApiClient clientApi;

    private Binder<UpdateClientDataRequest> binder = new Binder<>();;

    public EditClientDataDialog(ClientDTO client, AlfaApiClient clientApi) {
        super("Edit client", "Save");

        this.client = client;
        this.clientApi = clientApi;

        setDialogContent(form());
        setWidth(600, Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        if (binder.validate().isOk()) {
            BackgroundOperations.callApi("Updating data", clientApi.updateClientData(binder.getBean()), t -> {
                Notifications.trayNotification("Updated");
                close();
            }, Notifications::errorNotification);
        }
    }

    private Component form() {
        UpdateClientDataRequest clientData = new UpdateClientDataRequest();
        clientData.setClientId(client.getId());
        clientData.setFirstName(client.getFirstName());
        clientData.setLastName(client.getLastName());
        clientData.setSecondLastName(client.getSecondLastName());
        clientData.setDateOfBirth(client.getDateOfBirth());
        clientData.setGender(client.getGender());
        clientData.setEmail(client.getEmail());
        clientData.setPhone(client.getPhone());
        clientData.setAdditionalPhone(client.getAdditionalPhone());
        clientData.setAccountNumber(client.getAccountNumber());
        clientData.setBlockCommunication(client.getBlockCommunication());
        clientData.setExcludedFromASNEF(client.getExcludedFromAsnef());
        binder.setBean(clientData);

        FormLayout form = new FormLayout();
        form.setMargin(true);

        TextField firstName = new TextField("First name");
        firstName.setWidth(100, Unit.PERCENTAGE);
        binder.forField(firstName)
            .asRequired()
            .bind(UpdateClientDataRequest::getFirstName, UpdateClientDataRequest::setFirstName);
        form.addComponent(firstName);

        TextField lastName = new TextField("Last name");
        lastName.setWidth(100, Unit.PERCENTAGE);
        binder.forField(lastName)
            .asRequired()
            .bind(UpdateClientDataRequest::getLastName, UpdateClientDataRequest::setLastName);
        form.addComponent(lastName);

        TextField secondLastName = new TextField("Second last name");
        secondLastName.setWidth(100, Unit.PERCENTAGE);
        binder.forField(secondLastName)
            .bind(UpdateClientDataRequest::getSecondLastName, UpdateClientDataRequest::setSecondLastName);
        form.addComponent(secondLastName);

        DateField dateOfBirth = new DateField("Date of birth");
        dateOfBirth.setWidth(100, Unit.PERCENTAGE);
        dateOfBirth.setDateFormat(Formats.DATE_FORMAT);
        dateOfBirth.setRangeEnd(TimeMachine.today());
        binder.forField(dateOfBirth)
            .asRequired()
            .bind(UpdateClientDataRequest::getDateOfBirth, UpdateClientDataRequest::setDateOfBirth);
        form.addComponent(dateOfBirth);

        ComboBox<String> gender = new ComboBox<>("Gender");
        gender.setWidth(100, Unit.PERCENTAGE);
        gender.setItems("MALE", "FEMALE");
        gender.setTextInputAllowed(false);
        gender.setEmptySelectionAllowed(false);
        binder.forField(gender)
            .asRequired()
            .bind(UpdateClientDataRequest::getGender, UpdateClientDataRequest::setGender);

        form.addComponent(gender);

        TextField email = new TextField("Email");
        email.setWidth(100, Unit.PERCENTAGE);
        binder.forField(email)
            .asRequired()
            .bind(UpdateClientDataRequest::getEmail, UpdateClientDataRequest::setEmail);
        form.addComponent(email);

        TextField phone = new TextField("Phone");
        phone.setWidth(100, Unit.PERCENTAGE);
        binder.forField(phone)
            .asRequired()
            .bind(UpdateClientDataRequest::getPhone, UpdateClientDataRequest::setPhone);
        form.addComponent(phone);

        TextField additionalPhone = new TextField("Additional phone");
        additionalPhone.setWidth(100, Unit.PERCENTAGE);
        binder.forField(additionalPhone)
            .bind(UpdateClientDataRequest::getAdditionalPhone, UpdateClientDataRequest::setAdditionalPhone);
        form.addComponent(additionalPhone);

        TextField accountNumber = new TextField("Account number");
        accountNumber.setWidth(100, Unit.PERCENTAGE);
        binder.forField(accountNumber)
            .bind(UpdateClientDataRequest::getAccountNumber, UpdateClientDataRequest::setAccountNumber);
        form.addComponent(accountNumber);

        CheckBox blockCommunication = new CheckBox("Block communication");
        binder.forField(blockCommunication)
            .bind(UpdateClientDataRequest::isBlockCommunication, UpdateClientDataRequest::setBlockCommunication);
        form.addComponent(blockCommunication);

        CheckBox excludedFromASNEF = new CheckBox("Exclude this client from ASNEF");
        binder.forField(excludedFromASNEF)
            .bind(UpdateClientDataRequest::isExcludedFromASNEF, UpdateClientDataRequest::setExcludedFromASNEF);
        form.addComponent(excludedFromASNEF);

        return form;
    }
}
