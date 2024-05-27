package fintech.spain.alfa.product.testing;

import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.registration.forms.AffiliateData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class TestFactory {

    private static ApplicationContext applicationContext;

    @Autowired
    public TestFactory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public static TestClient existingClient(long clientId) {
        return applicationContext.getBean(TestClient.class, clientId);
    }

    public static TestClient newClient() {
        return applicationContext.getBean(TestClient.class);
    }

    public static TestClient newAffiliateClient() {
        TestClient client = applicationContext.getBean(TestClient.class);
        client.getSignUpForm().setAffiliate(new AffiliateData()
            .setAffiliateName(AlfaConstants.TEST_AFFILIATE_NAME)
            .setAffiliateLeadId("1"));
        return client;
    }


    public static TestLoan loan(TestClient client, Long loanId) {
        return applicationContext.getBean(TestLoan.class, client, loanId);
    }

    public static PaymentHelper payments() {
        return applicationContext.getBean(PaymentHelper.class);
    }

    public static TestUpsellWorkflow upsellWorkflow(TestClient client, Long workflowId) {
        return applicationContext.getBean(TestUpsellWorkflow.class, client, workflowId);
    }

    public static TestChangeBankAccountWorkflow changeBankAccountWorkflow(TestClient client, Long workflowId) {
        return applicationContext.getBean(TestChangeBankAccountWorkflow.class, client, workflowId);
    }

    public static <T> T workflow(Class<T> wfClass, TestClient client, Long applicationId) {
        return applicationContext.getBean(wfClass, client, applicationId);
    }

    public static TestApplication application(TestClient client, Long applicationId) {
        return applicationContext.getBean(TestApplication.class, client, applicationId);
    }

    public static TestTask task(Long taskId) {
        return applicationContext.getBean(TestTask.class, taskId);
    }
}
