package fintech.spain.inglobaly.impl;

import com.global.info.ws.soap.ListadoDomiciliosTelefonos;

public interface InglobalyProvider {

    ListadoDomiciliosTelefonos request(String documentNumber);
}
