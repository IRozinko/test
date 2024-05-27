package fintech.spain.alfa.product.scoring;

import fintech.ScoringProperties;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.scoring.values.spi.ScoringValuesProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Properties;

@Slf4j
@Component
@Transactional
public class ClientDNIScoringValuesProvider implements ScoringValuesProvider {

    private static final String DOCUMENT_NUMBER = "document_number";

    @Autowired
    private ClientService clientService;

    @Override
    public Properties provide(long clientId) {
        Client client = clientService.get(clientId);
        ScoringProperties properties = new ScoringProperties();
        properties.put(DOCUMENT_NUMBER, client.getDocumentNumber());
        return properties;
    }
}
