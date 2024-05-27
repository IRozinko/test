package fintech.crm.contacts;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class UpdatePhoneContactCommand {

    private String countryCode;
    private String localNumber;
    private PhoneType type;
    private PhoneSource source;
    private LocalDate activeTill;
    private boolean legalConsent;
}
