package fintech.spain.alfa.product.registration;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VerifyPhoneResult {

    private final boolean verified;
    private final int availableAttempts;

}
