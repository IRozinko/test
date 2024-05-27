package fintech.spain.alfa.bo.model;

import lombok.Data;

@Data
public class AddClientAddressRequest {

    private Long clientId;
    private String type;
    private String street;
    private String houseNumber;
    private String province;
    private String city;
    private String postalCode;
    private String housingTenure;

}
