package fintech.crm.bankaccount;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(exclude = {"accountNumber"})
public class ClientBankAccount {

    private Long id;
    private String bankName;
    private String accountOwnerName;
    private String accountNumber;
    private boolean primary;
    private Long clientId;
    private String currency;
    private BigDecimal balance;
    private Long numberOfTransactions;
}
