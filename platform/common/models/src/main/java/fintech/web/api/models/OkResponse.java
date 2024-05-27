package fintech.web.api.models;

import lombok.Data;

@Data
public class OkResponse {

    public static final OkResponse OK = new OkResponse();

    private String result = "OK";
}
