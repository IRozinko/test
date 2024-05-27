package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.SaveCreditLimitCommand;
import fintech.lending.core.creditlimit.AddCreditLimitCommand;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.task.spi.TaskContext;
import fintech.task.spi.TaskListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CalculateApplicationCreditLimitFromDocuments implements TaskListener {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private CreditLimitService creditLimitService;

    @Autowired
    private ClientService clientService;

    @Override
    public void handle(TaskContext context) {
        LoanApplication application = loanApplicationService.get(context.getTask().getApplicationId());

        Client client = clientService.get(application.getClientId());
        Validate.notBlank(client.getAccountNumber(), "Client has not bank account number");

        log.warn("FAKE CREDIT LIMIT CALCULATION!!!!");
        BigDecimal creditLimit = application.getRequestedPrincipal();


        saveClientCreditLimit(application.getClientId(), creditLimit);
        saveApplicationCreditLimit(application.getId(), creditLimit);
    }

    private void saveClientCreditLimit(Long clientId, BigDecimal limit) {
        AddCreditLimitCommand addLimitCommand = new AddCreditLimitCommand();
        addLimitCommand.setActiveFrom(TimeMachine.today());
        addLimitCommand.setClientId(clientId);
        addLimitCommand.setLimit(limit);
        addLimitCommand.setReason("UnderwritingFirstLoan");
        creditLimitService.addLimit(addLimitCommand);
    }

    private void saveApplicationCreditLimit(Long applicationId, BigDecimal creditLimit) {
        loanApplicationService.saveCreditLimit(
            new SaveCreditLimitCommand(applicationId, creditLimit));
    }
}
