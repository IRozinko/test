package fintech.bo.api.model.address;

import lombok.Data;

@Data
public class AddressCatalogEntry {

    private String postalCode;
    private String city;
    private String province;
    private String state;
}
