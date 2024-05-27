package fintech.bo.api.model.client;

import lombok.Data;

@Data
public class UpdatePhoneContactRequest {

    private boolean primary;
    private boolean active;
    private boolean legalConsent;

}
