package fintech.bo.api.model.risk.checklist;

import lombok.Data;

@Data
public class AddChecklistRequest {
    private String type;
    private String value1;
    private String comment;
}
