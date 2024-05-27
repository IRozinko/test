package fintech.spain.alfa.product.registration.forms;

import fintech.spain.alfa.product.registration.validators.ValidGender;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString(exclude = {"address"})
@Accessors(chain = true)
public class ApplicationForm {

    @NotNull
    private LocalDate dateOfBirth;

    @NotEmpty
    @ValidGender
    private String gender;

    @NotNull
    @Valid
    private AddressData address = new AddressData();

    private String employmentStatus;

    private String employmentDetail;

    private String monthlyIncome;

    private String familyStatus;

    private String numberOfDependants;

    private String loanPurpose;

    @NotEmpty
    private String incomeSource;

    private Map<String, String> attributes = new HashMap<>();

}
