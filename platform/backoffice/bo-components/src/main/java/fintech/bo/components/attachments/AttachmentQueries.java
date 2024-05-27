package fintech.bo.components.attachments;

import fintech.bo.db.jooq.crm.tables.records.ClientAttachmentRecord;
import fintech.bo.db.jooq.storage.tables.records.CloudFileRecord;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.db.jooq.crm.tables.ClientAttachment.CLIENT_ATTACHMENT;
import static fintech.bo.db.jooq.storage.tables.CloudFile.CLOUD_FILE;

@Component
public class AttachmentQueries {

    @Autowired
    private DSLContext db;

    public List<ClientAttachmentRecord> findAttachmentsByType(Long clientId, String type) {
        return db.selectFrom(CLIENT_ATTACHMENT).where(CLIENT_ATTACHMENT.CLIENT_ID.eq(clientId).and(CLIENT_ATTACHMENT.ATTACHMENT_TYPE.eq(type))).fetch();
    }

    public CloudFileRecord findCloudFileById(Long fileId) {
        return db.selectFrom(CLOUD_FILE).where(CLOUD_FILE.ID.eq(fileId)).fetchOne();
    }
}
