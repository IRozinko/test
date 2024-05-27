package fintech.ga;

import fintech.ga.impl.GARequest;
import fintech.ga.impl.GAResponse;

import java.io.IOException;

public interface GADataSender {

    GAResponse sendData(GARequest request) throws IOException;
}
