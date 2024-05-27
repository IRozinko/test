package fintech.crm.bankaccount;

import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public interface ClientBankAccountService {

    Long addBankAccount(AddClientBankAccountCommand command);

    void makePrimary(Long bankAccountId);

    boolean deactivatePrimaryAccount(Long clientId);

    Optional<ClientBankAccount> findPrimaryByClientId(Long clientId);

    List<ClientBankAccount> findAllByClientId(Long clientId);

    Optional<ClientBankAccount> findByAccountNumber(Long clientId, String accountNumber);

    ClientBankAccount get(Long id);

    boolean isBankAccountAvailableForClient(Long clientId, String accountNumber);

    static String normalizeAccountNumber(String accountNumber) {
        return StringUtils.replace(accountNumber, " ", "").toUpperCase();
    }
}
