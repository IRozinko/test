package fintech.crm.bankaccount.events;

import fintech.crm.bankaccount.ClientBankAccount;

public class ClientBankAccountAddedEvent extends AbstractClientBankAccountEvent {

    public ClientBankAccountAddedEvent(ClientBankAccount account) {
        super(account);
    }
}
