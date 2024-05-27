package fintech.crm.bankaccount;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString(exclude = {"accountNumber"})
public class AddClientBankAccountCommand {

    private Long clientId;
    private String bankName = "";
    private String accountNumber;
    private String accountOwnerName = "";
    private String currency;
    private BigDecimal balance;
    private Long numberOfTransactions = 0L;
    private boolean primaryAccount;
}
