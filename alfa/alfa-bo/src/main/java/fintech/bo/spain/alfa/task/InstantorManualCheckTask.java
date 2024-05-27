package fintech.bo.spain.alfa.task;

import com.google.common.base.MoreObjects;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.attachments.UploadAttachmentDialog;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.instantor.InstantorQueries;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.db.jooq.crm.tables.records.ClientAttachmentRecord;
import fintech.bo.db.jooq.instantor.tables.records.ResponseRecord;
import fintech.bo.db.jooq.task.tables.records.TaskAttributeRecord;
import fintech.bo.spain.alfa.AlfaBoConstants;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import fintech.bo.spain.alfa.client.EditClientDataDialog;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static fintech.bo.db.jooq.instantor.Tables.RESPONSE;

public class InstantorManualCheckTask extends CommonTaskView {

    private final ClientRepository clientRepository;
    private ResponseRecord instantorRecord;
    private ClientDTO client;
    private final InstantorQueries instantorQueries;
    private final AlfaApiClient alfaApiClient;

    public InstantorManualCheckTask() {
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
        this.instantorQueries = ApiAccessor.gI().get(InstantorQueries.class);
        this.alfaApiClient = ApiAccessor.gI().get(AlfaApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        Optional<TaskAttributeRecord> instantorResponseId = getHelper().getTaskQueries().findAttributeByKey(getTask().getId(), "InstantorResponseId");
        Optional<ResponseRecord> instantorRecordMaybe = instantorResponseId.map(id -> getHelper().getDb().selectFrom(RESPONSE).where(RESPONSE.ID.eq(Long.valueOf(id.getValue()))).fetchOne());
        Validate.isTrue(instantorRecordMaybe.isPresent(), "Instantor response not found");
        this.instantorRecord = instantorRecordMaybe.get();
        this.client = clientRepository.getRequired(getTask().getClientId());

        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(instantorComponent());
        layout.addComponent(uploadedFiles());
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));

        customActions(baseLayout);
        baseLayout.addTab("Instantor response", instantorTab());

        return layout;
    }

    private void customActions(BusinessObjectLayout baseTaskView) {
        baseTaskView.addActionMenuItem("Edit personal data", (event) -> {
            ClientDTO client = clientRepository.getRequired(getTask().getClientId());
            EditClientDataDialog dialog = new EditClientDataDialog(client, alfaApiClient);
            dialog.addCloseListener((e) -> baseTaskView.refresh());
            UI.getCurrent().addWindow(dialog);
        });
        baseTaskView.addActionMenuItem("Upload file", (event) -> {
            UploadAttachmentDialog dialog = getHelper().getAttachmentsComponents().uploadAttachmentDialog("Upload file", this.getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_OTHER);
            dialog.addCloseListener((e) -> baseTaskView.refresh());
            UI.getCurrent().addWindow(dialog);
        });
    }

    private Component uploadedFiles() {
        List<ClientAttachmentRecord> other = getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_OTHER);
        List<ClientAttachmentRecord> clientUpload = getHelper().getAttachmentQueries().findAttachmentsByType(getTask().getClientId(), AlfaBoConstants.ATTACHMENT_TYPE_CLIENT_UPLOAD);
        List<ClientAttachmentRecord> attachments = new ArrayList<>();
        attachments.addAll(clientUpload);
        attachments.addAll(other);
        if (attachments.isEmpty()) {
            return new Label();
        }
        PropertyLayout layout = new PropertyLayout("Uploads");
        for (ClientAttachmentRecord record : attachments) {
            layout.add("File", getHelper().getAttachmentsComponents().generateViewLink(record));
        }
        layout.setMargin(new MarginInfo(true, false, false, false));
        return layout;
    }

    private Supplier<Component> instantorTab() {
        return () -> {
            TextArea textArea = new TextArea();
            textArea.setSizeFull();
            textArea.setReadOnly(true);
            textArea.addStyleName(BackofficeTheme.TEXT_MONO);
            textArea.setWordWrap(false);

            String payloadJson = MoreObjects.firstNonNull(instantorRecord.getPayloadJson(), "");
            if ("OK".equals(instantorRecord.getStatus())) {
                payloadJson = JsonUtils.formatJson(payloadJson);
            }
            textArea.setValue(payloadJson);
            return textArea;
        };
    }

    private Component instantorComponent() {
        PropertyLayout layout = new PropertyLayout("Instantor");
        layout.add("Full Name", instantorRecord.getNameForVerification());

        Label documentNumber = new Label(MoreObjects.firstNonNull(instantorRecord.getPersonalNumberForVerification(), "-"));
        documentNumber.addStyleName(ValoTheme.LABEL_BOLD);
        if (StringUtils.equalsIgnoreCase(instantorRecord.getPersonalNumberForVerification(), client.getDocumentNumber())) {
            documentNumber.addStyleName(BackofficeTheme.TEXT_SUCCESS);
        } else {
            documentNumber.addStyleName(BackofficeTheme.TEXT_DANGER);
        }
        layout.add("Document Number", documentNumber);
        layout.add("Account Holder Name", instantorQueries.findInstantorResponseAccountHolderName(instantorRecord.getClientId(), client.getAccountNumber()));
        layout.setMargin(new MarginInfo(true, false, false, false));
        return layout;
    }

}
