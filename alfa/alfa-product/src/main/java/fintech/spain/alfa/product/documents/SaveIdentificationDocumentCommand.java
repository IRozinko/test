package fintech.spain.alfa.product.documents;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class SaveIdentificationDocumentCommand {
    private Long clientId;
    private Long taskId;
    private DocumentType documentType;
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
    private Long frontFileId;
    private String frontFileName;
    private Long backFileId;
    private String backFileName;
    private String customerServiceAssessment;
    /**
     * indicates whether event should be emitted when identification document is being saved
     */
    private boolean notifyOnSave;
}
