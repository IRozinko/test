package fintech.spain.alfa.product.workflow.upsell;

import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.lending.core.creditlimit.AddCreditLimitCommand;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.events.LoanDerivedValuesUpdated;
import fintech.spain.alfa.product.lending.CalculateCreditLimitCommand;
import fintech.spain.alfa.product.lending.CreditLimitCalculator;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.eq;

@Component
@Slf4j
class LoanClosedEventListener {

    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private CreditLimitCalculator creditLimitCalculator;
    @Autowired
    private ClientService clientService;
    @Autowired
    private CreditLimitService creditLimitService;

    @EventListener
    public void onEvent(LoanDerivedValuesUpdated event) {
        Loan loan = event.getLoan();
        terminateWorkflows(loan);
        recalculateClientCreditLimit(loan);
    }

    private void terminateWorkflows(Loan loan) {
        if (loan.isClosed() || DateUtils.goe(TimeMachine.today(), loan.getMaturityDate())) {
            List<Workflow> workflows = workflowService.findWorkflows(WorkflowQuery.byLoanId(loan.getId(), UpsellWorkflow.WORKFLOW, WorkflowStatus.ACTIVE));
            workflows.forEach(workflow -> workflowService.terminateWorkflow(workflow.getId(), "Expired"));
        }
    }

    private void recalculateClientCreditLimit(Loan loan) {
        if (loan.isClosed()) {
            log.info("Recalculating credit_limit for client {} loan {}", loan.getClientId(), loan.getId());
            Client client = clientService.get(loan.getClientId());
            Optional<CreditLimit> oldCreditLimit = creditLimitService.getClientLimit(loan.getClientId(), TimeMachine.today());
            BigDecimal creditLimit = calculateCreditLimit(client, oldCreditLimit.map(CreditLimit::getLimit).orElse(null));
            if (!oldCreditLimit.isPresent() || !eq(oldCreditLimit.get().getLimit(), creditLimit)) {
                log.info("Save new credit_limit for client {} limit {}", loan.getClientId(), creditLimit);
                saveCreditLimit(loan, creditLimit);
            } else {
                log.info("Skip save new credit_limit for client {} , old limit is same", loan.getClientId());
            }
        }
    }

    private void saveCreditLimit(Loan loan, BigDecimal creditLimit) {
        AddCreditLimitCommand command = new AddCreditLimitCommand();
        command.setClientId(loan.getClientId());
        command.setActiveFrom(TimeMachine.today());
        command.setReason("ClosedLoan");
        command.setLimit(creditLimit);
        creditLimitService.addLimit(command);
    }

    private BigDecimal calculateCreditLimit(Client client, BigDecimal oldCreditLimit) {
        CalculateCreditLimitCommand command = new CalculateCreditLimitCommand();
        command.setClientId(client.getId());
        command.setLastCreditLimit(oldCreditLimit);
        return creditLimitCalculator.calculateCreditLimit(command);
    }

}
