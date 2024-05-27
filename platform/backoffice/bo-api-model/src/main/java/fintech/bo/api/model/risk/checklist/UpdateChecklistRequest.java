package fintech.bo.api.model.risk.checklist;

import lombok.Data;

@Data
public class UpdateChecklistRequest {
    private Long id;
    private String type;
    private String value1;
    private String comment;
}
