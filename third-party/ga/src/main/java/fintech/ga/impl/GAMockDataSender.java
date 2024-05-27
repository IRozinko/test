package fintech.ga.impl;

import fintech.ga.GADataSender;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service(GAMockDataSender.NAME)
public class GAMockDataSender implements GADataSender {

    public static final String NAME = "mock-data-sender";

    @Setter
    private GAResponse response = new GAResponse(200, "OK");

    @Override
    public GAResponse sendData(GARequest request) throws IOException {
        return response;
    }
}
