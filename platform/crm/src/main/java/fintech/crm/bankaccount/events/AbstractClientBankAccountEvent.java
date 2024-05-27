package fintech.crm.bankaccount.events;

import fintech.crm.bankaccount.ClientBankAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract  class AbstractClientBankAccountEvent {

    private final ClientBankAccount account;

}
