package fintech.crm.client;

import fintech.crm.client.model.ChangeAcceptMarketingCommand;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ClientService {

    Long create(CreateClientCommand command);

    void update(UpdateClientCommand command);

    void updateAttributes(UpdateClientCommand command);

    void updateBlockCommunication(Long clientId, boolean isBlockCommunication, String reason);

    Client get(Long clientId);

    Optional<Client> findByClientNumber(String clientNumber);

    Optional<Client> findByPhone(String phone);

    Optional<Client> findByDocumentNumber(String dni);

    void addToSegment(Long clientId, LocalDateTime when, String... segments);

    void addToSegment(Long clientId, String... segments);

    void removeFromSegment(Long clientId, String... segments);

    void updateAcceptMarketing(ChangeAcceptMarketingCommand cmd);
}
