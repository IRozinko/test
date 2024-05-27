package fintech.crm.contacts;


import lombok.Data;

@Data
public class AddPhoneVerificationCommand {
    private Long phoneContactId;
    private String code;
}

