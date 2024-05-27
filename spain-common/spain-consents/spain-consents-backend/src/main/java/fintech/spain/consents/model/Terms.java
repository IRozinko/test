package fintech.spain.consents.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Terms {

    private String name;
    private String text;
    private String version;
    private LocalDateTime changedAt;

}
