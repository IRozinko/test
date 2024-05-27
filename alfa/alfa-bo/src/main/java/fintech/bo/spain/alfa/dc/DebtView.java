package fintech.bo.spain.alfa.dc;

import com.google.common.collect.ImmutableList;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Component;
import fintech.TimeMachine;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.loan.ExtensionPrice;
import fintech.bo.api.model.loan.GetExtensionPricesRequest;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.TabHelper;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dc.AbstractDebtView;
import fintech.bo.components.dc.DcSettingsJson;
import fintech.bo.components.emails.EmailLogTab;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.sms.SmsLogTab;
import fintech.bo.components.views.BoComponent;
import fintech.bo.components.views.BoComponentContext;
import fintech.bo.components.views.StandardScopes;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import fintech.bo.spain.asnef.AsnefComponents;
import fintech.bo.spain.alfa.dc.action.RepurchaseAction;
import fintech.bo.spain.alfa.loan.PaymentSimulationComponentProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.function.Consumer;

import static fintech.bo.components.dc.DcConstants.PORTFOLIO_SOLD;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.lang3.StringUtils.lowerCase;

@SpringView(name = AbstractDebtView.NAME)
public class DebtView extends AbstractDebtView {

    @Autowired
    private PaymentSimulationComponentProvider paymentSimulation;

    @Autowired
    private AsnefComponents asnefComponents;

    @Autowired
    private RepurchaseAction repurchaseAction;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private LoanApiClient loanApiClient;

    @Autowired
    private LoanQueries loanQueries;

    public DebtView(AlfaDcComponents dcComponents) {
        super(dcComponents);
    }

    @Override
    protected void addCustomActions(BusinessObjectLayout layout) {
        if (PORTFOLIO_SOLD.equals(debt.getPortfolio()))
            layout.addActionMenuItem("Repurchase", e -> repurchaseAction.repurchase(
                ImmutableList.of(debt), errors -> refresh(), showException())
            );
    }

    @Override
    protected void buildTabs(BusinessObjectLayout layout) {
        ClientDTO client = clientRepository.getRequired(debt.getClientId());
        layout.addTab("New action", this::newActionTab);
//        layout.addTab("Payment simulation", this::paymentSimulation);
        layout.addTab("Action History", this::actionsTab);
        layout.addTab("Schedule", this::schedule);
        layout.addTab("Payments", this::payments);
        layout.addTab("Attachments", this::attachments);
        layout.addTab("Addresses", this::addresses);
        TabHelper.addIfAllowed(layout, new SmsLogTab("SMS Log", client, smsComponents));
        TabHelper.addIfAllowed(layout, new EmailLogTab("Email Log", client, emailsComponents));
        layout.addTab("Transactions", this::transactions);
//        layout.addTab("Asnef", () -> asnefComponents.asnefTab(debt.getClientId()));
    }

    @Override
    protected void buildLeft(BusinessObjectLayout layout) {
        layout.addLeftComponent(clientComponents.clientInfoSimple(debt.getClientId(), true));
//        layout.addLeftComponent(extensionOffers(debt));
        layout.addLeftComponent(dcComponents.debtInfo(debt));
    }

    private Component paymentSimulation() {
        BoComponent simulation = paymentSimulation.build(new BoComponentContext().withScope(StandardScopes.SCOPE_LOAN, this.debt.getLoanId()));
        simulation.refresh();
        return simulation;
    }

    private Consumer<Exception> showException() {
        return exception -> {
            Notifications.errorNotification(exception);
            refresh();
        };
    }

    private PropertyLayout extensionOffers(DebtRecord debt) {
        PropertyLayout layout = new PropertyLayout("Extension offers");
        LocalDate simulationDate = debt.getPaymentDueDate();
        DcSettingsJson.ExtensionSettings settings = dcQueries.getSettings().getExtensionSettings();
        LoanRecord loanRecord = loanQueries.findById(debt.getLoanId());
        int extensionDays = loanRecord.get(LOAN.EXTENDED_BY_DAYS);
        BackgroundOperations.callApiSilent(loanApiClient.getExtensionPrices(
            new GetExtensionPricesRequest(debt.getLoanId(), simulationDate)), extensionPricesResponse -> {
            if (extensionPricesResponse.getExtensions().isEmpty()) {
                layout.addWarning("No extension offers available");
            }
            extensionPricesResponse.getExtensions().forEach(
                extension -> {
                    String label = String.format("%s %s", extension.getPeriodCount(), lowerCase(extension.getPeriodUnit()));
                    if (extensionDays + extension.getPeriodCount() <= settings.getMaxPeriodDays() && isNotOverdueAfterExtension(debt, extension)) {
                        layout.add(label, extension.getPrice()).addStyleName(BackofficeTheme.TEXT_SUCCESS);
                    } else {
                        layout.addWarning(label, extension.getPrice());
                    }
                });
        }, Notifications::errorNotification);
        return layout;
    }

    private boolean isNotOverdueAfterExtension(DebtRecord debt, ExtensionPrice price) {
        LoanRecord loan = loanQueries.findById(debt.getLoanId());
        LocalDate newMaturityDate = loan.getMaturityDate().plus(price.getPeriodCount(), DAYS);
        return !newMaturityDate.isBefore(TimeMachine.today());
    }
}
