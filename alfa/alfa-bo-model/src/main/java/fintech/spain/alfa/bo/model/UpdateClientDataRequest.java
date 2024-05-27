package fintech.spain.alfa.bo.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateClientDataRequest {

    private Long clientId;

    private String firstName;

    private String lastName;

    private String secondLastName;

    private LocalDate dateOfBirth;

    private String gender;

    private String email;

    private String phone;

    private String additionalPhone;

    private String accountNumber;

    private boolean blockCommunication;

    private boolean excludedFromASNEF;
}
