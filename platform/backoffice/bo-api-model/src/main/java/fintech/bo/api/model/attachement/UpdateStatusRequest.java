package fintech.bo.api.model.attachement;

import lombok.Data;

@Data
public class UpdateStatusRequest {
    private Long attachmentId;
    private String status;
    private String statusDetail;
}
