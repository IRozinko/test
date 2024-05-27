package fintech.spain.alfa.product.testing;

import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestApplication {

    private final TestClient client;
    private final Long applicationId;

    @Autowired
    private LoanApplicationService applicationService;

    public TestApplication(TestClient client, Long applicationId) {
        this.client = client;
        this.applicationId = applicationId;
    }

    public TestClient toClient() {
        return client;
    }

    public LoanApplication getApplication() {
        return applicationService.get(this.applicationId);
    }

    public boolean isRejected() {
        return LoanApplicationStatusDetail.REJECTED.equals(getApplication().getStatusDetail());
    }

    public String getCloseReason() {
        return getApplication().getCloseReason();
    }

    public String getStatusDetail() {
        return getApplication().getStatusDetail();
    }
}
