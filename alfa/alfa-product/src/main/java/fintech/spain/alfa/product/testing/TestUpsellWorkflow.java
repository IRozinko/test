package fintech.spain.alfa.product.testing;

import fintech.TimeMachine;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingTasks;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.CREDIT_LIMIT;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.GENERATE_AGREEMENT;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.PREPARE_OFFER;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL;
import static fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow.Activities.UPSELL_OFFER_CALL;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestUpsellWorkflow extends TestWorkflow<TestUpsellWorkflow> {

    public TestUpsellWorkflow(TestClient client, Long workflowId) {
        super(client, workflowId);
    }

    @Override
    protected void buildRunnables(List<ActivityRunnable> runnables) {
        runnables.add(systemActivityRunnable(CREDIT_LIMIT));
        runnables.add(systemActivityRunnable(PREPARE_OFFER));
        runnables.add(new ActivityRunnable(UPSELL_OFFER_CALL, () -> taskOfActivity(UPSELL_OFFER_CALL).complete(UnderwritingTasks.UpsellOfferCall.CLIENT_ACCEPTED)));
        runnables.add(systemActivityRunnable(GENERATE_AGREEMENT));
        runnables.add(systemActivityRunnable(UPSELL));
    }

    public TestUpsellWorkflow exportDisbursement() {
        return exportDisbursement(TimeMachine.today());
    }

    public TestUpsellWorkflow exportDisbursement(LocalDate when) {
        toLoan().exportDisbursements(when);
        return this;
    }
}
