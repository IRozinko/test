package fintech.spain.alfa.web.models;

import fintech.crm.client.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PersonalDetailsResponse {
    private Long clientId;
    private String number;
    private String firstName;
    private String lastName;
    private String secondFirstName;
    private String address;
    private String phone;
    private String email;
    private String documentNumber;
    private String accountNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
    private boolean acceptTerms;
    private boolean acceptVerification;
    private boolean acceptMarketing;

}
