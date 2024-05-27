package fintech.spain.equifax.model;

import lombok.Data;
import lombok.ToString;

@ToString
@Data
public class EquifaxRequest {

    private Long clientId;
    private Long applicationId;
    private String documentNumber;
    private String postalCode;
    private String firstName;
    private String lastName;
    private String secondLastName;

}
