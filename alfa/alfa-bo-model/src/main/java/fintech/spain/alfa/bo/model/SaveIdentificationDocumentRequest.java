package fintech.spain.alfa.bo.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class SaveIdentificationDocumentRequest {
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
    private Attachment frontAttachment;
    private Attachment backAttachment;

    /**
     * indicates whether event should be emitted when identification document is being saved
     */
    private boolean notifyOnSave;

    public enum DocumentType {
        DNI("DNI"),
        NIE("NIE"),
        PASSPORT("Passport/ID card");

        private String label;

        DocumentType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }


    @Data
    @Accessors(chain = true)
    public static class Attachment {
        private String fileName;
        private Long fileId;
    }

}
