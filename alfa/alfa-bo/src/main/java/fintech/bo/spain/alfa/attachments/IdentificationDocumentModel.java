package fintech.bo.spain.alfa.attachments;

import fintech.bo.db.jooq.crm.tables.records.ClientAttachmentRecord;
import fintech.spain.alfa.bo.model.SaveIdentificationDocumentRequest;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class IdentificationDocumentModel {

    private Long clientId;
    private Long taskId;
    private SaveIdentificationDocumentRequest.DocumentType documentType;
    private String documentNumber;
    private String surname1;
    private String surname2;
    private String name;
    private String gender;
    private String nationality;
    private LocalDate dateOfBirth;
    private LocalDate expirationDate;
    private String street;
    private String house;
    private String city;
    private String province;
    private String placeOfBirth;
    private ClientAttachmentRecord frontAttachment;
    private ClientAttachmentRecord backAttachment;
    private boolean notifyOnSave;

}


