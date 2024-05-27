package fintech.bo.spain.alfa.task;

import com.vaadin.server.Sizeable;
import com.vaadin.server.StreamResource;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Component;
import com.vaadin.ui.Grid;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.DownloadCloudFileRequest;
import fintech.bo.components.api.ApiAccessor;
import fintech.bo.components.attachments.AttachmentDataProvider;
import fintech.bo.components.attachments.AttachmentsComponents;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.task.CommonTaskView;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardFeatures;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.spain.alfa.attachments.NewIdentificationDocumentForm;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import retrofit2.Response;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import static fintech.bo.db.jooq.crm.tables.ClientAttachment.CLIENT_ATTACHMENT;

public class IdDocumentManualTextExtractionTask extends CommonTaskView {

    private final AttachmentsComponents attachmentsComponents;
    private final FileApiClient fileApiClient;
    private Component idDocsTab;

    public IdDocumentManualTextExtractionTask() {
        this.attachmentsComponents = ApiAccessor.gI().get(AttachmentsComponents.class);
        this.fileApiClient = ApiAccessor.gI().get(FileApiClient.class);
    }

    @Override
    public Component buildView(BusinessObjectLayout baseLayout) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(getHelper().callClientComponent(getTask().getClientId()));
        layout.addComponent(getHelper().completeTaskComponent(getTask(), resolutions()));

        idDocsTab = idDocsTab(getTask().getAgent(), getTask().getClientId());
        baseLayout.addTab("ID Docs", () -> idDocsTab);
        baseLayout.addTab("Attachments", () -> attachmentsTab(getTask().getClientId()));

        return layout;
    }

    private Component idDocsTab(String taskAgent, Long clientId) {
        VerticalLayout verticalLayout = new VerticalLayout();

        NewIdentificationDocumentForm identificationDocumentForm = new NewIdentificationDocumentForm(new BoComponentContext()
            .withScope(StandardScopes.SCOPE_CLIENT, clientId)
            .withFeature(StandardFeatures.FEATURE_HORIZONTAL_VIEW)
            .withFeature(StandardFeatures.FEATURE_ACTIONABLE));

        verticalLayout.addComponent(identificationDocumentForm);

        VerticalSplitPanel splitPanel = new VerticalSplitPanel();

        Grid<Record> grid = attachmentsTab(clientId);
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        grid.addSelectionListener(e -> {
            Optional<Record> selectedItem = e.getFirstSelectedItem();
            if (selectedItem.isPresent()) {
                showPreview(splitPanel, selectedItem.get());
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

    private void showPreview(VerticalSplitPanel splitPanel, Record record) {
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
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, fileName);

        BrowserFrame browserFrame = new BrowserFrame("", resource);
        browserFrame.setSizeFull();

        splitPanel.setSecondComponent(browserFrame);
    }

    private Grid<Record> attachmentsTab(Long clientId) {
        AttachmentDataProvider dataProvider = attachmentsComponents.attachmentDataProvider();
        dataProvider.setClientId(clientId);
        return attachmentsComponents.attachmentGrid(dataProvider);
    }
}
