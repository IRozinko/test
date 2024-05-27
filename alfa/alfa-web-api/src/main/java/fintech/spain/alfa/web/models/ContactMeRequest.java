package fintech.spain.alfa.web.models;

import fintech.EmailWithDomain;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class ContactMeRequest {

    @NotEmpty
    private String name;

    @EmailWithDomain
    @NotEmpty
    private String email;

    @NotEmpty
    private String phone;

    @NotEmpty
    private String comment;
}
