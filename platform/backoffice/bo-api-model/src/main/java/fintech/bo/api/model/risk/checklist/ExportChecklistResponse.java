package fintech.bo.api.model.risk.checklist;

import lombok.Data;

@Data
public class ExportChecklistResponse {
    private Long fileId;

    private String fileName;
}
