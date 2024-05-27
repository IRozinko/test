package fintech.bo.spain.alfa.task;

import com.vaadin.data.Binder;
import com.vaadin.server.Sizeable;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.attachments.UploadAttachmentDialog;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.spain.alfa.AlfaBoConstants;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.client.EditClientDataDialog;
import fintech.spain.alfa.bo.model.DocumentCheckUpdateRequest;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Collection;
import java.util.stream.Stream;

public class DocumentCheckTask  extends CommonTaskView {

    private ClientDTO client;
    private BusinessObjectLayout baseLayout;
    private final AlfaApiClient alfaApiClient;
    private final ClientRepository clientRepository;

    public DocumentCheckTask() {
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
        this.alfaApiClient = ApiAccessor.gI().get(AlfaApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        this.client = clientRepository.getRequired(getTask().getClientId());
        this.baseLayout = baseLayout;
        baseLayout.setSplitPosition(600);
        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(uploadedFiles());
        layout.addComponents(clientUpdate());
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));
        customActions();
        return layout;
    }

    private void customActions() {
        baseLayout.addActionMenuItem("Edit personal data", (event) -> {
            ClientDTO client = clientRepository.getRequired(getTask().getClientId());
            EditClientDataDialog dialog = new EditClientDataDialog(client, alfaApiClient);
            dialog.addCloseListener((e) -> baseLayout.refresh());
            UI.getCurrent().addWindow(dialog);
        });
        baseLayout.addActionMenuItem("Upload file", (event) -> {
            UploadAttachmentDialog dialog = getHelper().getAttachmentsComponents().uploadAttachmentDialog("Upload file", getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_OTHER);
            dialog.addCloseListener((e) -> baseLayout.refresh());
            UI.getCurrent().addWindow(dialog);
        });
    }

    public Component clientUpdate() {
        FormLayout form = new FormLayout();
        form.setMargin(false);

        Label titleLabel = new Label(WordUtils.capitalizeFully("Update client data"));
        titleLabel.addStyleName(ValoTheme.LABEL_H4);
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        titleLabel.addStyleName(ValoTheme.LABEL_BOLD);
        titleLabel.addStyleName(ValoTheme.LABEL_COLORED);
        form.addComponent(titleLabel);

        TextField firstName = new TextField("First name");
        firstName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(firstName);

        TextField lastName = new TextField("Last name");
        lastName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(lastName);

        TextField secondLastName = new TextField("Second last name");
        secondLastName.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(secondLastName);

        DateField dateOfBirth = new DateField("Date of birth");
        dateOfBirth.setWidth(100, Sizeable.Unit.PERCENTAGE);
        dateOfBirth.setDateFormat(Formats.DATE_FORMAT);
        form.addComponent(dateOfBirth);

        TextField documentNumber = new TextField("Document number");
        documentNumber.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(documentNumber);

        TextField accountNumber = new TextField("Account number");
        accountNumber.setWidth(100, Sizeable.Unit.PERCENTAGE);
        form.addComponent(accountNumber);

        DocumentCheckUpdateRequest request = new DocumentCheckUpdateRequest();
        request.setTaskId(getTask().getId());
        request.setFirstName(client.getFirstName());
        request.setLastName(client.getLastName());
        request.setSecondLastName(client.getSecondLastName());
        request.setDateOfBirth(client.getDateOfBirth());
        request.setAccountNumber(client.getAccountNumber());
        request.setDocumentNumber(client.getDocumentNumber());

        Binder<DocumentCheckUpdateRequest> binder = new Binder<>(DocumentCheckUpdateRequest.class);
        binder.setBean(request);
        binder.bind(firstName, "firstName");
        binder.bind(lastName, "lastName");
        binder.bind(secondLastName, "secondLastName");
        binder.bind(dateOfBirth, "dateOfBirth");
        binder.bind(accountNumber, "accountNumber");
        binder.bind(documentNumber, "documentNumber");

        Button save = new Button("Save");
        save.addStyleName(ValoTheme.BUTTON_PRIMARY);
        save.addClickListener(e -> {
            saveClientData(request);
        });
        form.addComponent(save);

        return form;
    }

    private void saveClientData(DocumentCheckUpdateRequest request) {
        BackgroundOperations.callApi("Updating client", alfaApiClient.documentCheckUpdate(request), t -> {
            Notifications.trayNotification("Client updated");
            baseLayout.refresh();
        }, Notifications::errorNotification);
    }

    private Component uploadedFiles() {
        PropertyLayout layout = new PropertyLayout("Uploaded files");
        Stream.of(
            getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_OTHER),
            getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_CLIENT_UPLOAD),
            getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_ID_DOCUMENT),
            getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_BANK_ACC_OWNERSHIP)
        )
            .flatMap(Collection::stream)
            .forEach(record -> {
                    layout.add("File", getHelper().getAttachmentsComponents().generateViewLink(record));
                }
            );

        layout.setMargin(new MarginInfo(true, false, false, false));
        return layout;
    }
}
