package fintech.spain.alfa.product.cms;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ContactMeModel {

    private String name;
    private String email;
    private String phone;
    private String comment;
    private String ipAddress;
}
