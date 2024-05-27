package fintech.spain.alfa.product.registration.forms;

import fintech.EmailWithDomain;
import fintech.spain.alfa.product.validators.Nationality;
import fintech.spain.alfa.product.validators.UniqueDocumentNumber;
import fintech.spain.alfa.product.validators.ValidDocumentNationality;
import fintech.spain.platform.web.validations.DocumentNumber;
import fintech.spain.platform.web.validations.Extended;
import fintech.spain.platform.web.validations.UniqueEmail;
import fintech.spain.platform.web.validations.UniquePhoneNumber;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
@Accessors(chain = true)
@ToString(exclude = {"password", "mobilePhone", "documentNumber"})
@ValidDocumentNationality(groups = Extended.class)
public class SignUpForm {

    @NotEmpty
    private String firstName;

    @NotEmpty
    private String lastName;

    private String secondLastName;

    @NotEmpty
    @EmailWithDomain(groups = Extended.class)
    @UniqueEmail(groups = Extended.class)
    private String email;

    @NotEmpty
    @UniquePhoneNumber(groups = Extended.class)
    private String mobilePhone;

    private String otherPhone;

    @NotEmpty
    @Length(min = 6, groups = Extended.class)
    private String password;

    @NotEmpty
    @DocumentNumber(groups = Extended.class)
    @UniqueDocumentNumber(groups = Extended.class)
    private String documentNumber;

    @NotEmpty
    @Nationality(groups = Extended.class)
    private String countryCodeOfNationality;

    @NotNull
    private BigDecimal amount;

    @NotNull
    private Long termInDays;

    private String promoCode;

    @AssertTrue
    private boolean acceptTerms;

    @NotEmpty
    private String blackbox;

    @AssertTrue
    private boolean acceptVerification;

    private boolean acceptMarketing;

    private AffiliateData affiliate;

    private AnalyticsData analytics;

    private Map<String, String> attributes = new HashMap<>();

    public boolean isAffiliate() {
        return affiliate != null && affiliate.getAffiliateName() != null;
    }
}
