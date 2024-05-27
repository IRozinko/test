package fintech.dowjones;


import fintech.dowjones.model.search.name.NameSearchResult;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DowJonesResponseData {

    private String url;
    private int statusCode;
    private String responseBody;
    private String error;
    private NameSearchResult nameSearchResult;

    public static DowJonesResponseData error(String url, int statusCode, String responseBody, String error) {
        DowJonesResponseData response = new DowJonesResponseData();
        response.setUrl(url);
        response.setStatusCode(statusCode);
        response.setResponseBody(responseBody);
        response.setError(error);
        return response;
    }

    public static DowJonesResponseData ok(String url, int statusCode, String responseBody) {
        DowJonesResponseData response = new DowJonesResponseData();
        response.setUrl(url);
        response.setStatusCode(statusCode);
        response.setResponseBody(responseBody);
        return response;
    }
}
