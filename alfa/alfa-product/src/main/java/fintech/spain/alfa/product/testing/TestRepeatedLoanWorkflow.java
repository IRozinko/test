package fintech.spain.alfa.product.testing;

import fintech.TimeMachine;
import fintech.instantor.model.SaveInstantorResponseCommand;
import fintech.nordigen.impl.NordigenResponse;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.APPROVE_LOAN_OFFER;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.BASIC_LENDING_RULES;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.COLLECT_BASIC_INFORMATION;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.ISSUE_LOAN;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.LOAN_OFFER_EMAIL;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.LOAN_OFFER_SMS;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.MANDATORY_LENDING_RULES;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PREPARE_OFFER;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PRESTO_CROSSCHECK;
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.PRESTO_CROSSCHECK_RULES;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestRepeatedLoanWorkflow extends TestWorkflow<TestRepeatedLoanWorkflow> {

    @Getter
    @Setter
    private SaveInstantorResponseCommand instantorResponse;

    @Getter
    @Setter
    private NordigenResponse nordigenResponse;

    public TestRepeatedLoanWorkflow(TestClient testClient, Long workflowId) {
        super(testClient, workflowId);
    }

    @Override
    protected void buildRunnables(List<ActivityRunnable> runnables) {
        runnables.add(systemActivityRunnable(COLLECT_BASIC_INFORMATION));
        runnables.add(systemActivityRunnable(MANDATORY_LENDING_RULES));
        runnables.add(systemActivityRunnable(BASIC_LENDING_RULES));
        runnables.add(systemActivityRunnable(PRESTO_CROSSCHECK));
        runnables.add(systemActivityRunnable(PRESTO_CROSSCHECK_RULES));
        runnables.add(systemActivityRunnable(PREPARE_OFFER));
        runnables.add(systemActivityRunnable(LOAN_OFFER_EMAIL));
        runnables.add(systemActivityRunnable(LOAN_OFFER_SMS));
        runnables.add(new ActivityRunnable(APPROVE_LOAN_OFFER, () -> {
            taskOfActivity(APPROVE_LOAN_OFFER).complete(UnderwritingTasks.LoanOfferCall.CLIENT_APPROVED_OFFER);
        }));
        runnables.add(systemActivityRunnable(ISSUE_LOAN));
    }

    public TestRepeatedLoanWorkflow exportDisbursement(LocalDate when) {
        toLoan().exportDisbursements(when);
        return this;
    }

    public TestRepeatedLoanWorkflow exportDisbursement() {
        return exportDisbursement(TimeMachine.today());
    }

}
