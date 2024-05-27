package fintech.spain.alfa.bo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BlacklistClientRequest {

    private Long clientId;
    private String comment;

}
