package fintech.nordigen.impl;

import fintech.nordigen.model.NordigenStatus;
import lombok.Data;
import lombok.ToString;

@ToString(exclude = {"responseBody"})
@Data
public class NordigenResponse {

    private NordigenStatus status;
    private int responseStatusCode;
    private String error;
    private String responseBody;

    public static NordigenResponse error(int statusCode, String responseBody, String error) {
        NordigenResponse response = new NordigenResponse();
        response.setStatus(NordigenStatus.ERROR);
        response.setResponseStatusCode(statusCode);
        response.setResponseBody(responseBody);
        response.setError(error);
        return response;
    }

    public static NordigenResponse ok(int statusCode, String responseBody) {
        NordigenResponse response = new NordigenResponse();
        response.setStatus(NordigenStatus.OK);
        response.setResponseStatusCode(statusCode);
        response.setResponseBody(responseBody);
        return response;
    }
}
