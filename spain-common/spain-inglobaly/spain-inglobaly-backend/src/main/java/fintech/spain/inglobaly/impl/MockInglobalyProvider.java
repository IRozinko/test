package fintech.spain.inglobaly.impl;

import com.global.info.ws.soap.ListadoDomiciliosTelefonos;
import com.global.info.ws.soap.ObtenerDomicilioNIFResponse;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.io.InputStream;

@Component(MockInglobalyProvider.NAME)
public class MockInglobalyProvider implements InglobalyProvider {

    public static final String RESPONSE_FOUND = "inglobaly/inglobaly-response.xml";

    public static final String NAME = "mock-spain-inglobaly-provider";

    private boolean throwError = false;

    private String responseResource = null;

    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }

    public void setResponseResource(String responseResource) {
        this.responseResource = responseResource;
    }

    @SneakyThrows
    @Override
    public ListadoDomiciliosTelefonos request(String documentNumber) {
        if (throwError) {
            throw new RuntimeException("Simulating Inglobaly error");
        }
        if (responseResource == null) {
            return null;
        }
        try (InputStream is = new ClassPathResource(responseResource).getInputStream()) {
            JAXBElement jaxbElement = (JAXBElement) ObjectSerializer.unmarshal(is);
            ObtenerDomicilioNIFResponse response =  (ObtenerDomicilioNIFResponse) jaxbElement.getValue();
            return response.getReturn();
        }
    }

    public void returnNotFound() {
        this.responseResource = null;
        this.throwError = false;
    }

}
