package fintech.spain.alfa.product.dc;

import fintech.ClasspathUtils;
import fintech.JsonUtils;
import fintech.dc.DcSettingsService;
import fintech.dc.DebtProcessorRegistry;
import fintech.dc.model.DcSettings;
import fintech.dc.parsers.MoneymanParser;
import fintech.dc.parsers.UniversalParser;
import fintech.dc.parsers.VivusParser;
import fintech.dc.spi.DcDefaults;
import fintech.dc.spi.DcRegistry;
import fintech.spain.alfa.product.dc.impl.company.MoneymanStrategy;
import fintech.spain.alfa.product.dc.impl.company.QueBuenoStrategy;
import fintech.spain.alfa.product.dc.impl.company.WandooStrategy;
import fintech.spain.alfa.product.dc.spi.*;
import fintech.spain.dc.handler.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DcSetup {

    private static final boolean OVERWRITE = false;

    private final DcSettingsService dcSettingsService;
    private final DcRegistry dcRegistry;
    private final DcDefaults dcDefaults;
    private final DebtProcessorRegistry debtProcessorRegistry;
    private final StrategyRegistry strategyRegistry;
    @Autowired
    public DcSetup(DcSettingsService dcSettingsService, DcRegistry dcRegistry, DcDefaults dcDefaults, DebtProcessorRegistry debtProcessorRegistry, StrategyRegistry strategyRegistry) {
        this.dcSettingsService = dcSettingsService;
        this.dcRegistry = dcRegistry;
        this.dcDefaults = dcDefaults;
        this.debtProcessorRegistry = debtProcessorRegistry;
        this.strategyRegistry = strategyRegistry;
    }

    public void setUp() {
        dcDefaults.init();

        dcRegistry.registerActionHandler("SendNotification", DcSendNotificationAction.class);
        dcRegistry.registerActionHandler("ShowPopup", DcShowPopupAction.class);
        dcRegistry.registerActionHandler("ExhaustPopup", DcExhaustPopupAction.class);
        dcRegistry.registerActionHandler("BreakLoan", BreakLoanAction.class);
        dcRegistry.registerActionHandler("BreakRescheduling", BreakReschedulingAction.class);
        dcRegistry.registerActionHandler("SetSuspendPenalties", SetSuspendPenaltiesAction.class);
        dcRegistry.registerActionHandler("ManageAsnef", ManageAsnefAction.class);
        dcRegistry.registerActionHandler("ManageClientCommunications", ManageClientCommunicationsAction.class);
        dcRegistry.registerActionHandler("ManageBlacklists", ManageBlacklistsAction.class);
        dcRegistry.registerActionHandler("ChangeLoanStatusDetail", ChangeLoanStatusDetailAction.class);
        dcRegistry.registerActionHandler("AssignToAgent", AutoAssignToAgentAction.class);

        dcRegistry.registerBulkActionHandler("SendSms", DcSendSmsBulkAction.class);
        dcRegistry.registerBulkActionHandler("SendEmail", DcSendEmailBulkAction.class);
        dcRegistry.registerBulkActionHandler("SendNotification", DcSendNotificationBulkAction.class);
        dcRegistry.registerBulkActionHandler("LogActivity", DcLogActivityBulkAction.class);
        dcRegistry.registerBulkActionHandler("BreakLoan", BreakLoanBulkAction.class);
        dcRegistry.registerBulkActionHandler("Reschedule", RescheduleBulkAction.class);
        dcRegistry.registerBulkActionHandler("ManualReschedule", RescheduleBulkAction.class);
        dcRegistry.registerBulkActionHandler("BreakRescheduling", BreakReschedulingBulkAction.class);
        dcRegistry.registerBulkActionHandler("ExhaustPopup", DcExhaustPopupBulkAction.class);
        dcRegistry.registerBulkActionHandler("ManageAsnef", ManageAsnefBulkAction.class);
        dcRegistry.registerBulkActionHandler("ManageClientCommunications", ManageClientCommunicationsBulkAction.class);
        dcRegistry.registerBulkActionHandler("ManageBlacklists", ManageBlacklistsBulkAction.class);
        dcRegistry.registerBulkActionHandler("ChangeLoanStatusDetail", ChangeLoanStatusDetailBulkAction.class);

        dcRegistry.registerConditionHandler("PenaltiesSuspended", PenaltiesSuspendedCondition.class);
        dcRegistry.registerConditionHandler("ExternalPortfolioBefore", ExternalPortfolioBeforeCondition.class);
        dcRegistry.registerConditionHandler("InstallmentStatusDetail", InstallmentStatusDetailCondition.class);
        dcRegistry.registerConditionHandler("AsnefStatus", AsnefStatusCondition.class);
        dcRegistry.registerConditionHandler("ClientCommunicationsStatus", ClientCommunicationsStatusCondition.class);
        dcRegistry.registerConditionHandler("BlacklistsStatus", BlacklistsStatusCondition.class);
        dcRegistry.registerConditionHandler("MostRecentDebt", MostRecentDebtCondition.class);
        dcRegistry.registerConditionHandler("ExtensionExceeded", ExtensionExceededCondition.class);
        dcRegistry.registerConditionHandler("ExtensionNotExceeded", ExtensionNotExceededCondition.class);

        debtProcessorRegistry.add(MoneymanParser.FORMAT_NAME, MoneymanParser.class);
        debtProcessorRegistry.add(VivusParser.FORMAT_NAME, VivusParser.class);
        debtProcessorRegistry.add(UniversalParser.FORMAT_NAME, UniversalParser.class);

        strategyRegistry.add(MoneymanStrategy.company, MoneymanStrategy.class);
        strategyRegistry.add(QueBuenoStrategy.company, QueBuenoStrategy.class);
        strategyRegistry.add(WandooStrategy.company, WandooStrategy.class);

        DcSettings settings = JsonUtils.readValue(ClasspathUtils.resourceToString("default-settings/dc-settings.json"), DcSettings.class);
        dcSettingsService.saveSettings(settings, OVERWRITE);
    }

}
