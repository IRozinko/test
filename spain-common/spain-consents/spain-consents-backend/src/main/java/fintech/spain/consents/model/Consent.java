package fintech.spain.consents.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Consent {

    private Long clientId;
    private String name;
    private String version;
    private boolean accepted;
    private String source;
    private LocalDateTime changedAt;

}
