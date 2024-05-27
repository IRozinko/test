package fintech.bo.spain.alfa;

import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
import com.vaadin.spring.annotation.SpringUI;
import com.vaadin.ui.MenuBar;
import fintech.bo.api.model.product.ProductType;
import fintech.bo.components.AbstractBackofficeUI;
import fintech.bo.components.HomeView;
import fintech.bo.components.MenuHelper;
import fintech.bo.components.accounting.AccountingView;
import fintech.bo.components.admintools.AdminToolsView;
import fintech.bo.components.admintools.DemoScenariosView;
import fintech.bo.components.affiliate.AffiliateEventsView;
import fintech.bo.components.affiliate.AffiliatePartnersView;
import fintech.bo.components.agents.AgentsView;
import fintech.bo.components.callcenter.CallCenterView;
import fintech.bo.components.cms.CmsItemsView;
import fintech.bo.components.dc.DcAgentsView;
import fintech.bo.components.dc.DcSettingsView;
import fintech.bo.components.dc.DcWorkqueuesView;
import fintech.bo.components.dc.DebtUploadView;
import fintech.bo.components.dowjones.DowJonesMatchResultView;
import fintech.bo.components.dowjones.DowJonesSearchResultView;
import fintech.bo.components.dowjones.DowJonesView;
import fintech.bo.components.emails.EmailsView;
import fintech.bo.components.instantor.InstantorView;
import fintech.bo.components.institution.InstitutionsView;
import fintech.bo.components.invoice.InvoicesView;
import fintech.bo.components.iovation.IovationBlackBoxesView;
import fintech.bo.components.iovation.IovationTransactionsView;
import fintech.bo.components.loan.discounts.DiscountsView;
import fintech.bo.components.loan.promocodes.PromoCodesView;
import fintech.bo.components.nordigen.NordigenView;
import fintech.bo.components.payments.PaymentsView;
import fintech.bo.components.payments.disbursement.DisbursementExportView;
import fintech.bo.components.payments.disbursement.DisbursementsView;
import fintech.bo.components.payments.statement.StatementUploadView;
import fintech.bo.components.payments.statement.StatementsView;
import fintech.bo.components.period.PeriodsView;
import fintech.bo.components.product.ProductsView;
import fintech.bo.components.quartz.SchedulersView;
import fintech.bo.components.risk.checklist.ChecklistView;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.settings.NewSettingsView;
import fintech.bo.components.settings.SettingsView;
import fintech.bo.components.sms.IncomingSmsView;
import fintech.bo.components.sms.OutgoingSmsView;
import fintech.bo.components.task.TasksView;
import fintech.bo.components.transaction.TransactionsView;
import fintech.bo.components.users.RolesView;
import fintech.bo.components.users.UsersView;
import fintech.bo.components.webanalytics.WebAnalyticsEventsView;
import fintech.bo.db.jooq.marketing.tables.records.MarketingCampaignRecord;
import fintech.bo.spain.asnef.AsnefView;
import fintech.bo.spain.equifax.EquifaxView;
import fintech.bo.spain.experian.ExperianCaisOperacionesView;
import fintech.bo.spain.experian.ExperianCaisResumenView;
import fintech.bo.spain.scoring.SpainScoringLogView;
import fintech.bo.spain.alfa.activity.ActivitySetup;
import fintech.bo.spain.alfa.address.AddressCatalogView;
import fintech.bo.spain.alfa.applications.LoanApplicationsView;
import fintech.bo.spain.alfa.client.ClientsView;
import fintech.bo.spain.alfa.dc.BatchDebtActionsView;
import fintech.bo.spain.alfa.dc.DcPaymentsView;
import fintech.bo.spain.alfa.dc.DcSetup;
import fintech.bo.spain.alfa.dc.DebtsView;
import fintech.bo.spain.alfa.de.DecisionEngineView;
import fintech.bo.spain.alfa.loan.LoansView;
import fintech.bo.spain.alfa.loan.ReschedulingLoansView;
import fintech.bo.spain.alfa.loc.LocBatchView;
import fintech.bo.spain.alfa.payment.PaymentSetup;
import fintech.bo.spain.alfa.task.TaskSetup;
import fintech.bo.spain.alfa.viventor.ViventorLoansView;
import fintech.bo.spain.alfa.viventor.ViventorLogView;
import fintech.bo.spain.alfa.workflow.NotificationsActivityListenersView;
import fintech.bo.spain.unnax.UnnaxView;
import fintech.marketing.bo.*;
import fintech.strategy.bo.StrategiesView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static fintech.bo.api.model.permissions.BackofficePermissions.ADMIN;
import static fintech.bo.api.model.permissions.BackofficePermissions.MARKETING_MANAGER;

@Slf4j
@SpringUI
@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET)
@Theme("backoffice")
@Title("Backoffice : Alfa")
public class AlfaBackofficeUI extends AbstractBackofficeUI {

    @Autowired
    private PaymentSetup paymentSetup;

    @Autowired
    private TaskSetup taskSetup;

    @Autowired
    private DcSetup dcSetup;

    @Autowired
    private ActivitySetup activitySetup;

    @Autowired
    private MarketingApiClient marketingApiClient;

    public AlfaBackofficeUI() {
        super(ProductType.PAYDAY, "Alfa");
    }

    @Override
    protected void setup() {
//        taskSetup.init();
        paymentSetup.init();
        dcSetup.init();
        activitySetup.init();
    }

    @Override
    protected MenuBar buildMainMenu() {
        List<String> permissions = LoginService.getUserPermissions();
        MenuBar menuBar = new MenuBar();

        MenuHelper.addIfAllowed(menuBar, "Alfa", HomeView.class, permissions);
        MenuHelper.addIfAllowed(menuBar, "Clients", ClientsView.class, permissions);

//        MenuBar.MenuItem lending = menuBar.addItem("Lending", null);
//        MenuHelper.addIfAllowed(lending, "Loan Applications", LoanApplicationsView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Loans", LoansView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Discounts", DiscountsView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Promo codes", PromoCodesView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Invoices", InvoicesView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Tasks", TasksView.class, permissions);
//        MenuHelper.addIfAllowed(lending, "Notifications", NotificationsActivityListenersView.class, permissions);
//        lending.setVisible(lending.hasChildren());

        MenuBar.MenuItem dc = menuBar.addItem("DC", null);
        MenuHelper.addIfAllowed(dc, "Work Queue", DcWorkqueuesView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Debts", DebtsView.class, permissions);
        MenuBar.MenuItem importDebts = menuBar.addItem("Import Debts");
        MenuHelper.addIfAllowed(importDebts, "Debts import", DebtUploadView.class, permissions);
        MenuHelper.addIfAllowed(importDebts, "Documents import", DebtUploadView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Batch actions", BatchDebtActionsView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Rescheduled Loans", ReschedulingLoansView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Payments", DcPaymentsView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Agents", DcAgentsView.class, permissions);
        MenuHelper.addIfAllowed(dc, "Settings", DcSettingsView.class, permissions);
//        dc.setVisible(lending.hasChildren());

        MenuBar.MenuItem finance = menuBar.addItem("Finance", null);
//        MenuHelper.addIfAllowed(finance, "Accounting", AccountingView.class, permissions);
//        MenuHelper.addIfAllowed(finance, "Disbursement export", DisbursementExportView.class, permissions);
//        MenuHelper.addIfAllowed(finance, "Disbursements", DisbursementsView.class, permissions);
        MenuHelper.addIfAllowed(finance, "Payments", PaymentsView.class, permissions);
//        MenuHelper.addIfAllowed(finance, "Periods", PeriodsView.class, permissions);
        MenuBar.MenuItem importStatements = menuBar.addItem("Import Payments");
        MenuHelper.addIfAllowed(importStatements, "Statement upload", StatementUploadView.class, permissions);
        MenuHelper.addIfAllowed(importStatements, "Confirmation upload", StatementUploadView.class, permissions);
        MenuHelper.addIfAllowed(finance, "Statements", StatementsView.class, permissions);
//        MenuHelper.addIfAllowed(finance, "Transactions", TransactionsView.class, permissions);
        finance.setVisible(finance.hasChildren());

//        MenuBar.MenuItem integrations = menuBar.addItem("Integrations", null);
//        MenuBar.MenuItem affiliates = integrations.addItem("Affiliate", null);
//        MenuHelper.addIfAllowed(affiliates, "Partners", AffiliatePartnersView.class, permissions);
//        MenuHelper.addIfAllowed(affiliates, "Events", AffiliateEventsView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Asnef", AsnefView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Call Center", CallCenterView.class, permissions);
//        MenuBar.MenuItem dowJones = integrations.addItem("DowJones", null);
//        MenuHelper.addIfAllowed(dowJones, "Responses", DowJonesView.class, permissions);
//        MenuHelper.addIfAllowed(dowJones, "Search Results", DowJonesSearchResultView.class, permissions);
//        MenuHelper.addIfAllowed(dowJones, "Match Results", DowJonesMatchResultView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Email", EmailsView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Equifax", EquifaxView.class, permissions);
//        MenuBar.MenuItem experian = integrations.addItem("Experian", null);
//        MenuHelper.addIfAllowed(experian, "Cais Resumen", ExperianCaisResumenView.class, permissions);
//        MenuHelper.addIfAllowed(experian, "Cais Operaciones", ExperianCaisOperacionesView.class, permissions);
//        MenuBar.MenuItem sms = integrations.addItem("SMS", null);
//        MenuHelper.addIfAllowed(sms, "Outgoing", OutgoingSmsView.class, permissions);
//        MenuHelper.addIfAllowed(sms, "Incoming", IncomingSmsView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Instantor", InstantorView.class, permissions);
//        MenuBar.MenuItem iovation = integrations.addItem("Iovation", null);
//        MenuHelper.addIfAllowed(iovation, "BlackBoxes", IovationBlackBoxesView.class, permissions);
//        MenuHelper.addIfAllowed(iovation, "Transactions", IovationTransactionsView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Nordigen", NordigenView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Decision Engine", DecisionEngineView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Scoring", SpainScoringLogView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Unnax", UnnaxView.class, permissions);
//        MenuBar.MenuItem viventor = integrations.addItem("Viventor", null);
//        MenuHelper.addIfAllowed(viventor, "Loans", ViventorLoansView.class, permissions);
//        MenuHelper.addIfAllowed(viventor, "Log", ViventorLogView.class, permissions);
//        MenuHelper.addIfAllowed(integrations, "Web events", WebAnalyticsEventsView.class, permissions);
//        integrations.setVisible(integrations.hasChildren());

//        MenuBar.MenuItem marketingMenu = menuBar.addItem("Marketing", null);
//
//        if (permissions.contains(MARKETING_MANAGER) || permissions.contains(ADMIN)) {
//            MenuHelper.addIfAllowed(marketingMenu, "Overview", MarketingCommunicationView.class, permissions);
//
//            marketingMenu.addItem("New campaign", e -> {
//                EditMarketingCampaignDialog dialog = new EditMarketingCampaignDialog( new MarketingCampaignRecord());
//                getUI().addWindow(dialog);
//            });
//
//            MenuHelper.addIfAllowed(marketingMenu, "Manage automated campaigns", MarketingAutomatedCampaignView.class, permissions);
//            MenuHelper.addIfAllowed(marketingMenu, "Manage campaigns", MarketingOneTimeCampaignView.class, permissions);
//            MenuHelper.addIfAllowed(marketingMenu, "Template editor", MarketingTemplatesView.class, permissions);
//            marketingMenu.addItem("Settings", e -> {
//                EditSettingsDialog dialog = new EditSettingsDialog(marketingApiClient);
//                getUI().addWindow(dialog);
//            });
//        }

        MenuBar.MenuItem adminMenu = menuBar.addItem("Admin", null);
//        MenuHelper.addIfAllowed(adminMenu, "Address Catalog", AddressCatalogView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Agents", AgentsView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Checklists", ChecklistView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "CMS", CmsItemsView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Demo", DemoScenariosView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Institutions", InstitutionsView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Loc Batch", LocBatchView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "New Settings [Beta]", NewSettingsView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Products", ProductsView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Roles", RolesView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Schedulers", SchedulersView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Settings", SettingsView.class, permissions);
//        MenuHelper.addIfAllowed(adminMenu, "Strategies", StrategiesView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Tools", AdminToolsView.class, permissions);
        MenuHelper.addIfAllowed(adminMenu, "Users", UsersView.class, permissions);
        adminMenu.setVisible(adminMenu.hasChildren());

        return menuBar;
    }
}
