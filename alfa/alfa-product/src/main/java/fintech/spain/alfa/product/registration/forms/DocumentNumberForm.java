package fintech.spain.alfa.product.registration.forms;

import fintech.spain.alfa.product.validators.Nationality;
import fintech.spain.alfa.product.validators.UniqueDocumentNumber;
import fintech.spain.alfa.product.validators.ValidDocumentFormNationality;
import fintech.spain.platform.web.validations.DocumentNumber;
import fintech.spain.platform.web.validations.Extended;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidDocumentFormNationality(groups = Extended.class)
public class DocumentNumberForm {

    @NotEmpty
    @DocumentNumber(groups = Extended.class)
    @UniqueDocumentNumber(groups = Extended.class)
    private String documentNumber;

    @NotEmpty
    @Nationality(groups = Extended.class)
    private String countryCodeOfNationality;

}
