package fintech.crm.contacts;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailContact {

    private Long id;
    private Long clientId;
    private String email;
    private boolean primary;
    private boolean verified;
    private LocalDateTime verifiedAt;

}
