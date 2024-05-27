package fintech.spain.alfa.product.registration.forms;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
public class AddressData {

    @NotEmpty
    private String street;

    private String houseNumber;

    @NotEmpty
    private String city;

    @NotEmpty
    private String postalCode;

    private String housingTenure;

}
