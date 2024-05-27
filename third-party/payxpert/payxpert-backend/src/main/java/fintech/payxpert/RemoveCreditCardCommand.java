package fintech.payxpert;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RemoveCreditCardCommand {

    private Long clientId;
    private Long creditCardId;
}
