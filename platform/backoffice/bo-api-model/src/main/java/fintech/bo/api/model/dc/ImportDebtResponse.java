package fintech.bo.api.model.dc;

import lombok.Data;

@Data
public class ImportDebtResponse {
    private int processedCount;
    private int totalCount;
}
