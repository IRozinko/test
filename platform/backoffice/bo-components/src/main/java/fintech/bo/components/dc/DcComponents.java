package fintech.bo.components.dc;

import com.google.common.collect.ImmutableMap;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import fintech.TimeMachine;
import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.dc.SaveAgentRequest;
import fintech.bo.api.model.loan.ExtensionPrice;
import fintech.bo.api.model.loan.GetExtensionPricesRequest;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.dc.DcSettingsJson.Companies;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.dialogs.InfoDialog;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.security.LoginService;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.db.jooq.dc.tables.records.ActionRecord;
import fintech.bo.db.jooq.dc.tables.records.AgentRecord;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static fintech.bo.db.jooq.dc.Tables.AGENT;
import static fintech.bo.db.jooq.dc.Tables.AGENT_PORTFOLIO;
import static java.time.temporal.ChronoUnit.DAYS;
import static org.apache.commons.lang3.StringUtils.lowerCase;

public abstract class DcComponents {

    private final DcQueries dcQueries;

    private final DcApiClient apiClient;

    private final DSLContext db;

    private final LoanQueries loanQueries;

    private final LoanApiClient loanApiClient;

    private Map<String, Supplier<BulkActionComponent>> bulkActions = Collections.synchronizedMap(new HashMap<>());

    protected DcComponents(DcQueries dcQueries, DcApiClient apiClient, DSLContext db, LoanQueries loanQueries, LoanApiClient loanApiClient) {
        this.dcQueries = dcQueries;
        this.apiClient = apiClient;
        this.db = db;
        this.loanQueries = loanQueries;
        this.loanApiClient = loanApiClient;
    }

    public PropertyLayout debtInfoSimple(DebtRecord debt) {
        PropertyLayout layout = new PropertyLayout("Debt");
        layout.addLink("Number", debt.getLoanNumber(), DcComponents.debtLink(debt.getId()));
        layout.add("Managing Company", debt.getManagingCompany());
        layout.add("Portfolio", String.format("%s (%s)", debt.getPortfolio(), debt.getStatus()));
        layout.add("Total due", debt.getTotalDue());
        layout.add("DPD", debt.getDpd());
        layout.add("Last action", debt.getLastAction());
        layout.add("Next action", debt.getNextAction());
        return layout;
    }

    public abstract PropertyLayout debtInfo(DebtRecord debt);

    public PropertyLayout extensionOffers(DebtRecord debt) {
        PropertyLayout layout = new PropertyLayout("Extension offers");
        LocalDate simulationDate = debt.getPaymentDueDate();
        BackgroundOperations.callApiSilent(loanApiClient.getExtensionPrices(
            new GetExtensionPricesRequest(debt.getLoanId(), simulationDate)), extensionPricesResponse -> {

            if (extensionPricesResponse.getExtensions().isEmpty()) {
                layout.addWarning("No extension offers available");
            }

            extensionPricesResponse.getExtensions().forEach(
                extension -> {
                    String label = String.format("%s %ss", extension.getPeriodCount(), lowerCase(extension.getPeriodUnit()));
                    if (isNotOverdueAfterExtension(debt, extension)) {
                        layout.add(label, extension.getPrice());
                    } else {
                        layout.addWarning(label, extension.getPrice());
                    }
                });
        }, Notifications::errorNotification);
        return layout;
    }

    public PropertyLayout actionInfo(ActionRecord action) {
        PropertyLayout layout = new PropertyLayout("Action");
        layout.add("Created at", action.getCreatedAt());
        layout.add("Action", action.getActionName());
        layout.add("Agent", action.getAgent());
        layout.addTextArea("Comments", action.getComments());
        layout.addSpacer();
        layout.add("Status before", action.getDebtStatusBefore());
        layout.add("Status after", action.getDebtStatus());
        layout.addSpacer();
        layout.add("Portfolio before", action.getPortfolioBefore());
        layout.add("Portfolio after", action.getPortfolio());
        layout.addSpacer();
        layout.add("Next action", action.getNextAction());
        layout.add("Next action date", action.getNextActionAt());
        layout.addSpacer();
        layout.add("Promise date", action.getPromiseDueDate());
        layout.add("Promise amount", action.getPromiseAmount());
        layout.addSpacer();
        layout.add("Assigned to", action.getAssignedToAgent());
        return layout;
    }

    public InfoDialog actionInfoDialog(Long actionId) {
        ActionRecord action = dcQueries.findActionById(actionId);
        PropertyLayout info = actionInfo(action);
        VerticalLayout wrapper = new VerticalLayout(info);
        wrapper.setMargin(false);
        InfoDialog dialog = new InfoDialog(action.getActionName(), wrapper);
        dialog.setHeight(800, Sizeable.Unit.PIXELS);
        dialog.setWidth(600, Sizeable.Unit.PIXELS);
        dialog.setModal(true);
        return dialog;
    }

    public ActionDialog editDebtStateAndStatus(Long debtId) {
        DebtRecord debt = dcQueries.findDebtById(debtId);
        return new DebtEditStateAndStatusDialog(debt, apiClient);
    }

    public ComboBox<String> agentsComboBox() {
        return agentsComboBox("All");
    }

    public ComboBox<String> agentsComboBox(String emptySelectionCaption) {
        List<String> items = new ArrayList<>(dcQueries.listDebtAgents());
        String me = LoginService.getLoginData().getUser();
        items.remove(me);
        items.remove(DcConstants.UNASSIGNED_AGENT);

        items.add(0, me);
        items.add(1, DcConstants.UNASSIGNED_AGENT);

        ComboBox<String> comboBox = new ComboBox<>("Agent");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionCaption(emptySelectionCaption);
        comboBox.setPageLength(20);
        return comboBox;
    }

    public ComboBox<String> portfoliosComboBox() {
        DcSettingsJson settings = dcQueries.getSettings();
        List<String> items = settings.getPortfolios().stream().map(DcSettingsJson.Portfolio::getName).collect(Collectors.toList());

        ComboBox<String> comboBox = new ComboBox<>("Debt Portfolio");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionCaption("All");
        comboBox.setPageLength(20);
        return comboBox;
    }

    public List<String> actionsForPortfolio(String portfolio) {
        if (StringUtils.isBlank(portfolio))
            return Collections.emptyList();

        DcSettingsJson settings = dcQueries.getSettings();
        return settings.getOptionalPortfolio(portfolio).map(DcSettingsJson.Portfolio::getAgentActions)
            .orElse(Collections.emptyList())
            .stream()
            .map(DcSettingsJson.AgentAction::getTemplate)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public ComboBox<String> agingComboBox() {
        DcSettingsJson settings = dcQueries.getSettings();
        List<String> items = settings.getAgingBuckets().stream().map(DcSettingsJson.AgingBucket::getName).collect(Collectors.toList());

        ComboBox<String> comboBox = new ComboBox<>("Aging");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionCaption("All");
        comboBox.setPageLength(20);
        return comboBox;
    }

    public ComboBox<String> statusesComboBox() {
        List<String> items = new ArrayList<>(dcQueries.listStatuses());
        ComboBox<String> comboBox = new ComboBox<>("Debt Status");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setEmptySelectionCaption("All");
        comboBox.setEmptySelectionAllowed(true);
        comboBox.setPageLength(20);
        return comboBox;
    }

    public ComboBox<String> subStatusesComboBox() {
        List<String> items = new ArrayList<>(dcQueries.listSubStatuses());
        ComboBox<String> comboBox = new ComboBox<>("Debt Sub Status");
        comboBox.setEmptySelectionCaption("All");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(20);
        return comboBox;
    }

    public ComboBox<String> managingCompaniesComboBox() {
        Companies companies = dcQueries.getSettings().getCompanies();
        List<String> items = companies.getManagingCompanies();
        Optional.ofNullable(companies.getDefaultManagingCompany()).ifPresent(
            defaultManagingCompany -> items.add(0, defaultManagingCompany)
        );

        ComboBox<String> comboBox = new ComboBox<>("Managing Companies");
        comboBox.setEmptySelectionCaption("All");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(20);
        return comboBox;
    }

    public ComboBox<String> owningCompaniesComboBox() {
        Companies companies = dcQueries.getSettings().getCompanies();
        List<String> items = companies.getOwningCompanies();
        Optional.ofNullable(companies.getDefaultOwningCompany()).ifPresent(
            defaultOwningCompany -> items.add(0, defaultOwningCompany)
        );

        ComboBox<String> comboBox = new ComboBox<>("Owning Companies");
        comboBox.setEmptySelectionCaption("All");
        comboBox.setItems(items);
        comboBox.setTextInputAllowed(false);
        comboBox.setPageLength(20);
        return comboBox;
    }

    public static String debtLink(Long debtId) {
        return "debt/" + debtId;
    }

    public static String debtLinkWithBackNavigation(Long debtId, String navigateTo) {
        return String.format("debt/%d?%s=%s", debtId, UrlUtils.NAVIGATE_TO, navigateTo);
    }

    public void registerBulkAction(String name, Supplier<BulkActionComponent> component) {
        this.bulkActions.put(name, component);
    }

    public Map<String, Supplier<BulkActionComponent>> getBulkActions() {
        return ImmutableMap.copyOf(bulkActions);
    }

    public EditAgentDialog addAgent() {
        EditAgentDialog dialog = new EditAgentDialog(apiClient, dcQueries, new SaveAgentRequest());
        return dialog;
    }

    public EditAgentDialog editAgent(String name) {
        AgentRecord agent = db.selectFrom(AGENT).where(AGENT.AGENT_.eq(name)).fetchOne();
        List<String> portfolios = db.select(AGENT_PORTFOLIO.PORTFOLIOS).from(AGENT_PORTFOLIO).where(AGENT_PORTFOLIO.AGENT_ID.eq(agent.getId())).fetchInto(String.class);

        SaveAgentRequest request = new SaveAgentRequest();
        request.setAgent(agent.getAgent());
        request.setDisabled(agent.getDisabled());
        request.setPortfolios(new LinkedHashSet<>(portfolios));

        EditAgentDialog dialog = new EditAgentDialog(apiClient, dcQueries, request);
        return dialog;
    }

    private boolean isNotOverdueAfterExtension(DebtRecord debt, ExtensionPrice price) {
        LoanRecord loan = loanQueries.findById(debt.getLoanId());
        LocalDate newMaturityDate = loan.getMaturityDate().plus(price.getPeriodCount(), DAYS);
        return !newMaturityDate.isBefore(TimeMachine.today());
    }

    public LoanQueries getLoanQueries() {
        return loanQueries;
    }

    public ComboBox<String> loanStatusDetailComboBox() {
        ComboBox<String> comboBox = new ComboBox<>("Loan Status Detail");
        comboBox.setEmptySelectionCaption("All");
        comboBox.setItems(loanQueries.findStatuses());
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }
}
