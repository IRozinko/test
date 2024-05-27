package fintech.crm.bankaccount.impl;

import com.google.common.base.MoreObjects;
import fintech.crm.bankaccount.AddClientBankAccountCommand;
import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.bankaccount.DuplicateBankAccountException;
import fintech.crm.bankaccount.db.ClientBankAccountEntity;
import fintech.crm.bankaccount.db.ClientBankAccountRepository;
import fintech.crm.bankaccount.events.ClientBankAccountAddedEvent;
import fintech.crm.bankaccount.events.ClientPrimaryBankAccountSetEvent;
import fintech.crm.client.db.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.crm.db.Entities.clientBankAccount;

@Transactional
@Component
class ClientBankAccountServiceBean implements ClientBankAccountService {

    @Autowired
    private ClientBankAccountRepository repository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public Long addBankAccount(AddClientBankAccountCommand command) {
        String accountNumber = ClientBankAccountService.normalizeAccountNumber(command.getAccountNumber());

        ClientBankAccountEntity entity = repository.findAll(
            clientBankAccount.client.id.eq(command.getClientId())
                .and(clientBankAccount.accountNumber.equalsIgnoreCase(accountNumber)),
            clientBankAccount.createdAt.desc()
        ).stream().findFirst().orElseGet(ClientBankAccountEntity::new);

        entity.setClient(clientRepository.getRequired(command.getClientId()));
        entity.setBankName(MoreObjects.firstNonNull(command.getBankName(), ""));
        entity.setAccountNumber(accountNumber);
        entity.setAccountOwnerName(MoreObjects.firstNonNull(command.getAccountOwnerName(), ""));
        entity.setBalance(command.getBalance());
        entity.setCurrency(command.getCurrency());
        entity.setNumberOfTransactions(command.getNumberOfTransactions());

        Long id = repository.saveAndFlush(entity).getId();
        eventPublisher.publishEvent(new ClientBankAccountAddedEvent(entity.toValueObject()));

        if (command.isPrimaryAccount()) {
            makePrimary(id);
        }

        return id;
    }

    @Override
    public void makePrimary(Long bankAccountId) throws DuplicateBankAccountException {
        ClientBankAccountEntity entity = repository.getRequired(bankAccountId);
        if (!isBankAccountAvailableForClient(entity.getClient().getId(), entity.getAccountNumber())) {
            throw new DuplicateBankAccountException("Account already in use");
        }
        repository.findAll(clientBankAccount.client.id.eq(entity.getClient().getId())).forEach(bankAccount -> {
            bankAccount.setPrimary(false);
        });
        entity.setPrimary(true);
        entity.getClient().setAccountNumber(entity.getAccountNumber());
        eventPublisher.publishEvent(new ClientPrimaryBankAccountSetEvent(entity.toValueObject()));
    }

    @Override
    public boolean deactivatePrimaryAccount(Long clientId) {
        Optional<ClientBankAccount> primary = findPrimaryByClientId(clientId);
        if (!primary.isPresent()) {
            return false;
        }
        ClientBankAccountEntity entity = repository.getRequired(primary.get().getId());
        entity.setPrimary(false);
        return true;
    }

    @Override
    public Optional<ClientBankAccount> findPrimaryByClientId(Long clientId) {
        return repository.getOptional(clientBankAccount.client.id.eq(clientId).and(clientBankAccount.primary.isTrue()))
            .map(ClientBankAccountEntity::toValueObject);
    }

    @Override
    public List<ClientBankAccount> findAllByClientId(Long clientId) {
        return repository.findAll(clientBankAccount.client.id.eq(clientId), clientBankAccount.id.asc())
            .stream()
            .map(ClientBankAccountEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ClientBankAccount> findByAccountNumber(Long clientId, String accountNumber) {
        accountNumber = ClientBankAccountService.normalizeAccountNumber(accountNumber);
        return repository.getOptional(clientBankAccount.accountNumber.equalsIgnoreCase(accountNumber).and(clientBankAccount.client.id.eq(clientId)))
            .map(ClientBankAccountEntity::toValueObject);

    }

    @Override
    public ClientBankAccount get(Long id) {
        return repository.getRequired(id).toValueObject();
    }

    @Override
    public boolean isBankAccountAvailableForClient(Long clientId, String accountNumber) {
        accountNumber = ClientBankAccountService.normalizeAccountNumber(accountNumber);
        return !repository.exists(clientBankAccount.accountNumber.equalsIgnoreCase(accountNumber)
            .and(clientBankAccount.client.id.ne(clientId))
            .and(clientBankAccount.primary.isTrue())
            .and(clientBankAccount.client.deleted.isFalse()));
    }

}
