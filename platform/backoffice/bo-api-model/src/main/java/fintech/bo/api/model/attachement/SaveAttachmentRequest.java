package fintech.bo.api.model.attachement;

import lombok.Data;

@Data
public class SaveAttachmentRequest {
    private Long fileId;
    private Long clientId;
    private String type;
}
