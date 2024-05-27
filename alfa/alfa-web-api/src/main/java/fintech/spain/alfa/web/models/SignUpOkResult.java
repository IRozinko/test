package fintech.spain.alfa.web.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpOkResult {

    private Long clientId;

    private String token;

}
