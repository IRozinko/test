package fintech.crm.contacts;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VerifyPhoneCommand {
    private Long phoneContactId;
    private String code;
    private LocalDateTime codeCreatedAfter;
}

