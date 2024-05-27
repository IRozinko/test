package fintech.crm.contacts;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString(exclude = {"localNumber"})
public class PhoneContact {
    private Long id;
    private Long clientId;
    private String countryCode;
    private String localNumber;
    private boolean primary;
    private PhoneType phoneType;
    private boolean verified;
    private LocalDateTime verifiedAt;
    private boolean active;
    private LocalDate activeTill;
    private PhoneSource source;
    private boolean legalConsent;

    public String getPhoneNumber() {
        return StringUtils.join(countryCode, localNumber);
    }
}
