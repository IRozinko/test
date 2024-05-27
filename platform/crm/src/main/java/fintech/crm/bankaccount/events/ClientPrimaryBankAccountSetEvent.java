package fintech.crm.bankaccount.events;

import fintech.crm.bankaccount.ClientBankAccount;

public class ClientPrimaryBankAccountSetEvent extends AbstractClientBankAccountEvent {

    public ClientPrimaryBankAccountSetEvent(ClientBankAccount account) {
        super(account);
    }
}
