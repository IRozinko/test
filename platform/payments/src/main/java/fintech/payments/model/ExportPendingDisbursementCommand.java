package fintech.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExportPendingDisbursementCommand {
    
    private Long institutionId;
    private Long institutionAccountId;
}
