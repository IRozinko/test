package fintech.spain.alfa.product.crosscheck;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class CrosscheckRequest {

    @NotEmpty
    private String dni;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String email;
}
