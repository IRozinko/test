package fintech.crm.client;

import fintech.crm.client.db.ClientEntity;

public interface ClientImportService {
    ClientEntity createClient(CreateClientCommand command);

}
