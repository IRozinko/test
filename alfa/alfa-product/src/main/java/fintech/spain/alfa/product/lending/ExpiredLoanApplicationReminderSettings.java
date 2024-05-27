package fintech.spain.alfa.product.lending;

import lombok.Data;

@Data
public class ExpiredLoanApplicationReminderSettings {

    private Integer hours;
    private boolean newClients;
    private boolean repeatedClients;
}
