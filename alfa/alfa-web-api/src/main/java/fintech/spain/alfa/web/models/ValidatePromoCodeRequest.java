package fintech.spain.alfa.web.models;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class ValidatePromoCodeRequest {

    @NotEmpty
    private String promoCode;

}
