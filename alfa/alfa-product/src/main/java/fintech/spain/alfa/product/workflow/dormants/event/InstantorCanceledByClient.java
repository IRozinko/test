package fintech.spain.alfa.product.workflow.dormants.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstantorCanceledByClient {

    private Long clientId;
}
