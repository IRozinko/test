package fintech.bo.api.model.client;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PhoneContactRequest {

    private Long phoneContactId;
    @NotNull
    private Long clientId;
    private String countryCode;
    private String phoneNumber;
    private boolean primary;
    private String type;
    private String source;
    private boolean legalConsent;
    private LocalDate activeTill;

    public PhoneContactRequest(long clientId) {
        this.clientId = clientId;
    }

    public boolean hasPhoneContactId() {
        return phoneContactId != null;
    }
}
