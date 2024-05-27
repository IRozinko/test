package fintech.crm.address;

import java.util.Optional;

public interface ClientAddressService {

    Long addAddress(SaveClientAddressCommand command);

    Optional<ClientAddress> getClientPrimaryAddress(Long clientId, String type);

}
