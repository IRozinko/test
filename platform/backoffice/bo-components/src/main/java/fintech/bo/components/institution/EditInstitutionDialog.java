package fintech.bo.components.institution;


import com.vaadin.data.Binder;
import com.vaadin.ui.*;
import fintech.bo.api.client.PaymentApiClient;
import fintech.bo.api.model.institution.UpdateInstitutionRequest;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import org.jooq.Record;

import static fintech.bo.db.jooq.payment.tables.Institution.INSTITUTION;


public class EditInstitutionDialog extends ActionDialog {

    private final Record record;

    private final PaymentApiClient paymentApiClient;

    private Binder<UpdateInstitutionRequest> binder;


    public EditInstitutionDialog(Record record, PaymentApiClient paymentApiClient) {
        super("Edit institution", "Save");

        this.record = record;
        this.paymentApiClient = paymentApiClient;

        setDialogContent(form());
        setWidth(600, Unit.PIXELS);
    }

    @Override
    protected void executeAction() {
        BackgroundOperations.callApi("Updating data", paymentApiClient.updateInstitution(binder.getBean()), t -> {
            Notifications.trayNotification("Updated");
            close();
        }, Notifications::errorNotification);
    }

    private Component form() {
        FormLayout form = new FormLayout();
        form.setMargin(true);


        TextField name = new TextField("Name");
        name.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(name);

        TextField statementImportFormat = new TextField("Statement import format");
        statementImportFormat.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(statementImportFormat);

        TextField statementExportFormat = new TextField("Statement export format");
        statementExportFormat.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(statementExportFormat);

        TextArea statementExportParamsJson = new TextArea("Statement export params json");
        statementExportParamsJson.setWidth(100, Unit.PERCENTAGE);
        form.addComponent(statementExportParamsJson);

        CheckBox primary = new CheckBox("Primary");
        form.addComponent(primary);

        CheckBox disabled = new CheckBox("Disabled");
        form.addComponent(disabled);

        UpdateInstitutionRequest updateRequest = new UpdateInstitutionRequest();
        updateRequest.setInstitutionId(record.get(INSTITUTION.ID));
        updateRequest.setName(record.get(INSTITUTION.NAME));
        updateRequest.setPrimary(record.get(INSTITUTION.IS_PRIMARY));
        updateRequest.setDisabled(record.get(INSTITUTION.DISABLED));
        updateRequest.setStatementImportFormat(record.get(INSTITUTION.STATEMENT_IMPORT_FORMAT));
        updateRequest.setStatementExportFormat(record.get(INSTITUTION.STATEMENT_EXPORT_FORMAT));
        updateRequest.setStatementExportParamsJson(record.get(INSTITUTION.STATEMENT_EXPORT_PARAMS_JSON));


        binder = new Binder<>(UpdateInstitutionRequest.class);
        binder.setBean(updateRequest);
        binder.bind(name, "name");
        binder.bind(primary, "primary");
        binder.bind(disabled, "disabled");
        binder.bind(statementImportFormat, "statementImportFormat");
        binder.bind(statementExportFormat, "statementExportFormat");
        binder.bind(statementExportParamsJson, "statementExportParamsJson");

        return form;
    }

}
