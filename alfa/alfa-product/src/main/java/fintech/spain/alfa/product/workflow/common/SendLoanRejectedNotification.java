package fintech.spain.alfa.product.workflow.common;

import com.google.common.collect.ImmutableList;
import fintech.crm.client.Client;
import fintech.crm.client.ClientSegment;
import fintech.crm.client.ClientService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.referral.ReferralLendingCompanySettings;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.workflow.spi.ActivityContext;
import fintech.workflow.spi.ActivityListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static fintech.spain.alfa.product.cms.CmsSetup.LOAN_REJECTED_NOTIFICATION_NO_LINK;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SendLoanRejectedNotification implements ActivityListener {

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private CmsContextFactory cmsContextFactory;

    @Autowired
    private ClientService clientService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    private final List<String> excludeClientsInSegments;

    public SendLoanRejectedNotification() {
        this(ImmutableList.of());
    }

    public SendLoanRejectedNotification(List<String> excludeClientsInSegments) {
        this.excludeClientsInSegments = excludeClientsInSegments;
    }

    @Override
    public void handle(ActivityContext context) {

        Client client = clientService.get(context.getClientId());
        boolean exclude = client.getSegments().stream().map(ClientSegment::getSegment).anyMatch(this.excludeClientsInSegments::contains);
        if (exclude) {
            return;
        }

        LoanApplication loanApplication = loanApplicationService.get(context.getWorkflow().getApplicationId());
        ReferralLendingCompanySettings settings = settingsService.getJson(AlfaSettings.REFERRAL_LENDING_COMPANY_SETTINGS, ReferralLendingCompanySettings.class);
        String cmsKey = settings.getExcludeTraffic().contains(loanApplication.getSourceName()) ? LOAN_REJECTED_NOTIFICATION_NO_LINK : CmsSetup.LOAN_REJECTED_NOTIFICATION;

        Map<String, Object> notificationContext = cmsContextFactory.getContext(context.getWorkflow());
        notificationFactory.fromCustomerService(context.getClientId())
            .loanApplicationId(context.getWorkflow().getApplicationId())
            .loanId(context.getWorkflow().getLoanId())
            .render(cmsKey, notificationContext)
            .send();
    }
}
