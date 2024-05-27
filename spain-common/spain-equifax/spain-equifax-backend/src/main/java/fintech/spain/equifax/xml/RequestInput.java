package fintech.spain.equifax.xml;

import lombok.Data;

@Data
public class RequestInput {

    private String userId;
    private String password;
    private String organizationCode;
    private String orchestrationCode;
    private String documentNumber;

}
