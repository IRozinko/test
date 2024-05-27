package fintech.spain.alfa.product;

import fintech.marketing.MarketingRegistry;
import fintech.payments.settigs.PaymentsSettingsService;
import fintech.presence.PresenceSetup;
import fintech.spain.alfa.product.accounting.Accounts;
import fintech.spain.alfa.product.activity.ActivitySetup;
import fintech.spain.alfa.product.affiliate.AffiliatesSetup;
import fintech.spain.alfa.product.lending.LendingSetup;
import fintech.spain.alfa.product.payments.PaymentsSetup;
import fintech.spain.alfa.product.settings.CountrySetup;
import fintech.spain.alfa.product.settings.LocSettings;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.alfa.product.strategy.StrategySetup;
//import fintech.spain.alfa.product.workflow.WorkflowSetup;
import fintech.spain.callcenter.dc.CallCenterDcSetup;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.crm.CrmSetup;
import fintech.spain.alfa.product.dc.DcSetup;
import fintech.spain.alfa.product.risk.RiskSetup;
import fintech.spain.alfa.product.tasks.StandaloneTasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
@DependsOn("flyway.alfa")
public class CrmAlfaSetup {

    @Autowired
    private AlfaProductSetup productSetup;

    @Autowired
    private PaymentsSettingsService paymentsSettingsService;

    @Autowired
    private LendingSetup lendingSetup;

    @Autowired
    private PaymentsSetup paymentsSetup;

    @Autowired
    private AlfaSettings settings;

    @Autowired
    private LocSettings locSettings;

    @Autowired
    private CmsSetup cmsSetup;

    @Autowired
    private RiskSetup riskSetup;

//    @Autowired
//    private WorkflowSetup workflowSetup;

    @Autowired
    private CrmSetup crmSetup;

    @Autowired
    private DcSetup dcSetup;

    @Autowired
    private AffiliatesSetup affiliatesSetup;

    @Autowired
    private ActivitySetup activitySetup;

    @Autowired
    private Accounts accounts;

    @Autowired
    private StandaloneTasks standaloneTasks;

    @Autowired
    private CountrySetup countrySetup;

    @Autowired
    private PresenceSetup presenceSetup;

    @Autowired
    private CallCenterDcSetup callCenterDcSetup;

    @Autowired
    private StrategySetup strategySetup;

    @Autowired
    private MarketingRegistry marketingRegistry;

    public void setUp() {
        paymentsSettingsService.setup();
        settings.setUp();
        productSetup.init();
        locSettings.setUp();
        lendingSetup.setUp();
        paymentsSetup.setUp();
        riskSetup.setUp();
        cmsSetup.setUp();
        crmSetup.setUp();
//        workflowSetup.setUp();
        dcSetup.setUp();
        affiliatesSetup.setUp();
        activitySetup.setUp();
        accounts.init();
        standaloneTasks.init();
        countrySetup.setUp();
        presenceSetup.setup();
        callCenterDcSetup.init();
        strategySetup.setUp();
        marketingRegistry.setUp();
    }
}
