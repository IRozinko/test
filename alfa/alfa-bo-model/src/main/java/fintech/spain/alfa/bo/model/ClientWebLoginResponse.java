package fintech.spain.alfa.bo.model;

import lombok.Data;

@Data
public class ClientWebLoginResponse {

    private String jwtToken;
    private String url;
}
