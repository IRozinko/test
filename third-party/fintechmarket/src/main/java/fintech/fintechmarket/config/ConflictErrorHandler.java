package fintech.fintechmarket.config;

import fintech.fintechmarket.exception.ScenarioVersionConflictException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;

public class ConflictErrorHandler implements ResponseErrorHandler {

    private final DefaultResponseErrorHandler defaultErrorHandler = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().value() == 409;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        if (hasError(response))
            throw new ScenarioVersionConflictException();

        if (defaultErrorHandler.hasError(response))
            defaultErrorHandler.handleError(response);
    }
}
