package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class ChangePhoneRequest {

    @NotEmpty
    private String mobilePhone;
}
