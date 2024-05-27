package fintech.crm.address;

import lombok.Data;

@Data
public class SaveClientAddressCommand {

    private Long clientId;
    private String type;
    private String street;
    private String houseNumber;
    private String province;
    private String city;
    private String postalCode;
    private String housingTenure;
    private String houseFloor;
    private String houseLetter;
}
