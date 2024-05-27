package fintech.crm.contacts;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationCommand {
    private Long clientId;
    private String email;
}

