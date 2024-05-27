package fintech.dowjones.impl;

import fintech.dowjones.DowJonesRequestData;
import fintech.dowjones.DowJonesResponseData;
import fintech.dowjones.model.search.name.NameSearchResult;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component(MockDowJonesProviderBean.NAME)
public class MockDowJonesProviderBean implements DowJonesProvider {

    @Autowired
    private DowJonesObjectSerializer dowJonesObjectSerializer;

    public static final String NAME = "mock-dowjones";
    public static final String SUCCESS_RESPONSE = "dowjones/mock-success-search-name-response.xml";
    public static final String ERROR_RESPONSE = "dowjones/mock-error-callback.xml";

    private String responseResource = SUCCESS_RESPONSE;
    private boolean throwError;

    @Override
    public DowJonesResponseData search(DowJonesRequestData request) {
        return generateResponse();
    }

    @SneakyThrows
    private DowJonesResponseData generateResponse() {
        if (throwError) {
            return DowJonesResponseData.error("mock-dowjones-url", 0,
                null, "Simulating DowJones error");
        }

        @Cleanup
        InputStream inputStream = new ClassPathResource(responseResource).getInputStream();

        JAXBElement object = (JAXBElement) dowJonesObjectSerializer.unmarshal(inputStream);
        List<JAXBElement> content = new ArrayList<>();
        content.add(object);
        NameSearchResult nameSearchResult = (NameSearchResult) object.getValue();
        DowJonesResponseData response = new DowJonesResponseData();
        response.setUrl("mock-dowjones-url");
        response.setStatusCode(200);
        response.setResponseBody(nameSearchResult.toString());
        response.setNameSearchResult(nameSearchResult);
        return response;
    }

    public void setResponseResource(String responseResource) {
        this.responseResource = responseResource;
    }
 
    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

}
