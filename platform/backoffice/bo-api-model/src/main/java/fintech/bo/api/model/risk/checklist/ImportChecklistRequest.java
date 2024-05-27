package fintech.bo.api.model.risk.checklist;

import lombok.Data;

@Data
public class ImportChecklistRequest {

    private String type;

    private Long fileId;

    private boolean override;
}
