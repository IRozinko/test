package fintech.bo.components.payments.handlers;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OtherTransactionConfig {

    private String transactionSubType;
    private boolean showClientSelection;
}
