package fintech.spain.alfa.product.affiliate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fintech.EmailWithDomain;
import fintech.spain.platform.web.validations.DocumentNumber;
import fintech.spain.alfa.product.affiliate.validators.AcceptTerms;
import fintech.spain.alfa.product.affiliate.validators.RejectedClientApplication;
import fintech.spain.alfa.product.affiliate.validators.UniqueClientContacts;
import fintech.spain.alfa.product.affiliate.validators.ValidGender;
import fintech.spain.alfa.product.validators.DniBlacklist;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Accessors(chain = true)
@UniqueClientContacts
@RejectedClientApplication
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AffiliateRegistrationStep1Form {

    @NotNull
    @JsonProperty("principal")
    private BigDecimal amount;

    @NotNull
    private Long term;

    @NotEmpty
    @JsonProperty("name")
    private String firstName;

    @NotEmpty
    @JsonProperty("surname")
    private String lastName;

    @NotEmpty
    @DocumentNumber
    @DniBlacklist
    @JsonProperty("id_doc_number")
    private String documentNumber;

    @NotEmpty
    @ValidGender
    private String gender;

    @NotNull
    @JsonProperty("birth_date")
    private LocalDate dateOfBirth;

    @NotEmpty
    private String street;

    @NotEmpty
    @JsonProperty("zipcode")
    private String postalCode;

    @NotEmpty
    private String city;

    @NotEmpty
    @JsonProperty("phone")
    private String mobilePhone;

    private String otherPhone;

    @EmailWithDomain
    @NotEmpty
    private String email;

    @JsonProperty("IBAN")
    private String iban;

    @AcceptTerms
    private Integer tos;

    private Boolean cirex;

    private String campaign;

    private String lead;

    private String lead2;

    private Boolean acceptMarketing;

    private String maritalStatus;
    private Integer numberOfDependants;
    private String education;
    private String workSector;
    private String occupation;
    private LocalDate employedSince;
    private LocalDate nextSalaryDate;
    private String housingTenure;
    private String incomeSource;
    @JsonProperty("excluded_from_ASNEF")
    private Boolean excludedFromASNEF;
    private BigDecimal monthlyExpenses;
    private BigDecimal netoIncome;

    private String blackbox;
}
