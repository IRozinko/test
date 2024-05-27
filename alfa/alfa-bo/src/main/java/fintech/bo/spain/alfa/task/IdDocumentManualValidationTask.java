package fintech.bo.spain.alfa.task;

import com.google.common.base.Throwables;
import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.DownloadCloudFileRequest;
import fintech.bo.components.DateUtils;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.attachments.AttachmentDataProvider;
import fintech.bo.components.attachments.AttachmentsComponents;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.db.jooq.alfa.tables.records.IdentificationDocumentRecord;
import fintech.bo.db.jooq.workflow.Tables;
import fintech.bo.spain.alfa.attachments.IdentificationDocumentDialog;
import fintech.bo.spain.alfa.attachments.NewIdentificationDocumentForm;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.jooq.Record;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static fintech.bo.db.jooq.crm.tables.ClientAttachment.CLIENT_ATTACHMENT;
import static fintech.bo.db.jooq.alfa.Tables.IDENTIFICATION_DOCUMENT;

public class IdDocumentManualValidationTask  extends CommonTaskView {

    private final ClientRepository clientRepository;
    private final AttachmentsComponents attachmentsComponents;
    private final FileApiClient fileApiClient;
    private Component idDocsTab;

    public IdDocumentManualValidationTask() {
        this.attachmentsComponents = ApiAccessor.gI().get(AttachmentsComponents.class);
        this.fileApiClient = ApiAccessor.gI().get(FileApiClient.class);
        this.clientRepository = ApiAccessor.gI().get(ClientRepository.class);
    }

    public PropertyLayout callClientComponent(Long clientId) {
        ClientDTO client = clientRepository.getRequired(clientId);
        PropertyLayout layout = new PropertyLayout("Call client");
        layout.add("Client number", client.getClientNumber());
        layout.add("First name", client.getFirstName());
        layout.add("Last name", client.getLastName());
        layout.add("Second last name", client.getSecondLastName());
        layout.add("Email", client.getEmail());
        layout.add("Mobile phone", client.getPhone());
        layout.add("Document Number", client.getDocumentNumber());
        layout.add("Date of birth", client.getDateOfBirth());
        layout.add("Gender", client.getGender());
        layout.add("Age", DateUtils.yearsFromNow(client.getDateOfBirth()));
        layout.setMargin(false);
        return layout;
    }


    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();

        DSLContext db = ApiAccessor.gI().get(DSLContext.class);
        db.selectFrom(Tables.ACTIVITY)
            .where(Tables.ACTIVITY.WORKFLOW_ID.eq(getTask().getWorkflowId()))
            .and(Tables.ACTIVITY.NAME.eq("DecisionEngIdValidation"))
            .fetchOptional()
            .ifPresent(activity -> {
                Label label = new Label(activity.getResolutionDetail());
                label.addStyleName(ValoTheme.LABEL_FAILURE);
                layout.addComponent(label);
            });

        layout.addComponent(callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));


        List<IdentificationDocumentRecord> taskDocuments = db.selectFrom(IDENTIFICATION_DOCUMENT)
            .where(IDENTIFICATION_DOCUMENT.TASK_ID.eq(getTask().getId()))
            .fetch();

        idDocsTab = idDocsTab(getTask().getAgent(), getTask().getClientId(), getTask().getId(), taskDocuments, baseLayout);
        baseLayout.addTab("ID Docs", () -> idDocsTab);
        baseLayout.addTab("Attachments", () -> attachmentsTab(getTask().getClientId()));

        return layout;
    }

    private Component idDocsTab(String taskAgent, Long clientId, Long taskId, List<IdentificationDocumentRecord> documents, BusinessObjectLayout baseTaskView) {
        VerticalLayout verticalLayout = new VerticalLayout();

        if (!documents.isEmpty()) {
            PropertyLayout docsLayout = new PropertyLayout("ID Docs");
            docsLayout.setMargin(false);
            documents.forEach(d -> {
                Button view = new Button("View");
                view.addStyleName(ValoTheme.BUTTON_SMALL);
                view.addClickListener(c -> new IdentificationDocumentDialog(d).view());
                docsLayout.add(d.getDocumentNumber(), view);
            });
            verticalLayout.addComponent(docsLayout);
        }

        NewIdentificationDocumentForm identificationDocumentForm = new NewIdentificationDocumentForm(new BoComponentContext()
            .withScope(StandardScopes.SCOPE_CLIENT, clientId)
            .withScope(StandardScopes.SCOPE_TASK, taskId)
            .withFeature(StandardFeatures.FEATURE_HORIZONTAL_VIEW)
            .withFeature(StandardFeatures.FEATURE_ACTIONABLE));

        identificationDocumentForm.setSuccessCallback(d -> baseTaskView.refresh());

        verticalLayout.addComponent(identificationDocumentForm);

        VerticalSplitPanel splitPanel = new VerticalSplitPanel();

        Grid<Record> grid = attachmentsTab(clientId);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(e -> {
            Optional<Record> selectedItem = e.getFirstSelectedItem();
            if (selectedItem.isPresent()) {

                Record record = selectedItem.get();
                Long fileId = record.get(CLIENT_ATTACHMENT.FILE_ID);
                String fileName = record.get(CLIENT_ATTACHMENT.NAME);

                StreamResource resource = new StreamResource(() -> {
                    DownloadCloudFileRequest fileDownloadRequest = new DownloadCloudFileRequest();
                    fileDownloadRequest.setFileId(fileId);

                    try {
                        Response<ResponseBody> fileResponse = fileApiClient.download(fileDownloadRequest).execute();
                        if (fileResponse.isSuccessful()) {
                            return new ByteArrayInputStream(fileResponse.body().bytes());
                        } else {
                            return null;
                        }
                    } catch (IOException e1) {
                        Throwables.propagate(e1);
                    }

                    return null;
                }, fileName);

                BrowserFrame browserFrame = new BrowserFrame("", resource);
                browserFrame.setSizeFull();

                splitPanel.setSecondComponent(browserFrame);
            } else {
                splitPanel.setSecondComponent(new VerticalLayout());
            }
        });
        splitPanel.setFirstComponent(grid);
        splitPanel.setSplitPosition(20, Sizeable.Unit.PERCENTAGE);

        verticalLayout.addComponentsAndExpand(splitPanel);

        boolean assignedToMe = StringUtils.equalsIgnoreCase(taskAgent, LoginService.getLoginData().getUser());
        verticalLayout.setEnabled(assignedToMe);

        return verticalLayout;
    }


    private Grid<Record> attachmentsTab(Long clientId) {
        AttachmentDataProvider dataProvider = attachmentsComponents.attachmentDataProvider();
        dataProvider.setClientId(clientId);
        return attachmentsComponents.attachmentGrid(dataProvider);
    }
}
