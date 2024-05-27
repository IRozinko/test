package fintech.bo.spain.alfa.attachments;

import com.vaadin.ui.*;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.common.Tab;
import fintech.bo.components.security.SecuredTab;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.alfa.tables.records.IdentificationDocumentRecord;
import fintech.bo.spain.alfa.api.AlfaApiClient;
import org.jooq.DSLContext;

import java.util.Optional;

import static fintech.bo.db.jooq.alfa.Tables.IDENTIFICATION_DOCUMENT;

@SecuredTab(permissions = BackofficePermissions.ADMIN, condition = "#client.deleted == true")
public class AlfaAttachmentTab extends Tab {

    private final DSLContext db;
    private final FileApiClient fileApiClient;
    private final AlfaApiClient alfaApiClient;

    public AlfaAttachmentTab(String caption, ClientDTO client, DSLContext db, FileApiClient fileApiClient, AlfaApiClient alfaApiClient) {
        super(caption, client);
        this.db = db;
        this.fileApiClient = fileApiClient;
        this.alfaApiClient = alfaApiClient;
    }

    private Grid<IdentificationDocumentRecord> grid(IdentificationDocumentDataProvider dataProvider) {
        JooqGridBuilder<IdentificationDocumentRecord> builder = new JooqGridBuilder<>();
        gridColumns(builder);
        return builder.build(dataProvider);
    }

    private void gridColumns(JooqGridBuilder<IdentificationDocumentRecord> builder) {

        builder.addActionColumn("View", this::view);
        builder.addColumn(IDENTIFICATION_DOCUMENT.DOCUMENT_TYPE);
        builder.addColumn(IDENTIFICATION_DOCUMENT.DOCUMENT_NUMBER);
        builder.addComponentColumn(idFile -> generateViewLink(idFile.getFrontFileId(), idFile.getFrontFileName())).setWidth(300);
        builder.addComponentColumn(idFile -> generateViewLink(idFile.getBackFileId(), idFile.getBackFileName())).setWidth(300);
        builder.addAuditColumns(IDENTIFICATION_DOCUMENT);
        builder.addColumn(IDENTIFICATION_DOCUMENT.ID);
        builder.sortDesc(IDENTIFICATION_DOCUMENT.ID);
    }

    private void view(IdentificationDocumentRecord record) {
        new IdentificationDocumentDialog(record).view();
    }

    @Override
    public Component build() {
        IdentificationDocumentDataProvider dataProvider = new IdentificationDocumentDataProvider(db);
        dataProvider.setClientId(client.getId());

        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout buttonRow = new HorizontalLayout();

        Button addButton = new Button("New Document");
        addButton.addClickListener(c -> {
            NewIdentificationDocumentDialog dialog = new NewIdentificationDocumentDialog("New Identification Document", client.getId());
            dialog.addCloseListener(e -> dataProvider.refreshAll());
            UI.getCurrent().addWindow(dialog);
        });
        buttonRow.addComponents(addButton);

        layout.addComponent(buttonRow);
        layout.addComponentsAndExpand(grid(dataProvider));
        layout.setMargin(false);
        return layout;
    }

    private com.vaadin.ui.Component generateViewLink(Long fileId, String filename) {
        return Optional.ofNullable(fileId).map(f -> UrlUtils.generateViewLink(fileApiClient, new CloudFile(f, filename))).orElse(null);
    }
}
