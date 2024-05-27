package fintech.crm.contacts;


import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
@ToString(exclude = {"localNumber"})
public class AddPhoneCommand {
    private Long clientId;
    private String countryCode;
    private String localNumber;
    private PhoneType type;
    private PhoneSource source = PhoneSource.OTHER;
    private LocalDate activeTill;
    private boolean legalConsent;
}

