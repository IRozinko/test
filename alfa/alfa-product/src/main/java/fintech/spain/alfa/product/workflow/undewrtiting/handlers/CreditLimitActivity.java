package fintech.spain.alfa.product.workflow.undewrtiting.handlers;

import fintech.TimeMachine;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.commands.SaveCreditLimitCommand;
import fintech.lending.core.creditlimit.AddCreditLimitCommand;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.spain.alfa.product.lending.CalculateCreditLimitCommand;
import fintech.spain.alfa.product.lending.CreditLimitCalculator;
import fintech.spain.alfa.product.workflow.common.Resolutions;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityHandler;
import fintech.workflow.spi.ActivityResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.math.BigDecimal;

@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public class CreditLimitActivity implements ActivityHandler {

    @Autowired
    private ClientService clientService;

    @Autowired
    private CreditLimitService creditLimitService;

    @Autowired
    private CreditLimitCalculator creditLimitCalculator;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Override
    public ActivityResult handle(ActivityContext context) {
        try {
            return doHandle(context);
        } catch (IllegalStateException e) {
            return ActivityResult.fail(e.getMessage());
        }
    }

    private ActivityResult doHandle(ActivityContext context) {
        Client client = clientService.get(context.getClientId());

        BigDecimal clientLimit = creditLimitService.getClientLimit(context.getClientId())
            .map(CreditLimit::getLimit).orElse(null);

        BigDecimal creditLimit = calculateCreditLimit(client, clientLimit);
        saveCreditLimit(context.getClientId(), creditLimit);
        saveApplicationCreditLimit(context.getWorkflow().getApplicationId(), creditLimit);

        return ActivityResult.resolution(Resolutions.OK, "");
    }

    private BigDecimal calculateCreditLimit(Client client, @Nullable BigDecimal currentLimit) {
        CalculateCreditLimitCommand command = new CalculateCreditLimitCommand()
            .setClientId(client.getId())
            .setLastCreditLimit(currentLimit);
        return creditLimitCalculator.calculateCreditLimit(command);
    }

    private void saveCreditLimit(Long clientId, BigDecimal limit) {
        AddCreditLimitCommand addLimitCommand = new AddCreditLimitCommand();
        addLimitCommand.setClientId(clientId);
        addLimitCommand.setLimit(limit);
        addLimitCommand.setActiveFrom(TimeMachine.today());
        addLimitCommand.setReason("Underwriting");
        creditLimitService.addLimit(addLimitCommand);
    }

    private void saveApplicationCreditLimit(Long applicationId, BigDecimal creditLimit) {
        loanApplicationService.saveCreditLimit(new SaveCreditLimitCommand(applicationId, creditLimit));
    }

}
