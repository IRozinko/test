package fintech.crm.client.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.crm.CrmConstants;
import fintech.crm.client.ClientImportService;
import fintech.crm.client.CreateClientCommand;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Slf4j
public class ClientImportServiceBean implements ClientImportService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private JPAQueryFactory queryFactory;

    @Transactional
    public ClientEntity createClient(CreateClientCommand command) {
        ClientEntity entity = queryFactory
            .selectFrom(fintech.crm.db.Entities.client)
            .where(fintech.crm.db.Entities.client.documentNumber.eq(command.getDocumentNumber()))
            .fetchOne();
        ClientEntity client;
        if (entity == null) {
            client = new ClientEntity();
            String clientNumber = command.getClientNumber();
            client.setNumber(clientNumber);
            client.setDocumentNumber(command.getDocumentNumber());
            client.setPhone(command.getPhone());
            client.setAccountNumber(command.getAccountNumber());
            client.setFirstName(command.getFirstName());
            client.setLastName(command.getLastName());
            client.setLocale(CrmConstants.DEFAULT_LOCALE);
            client = clientRepository.saveAndFlush(client);
        } else {
            return entity;
        }
        return client;
    }
}
