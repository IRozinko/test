package fintech.crm.address.impl;

import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.address.SaveClientAddressCommand;
import fintech.crm.address.db.ClientAddressEntity;
import fintech.crm.address.db.ClientAddressRepository;
import fintech.crm.client.db.ClientRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static fintech.crm.db.Entities.clientAddress;

@Component
class ClientAddressServiceBean implements ClientAddressService {

    @Autowired
    private ClientAddressRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    @Override
    public Long addAddress(SaveClientAddressCommand command) {
        checkArgument(StringUtils.isNotEmpty(command.getType()), "Client address type is required");

        getClientPrimaryAddress(command.getClientId(), command.getType()).ifPresent(address -> {
            ClientAddressEntity currentAddress = repository.getRequired(address.getId());
            currentAddress.setPrimary(false);
        });

        ClientAddressEntity entity = new ClientAddressEntity();
        entity.setPrimary(true);
        entity.setClient(clientRepository.getRequired(command.getClientId()));
        entity.setType(command.getType());
        entity.setStreet(command.getStreet());
        entity.setHouseNumber(command.getHouseNumber());
        entity.setProvince(command.getProvince());
        entity.setCity(command.getCity());
        entity.setPostalCode(command.getPostalCode());
        entity.setHousingTenure(command.getHousingTenure());
        entity.setHouseFloor(command.getHouseFloor());
        entity.setHouseLetter(command.getHouseLetter());
        repository.saveAndFlush(entity);

        return entity.getId();
    }

    @Override
    @Transactional
    public Optional<ClientAddress> getClientPrimaryAddress(Long clientId, String type) {
        return repository
            .getOptional(clientAddress.client.id.eq(clientId)
                .and(clientAddress.type.eq(type)).and(clientAddress.primary.isTrue()))
            .map(ClientAddressEntity::toValueObject);
    }


}
