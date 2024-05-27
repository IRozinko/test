package fintech.crm.client.model;

import fintech.EmailWithDomain;
import fintech.crm.address.ClientAddress;
import fintech.crm.bankaccount.ClientBankAccount;
import fintech.filestorage.spi.FileContent;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class DormantsClientData {

    private Long id;

    @NotEmpty
    private String number;

    @EmailWithDomain
    @NotEmpty
    private String email;

    @NotEmpty
    private String mobilePhone;
    private String otherPhone;

    @NotEmpty
    private String firstName;
    private String secondFirstName;

    @NotEmpty
    private String lastName;
    private String secondLastName;
    private String maidenName;

    @NotEmpty
    private String documentNumber;

    private String gender;
    private LocalDate dateOfBirth;

    private boolean acceptTerms;
    private boolean acceptMarketing;
    private boolean acceptVerification;
    private boolean acceptPrivacyPolicy;
    private boolean blockCommunication;
    private boolean excludedFromASNEF;

    private String countryCodeOfNationality;

    private ClientAddress address = new ClientAddress();
    private ClientBankAccount bankAccount = new ClientBankAccount();

    private Map<String, String> attributes = new HashMap<>();

    private FileContent loanAgreementAttachment;
    private FileContent standardInfoAttachment;
}
