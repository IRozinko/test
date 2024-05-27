package fintech.bo.api.model.disbursements;

import lombok.Data;

@Data
public class DisbursementExportResponse {

    private Long fileId;

    private String fileName;

    public boolean isFile() {
        return fileId != null;
    }

}
