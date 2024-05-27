package fintech.spain.alfa.web.models;

import fintech.FileHashId;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AttachmentInfo {

    private String fileId;
    private String fileName;
    private AttachmentSubType subType;

    public AttachmentInfo(Attachment attachment) {
        this.fileId = FileHashId.encodeFileId(attachment.getClientId(), attachment.getFileId());
        this.fileName = attachment.getName();
        this.subType = attachment.getSubType();
    }

}
