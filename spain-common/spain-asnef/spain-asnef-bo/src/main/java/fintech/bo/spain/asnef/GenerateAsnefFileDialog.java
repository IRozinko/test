package fintech.bo.spain.asnef;

import com.vaadin.data.Binder;
import com.vaadin.data.converter.StringToLongConverter;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import fintech.TimeMachine;
import fintech.bo.components.Formats;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.spain.asnef.api.AsnefApiClient;
import fintech.bo.spain.asnef.model.GenerateAsnefFileRequest;

public class GenerateAsnefFileDialog extends ActionDialog {

    private final AsnefApiClient client;

    private final GenerateAsnefFileRequest request;

    public GenerateAsnefFileDialog(AsnefApiClient client) {
        super("Generate asnef file", "Generate");

        this.client = client;
        this.request = new GenerateAsnefFileRequest();

        setDialogContent(build());
        setWidth(600, Unit.PIXELS);
    }

    private Component build() {
        Binder<GenerateAsnefFileRequest> binder = new Binder<>(GenerateAsnefFileRequest.class);
        binder.setBean(request);

        ComboBox<String> type = new ComboBox<>("Select type");
        type.setItems(AsnefComponents.LOG_TYPE_NOTIFICA_RP, AsnefComponents.LOG_TYPE_FOTOALTAS);
        type.setTextInputAllowed(false);
        type.setEmptySelectionAllowed(false);
        type.setRequiredIndicatorVisible(true);
        type.setWidth(100, Unit.PERCENTAGE);
        binder.bind(type, GenerateAsnefFileRequest::getType, GenerateAsnefFileRequest::setType);

        DateField batchDate = new DateField("Select batch date");
        batchDate.setDateFormat(Formats.DATE_FORMAT);
        batchDate.setRequiredIndicatorVisible(true);
        batchDate.setRangeEnd(TimeMachine.today());
        batchDate.setWidth(100, Unit.PERCENTAGE);
        binder.bind(batchDate, GenerateAsnefFileRequest::getBatchDate, GenerateAsnefFileRequest::setBatchDate);

        TextField limit = new TextField("Enter limit (blank if not applicable)");
        limit.setWidth(100, Unit.PERCENTAGE);
        binder.forField(limit).withNullRepresentation("").withConverter(new StringToLongConverter("Wrong format")).bind(GenerateAsnefFileRequest::getLimit, GenerateAsnefFileRequest::setLimit);

        FormLayout formLayout = new FormLayout();
        formLayout.addComponents(type, batchDate, limit);
        return formLayout;
    }

    @Override
    protected void executeAction() {
        if (request.getType() == null) {
            Notifications.errorNotification("Type not selected");
            return;
        }

        if (request.getBatchDate() == null) {
            Notifications.errorNotification("Batch date not selected");
            return;
        }

        BackgroundOperations.callApi("Generating asnef file", client.generateAsnefFile(request), r -> {
            Notifications.trayNotification("Asnef file generated");

            close();
        }, Notifications::errorNotification);
    }
}
