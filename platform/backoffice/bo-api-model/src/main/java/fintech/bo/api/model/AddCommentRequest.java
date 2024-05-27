package fintech.bo.api.model;

import lombok.Data;

@Data
public class AddCommentRequest {
    private Long clientId;
    private String comment;
}
