package fintech.spain.alfa.product.testing;

import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow.Activities.*;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestChangeBankAccountWorkflow extends TestWorkflow<TestChangeBankAccountWorkflow> {

    public TestChangeBankAccountWorkflow(TestClient client, Long workflowId) {
        super(client, workflowId);
    }

    @Override
    protected void buildRunnables(List<ActivityRunnable> runnables) {
        runnables.add(systemActivityRunnable(CHANGE_BANK_ACCOUNT_LINK));
        runnables.add(systemActivityRunnable(CHANGE_BANK_ACCOUNT_VERIFY));
        runnables.add(systemActivityRunnable(CHANGE_BANK_ACCOUNT_INSTANTOR_CALLBACK));
        runnables.add(systemActivityRunnable(CHANGE_BANK_ACCOUNT_CHOICE));
    }
}
