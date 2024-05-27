package fintech.bo.components.attachments;

import com.vaadin.data.provider.Query;
import fintech.bo.components.JooqDataProvider;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectWhereStep;

import static fintech.bo.db.jooq.crm.tables.ClientAttachment.CLIENT_ATTACHMENT;
import static fintech.bo.db.jooq.storage.tables.CloudFile.CLOUD_FILE;
import static java.util.Arrays.asList;


@Slf4j
public class AttachmentDataProvider extends JooqDataProvider<Record> {

    private Long clientId;
    private String attachmentType;

    public AttachmentDataProvider(DSLContext db) {
        super(db);
    }

    @Override
    protected SelectWhereStep<Record> buildSelect(Query<Record, String> query) {
        SelectWhereStep<Record> select = db
            .select(asList(
                CLIENT_ATTACHMENT.ID,
                CLIENT_ATTACHMENT.FILE_ID,
                CLIENT_ATTACHMENT.CLIENT_ID,
                CLIENT_ATTACHMENT.ATTACHMENT_TYPE,
                CLIENT_ATTACHMENT.ATTACHMENT_GROUP,
                CLIENT_ATTACHMENT.NAME,
                CLIENT_ATTACHMENT.STATUS,
                CLIENT_ATTACHMENT.STATUS_DETAIL,
                CLIENT_ATTACHMENT.LOAN_ID,
                CLIENT_ATTACHMENT.APPLICATION_ID,
                CLIENT_ATTACHMENT.CREATED_AT,
                CLIENT_ATTACHMENT.CREATED_BY,
                CLIENT_ATTACHMENT.UPDATED_AT,
                CLIENT_ATTACHMENT.UPDATED_BY,
                CLOUD_FILE.ORIGINAL_FILE_NAME
            )).from(CLIENT_ATTACHMENT.leftJoin(CLOUD_FILE).on(CLIENT_ATTACHMENT.FILE_ID.eq(CLOUD_FILE.ID)));

        if (clientId != null) {
            select.where(CLIENT_ATTACHMENT.CLIENT_ID.eq(clientId));
        }

        if (attachmentType != null) {
            select.where(CLIENT_ATTACHMENT.ATTACHMENT_TYPE.eq(attachmentType));
        }

        return select;
    }

    @Override
    protected Object id(Record item) {
        return item.get(CLIENT_ATTACHMENT.ID);
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setAttachmentType(String attachmentType) {
        this.attachmentType = attachmentType;
    }
}
