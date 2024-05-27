package fintech.bo.components.client.dto;

import fintech.bo.components.security.HiddenForSecuredQuery;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
public class ClientDTO {

    private Long id;
    private String clientNumber;

    @HiddenForSecuredQuery
    private String phone;

    @HiddenForSecuredQuery
    private String additionalPhone;

    @HiddenForSecuredQuery
    private String email;

    @HiddenForSecuredQuery
    private String firstName;

    @HiddenForSecuredQuery
    private String secondFirstName;

    @HiddenForSecuredQuery
    private String lastName;

    @HiddenForSecuredQuery
    private String secondLastName;

    @HiddenForSecuredQuery
    private String maidenName;

    @HiddenForSecuredQuery
    private String documentNumber;

    @HiddenForSecuredQuery
    private String accountNumber;

    @HiddenForSecuredQuery
    private LocalDate dateOfBirth;
    private Boolean acceptTerms;
    private Boolean acceptMarketing;
    private Boolean acceptVerification;
    private Boolean acceptPrivacyPolicy;
    private Boolean blockCommunication;
    private Boolean excludedFromAsnef;
    private Boolean transferredToLoc;
    private Map<String, String> attributes = new HashMap<>();
    private LocalDateTime createdAt;

    @HiddenForSecuredQuery
    private String gender;
    private boolean deleted;

    private Long paidLoans;
}
