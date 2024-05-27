package fintech.spain.alfa.product.crm;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Address {

    private String postalCode;

    private String city;

    private String province;

    private String state;
}
