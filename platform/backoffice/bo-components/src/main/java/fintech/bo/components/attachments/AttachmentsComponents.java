package fintech.bo.components.attachments;


import com.vaadin.ui.Grid;
import com.vaadin.ui.Link;
import fintech.bo.api.client.AttachmentApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.utils.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static fintech.bo.db.jooq.crm.Tables.CLIENT_ATTACHMENT;

@Component
@Slf4j
public class AttachmentsComponents {

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private AttachmentApiClient attachmentApiClient;

    @Autowired
    private DSLContext db;

    public AttachmentDataProvider attachmentDataProvider() {
        return new AttachmentDataProvider(db);
    }

    public Grid<Record> attachmentGrid(AttachmentDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addColumn(CLIENT_ATTACHMENT.ATTACHMENT_TYPE);
        builder.addComponentColumn(this::generateViewLink).setWidth(300);
        builder.addColumn(CLIENT_ATTACHMENT.STATUS);
        builder.addAuditColumns(CLIENT_ATTACHMENT);
        builder.addColumn(CLIENT_ATTACHMENT.ID);
        builder.sortDesc(CLIENT_ATTACHMENT.CREATED_AT);
        return builder.build(dataProvider);
    }

    public Link generateViewLink(Record record) {
        CloudFile cloudFile = new CloudFile(record.get(CLIENT_ATTACHMENT.FILE_ID), record.get(CLIENT_ATTACHMENT.NAME));
        return UrlUtils.generateViewLink(fileApiClient, cloudFile);
    }

    public UploadAttachmentDialog uploadAttachmentDialog(String caption, Long clientId, String attachmentType) {
        UploadAttachmentDialog dialog = new UploadAttachmentDialog(caption, clientId, attachmentType, fileApiClient, attachmentApiClient);
        return dialog;
    }
}
