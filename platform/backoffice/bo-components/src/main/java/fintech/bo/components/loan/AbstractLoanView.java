package fintech.bo.components.loan;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.client.InvoiceApiClient;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.loan.UseOverpaymentRequest;
import fintech.bo.api.model.loan.VoidLoanRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.components.accounting.AccountingComponents;
import fintech.bo.components.accounting.AccountingEntryDataProvider;
import fintech.bo.components.accounting.AccountingTrialBalanceProvider;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.components.application.LoanApplicationQueries;
import fintech.bo.components.application.info.ApplicationInfo;
import fintech.bo.components.background.BackgroundOperations;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.ClientQueries;
import fintech.bo.components.client.dto.ClientDTO;
import fintech.bo.components.client.repository.ClientRepository;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.components.dialogs.Dialogs;
import fintech.bo.components.invoice.InvoiceComponents;
import fintech.bo.components.invoice.InvoiceDataProvider;
import fintech.bo.components.layouts.BusinessObjectLayout;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.disbursement.DisbursementComponents;
import fintech.bo.components.payments.disbursement.DisbursementDataProvider;
import fintech.bo.components.transaction.PaymentTransactionDataProvider;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.transaction.TransactionDataProvider;
import fintech.bo.components.utils.UrlUtils;
import fintech.bo.components.workflow.WorkflowComponents;
import fintech.bo.components.workflow.WorkflowDataProvider;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import retrofit2.Call;

import java.util.Objects;
import java.util.Optional;

import static fintech.BigDecimalUtils.isPositive;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_ACTIVE;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_BROKEN;
import static fintech.bo.components.security.LoginService.hasPermission;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

@Slf4j
public abstract class AbstractLoanView extends VerticalLayout implements View {

    public static final String NAME = "loan";

    protected final LoanComponents loanComponents;

    @Autowired
    protected DSLContext db;

    @Autowired
    protected ClientComponents clientComponents;

    @Autowired
    protected TransactionComponents transactionComponents;

    @Autowired
    protected InvoiceComponents invoiceComponents;

    @Autowired
    protected LoanApiClient loanApiClient;

    @Autowired
    protected WorkflowComponents workflowComponents;

    @Autowired
    protected ClientQueries clientQueries;

    @Autowired
    protected LoanQueries loanQueries;

    @Autowired
    protected LoanApplicationComponents loanApplicationComponents;

    @Autowired
    protected LoanApplicationQueries loanApplicationQueries;

    @Autowired
    protected InvoiceApiClient invoiceApiClient;

    @Autowired
    protected AccountingComponents accountingComponents;

    @Autowired
    protected DisbursementComponents disbursementComponents;

    @Autowired
    protected FileApiClient fileApiClient;

    @Autowired
    protected ClientRepository clientRepository;

    @Getter
    protected long loanId;

    @Getter
    protected LoanRecord loan;

    protected AbstractLoanView(LoanComponents loanComponents) {
        this.loanComponents = Objects.requireNonNull(loanComponents);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        String idParam = UrlUtils.getParam(event.getParameters(), UrlUtils.ID);
        if (!StringUtils.isNumeric(idParam)) {
            String phoneNumber = UrlUtils.getParam(event.getParameters(), UrlUtils.TEL);
            if (phoneNumber != null) {
                Optional<Long> maybeLoanId = loanQueries.findLoanIdByPhone(phoneNumber);
                if (maybeLoanId.isPresent()) {
                    loanId = maybeLoanId.get();
                    refresh();
                } else {
                    Notifications.errorNotification("Loan not found", "Phone= " + phoneNumber);
                }
            }
        } else {
            loanId = Long.parseLong(UrlUtils.getParam(event.getParameters(), UrlUtils.ID));
            refresh();
        }
    }

    protected abstract void addTabsAfter(BusinessObjectLayout layout);

    protected abstract void addTabsBefore(BusinessObjectLayout layout);

    protected void refresh() {
        removeAllComponents();
        setSpacing(false);
        setMargin(false);

        this.loan = loanQueries.findById(loanId);
        if (loan == null) {
            Notifications.errorNotification("Loan not found");
            return;
        }
        setCaption(format("Loan %s", loan.getLoanNumber()));

        BusinessObjectLayout layout = new BusinessObjectLayout();
        layout.setTitle(loan.getLoanNumber());
        buildLeft(loan, layout);
        buildTabs(loan, layout);
        buildActions(layout);
        addComponentsAndExpand(layout);
    }

    private void buildActions(BusinessObjectLayout layout) {
        if (hasPermission(BackofficePermissions.LOAN_WRITE_OFF)) {
            layout.addActionMenuItem("Write off amounts", e -> writeOff(loan));
        }
        if (hasPermission(BackofficePermissions.LOAN_VOID) && !LoanConstants.STATUS_DETAIL_VOIDED.equals(loan.getStatusDetail())) {
            layout.addActionMenuItem("Void loan", (event) -> voidLoan(loan));
        }
        if (hasPermission(BackofficePermissions.LOAN_BREAK) && equalsIgnoreCase(loan.getStatusDetail(), STATUS_DETAIL_ACTIVE)) {
            layout.addActionMenuItem("Break loan", (event) -> breakLoan(loan));
        }
        if (hasPermission(BackofficePermissions.LOAN_BREAK) && equalsIgnoreCase(loan.getStatusDetail(), STATUS_DETAIL_BROKEN)) {
            layout.addActionMenuItem("Un-Break loan", (event) -> unBreakLoan(loan));
        }
        if (hasPermission(BackofficePermissions.LOAN_VOID)
            && !LoanConstants.STATUS_CLOSED.equals(loan.getStatus()) && LoanConstants.STATUS_DETAIL_PAID.equals(loan.getStatusDetail())) {
            layout.addActionMenuItem("Close loan", event -> closePaidLoan(loan));
        }
        if (hasPermission(BackofficePermissions.OVERPAYMENT_USE) && isPositive(loan.getOverpaymentAvailable())) {
            layout.addActionMenuItem("Use overpayment", (event) -> useOverpayment(loan));
        }
        addCustomActions(layout);
        layout.setRefreshAction(this::refresh);
    }

    protected abstract void addCustomActions(BusinessObjectLayout layout);

    private void writeOff(LoanRecord loan) {
        ActionDialog dialog = loanComponents.writeOffDialog(loan.getId());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void voidLoan(LoanRecord loan) {
        Dialogs.confirm("Void loan", (Button.ClickListener) event -> {
            VoidLoanRequest request = new VoidLoanRequest();
            request.setLoanId(loan.getId());
            Call<Void> call = loanApiClient.voidLoan(request);
            BackgroundOperations.callApi("Voiding loan", call, t -> {
                Notifications.trayNotification("Loan voided");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void breakLoan(LoanRecord loan) {
        ActionDialog dialog = loanComponents.breakLoanDialog(loan.getId());
        dialog.addCloseListener(e -> refresh());
        getUI().addWindow(dialog);
    }

    private void unBreakLoan(LoanRecord loan) {
        Dialogs.confirm("Un-Break loan?", (Button.ClickListener) event -> {
            Call<Void> call = loanApiClient.unBreakLoan(new IdRequest(loan.getId()));
            BackgroundOperations.callApi("Un-Breaking loan", call, t -> {
                Notifications.trayNotification("Loan un-broken");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void closePaidLoan(LoanRecord loan) {
        Dialogs.confirm("Change the contract and close the loan?", (Button.ClickListener) event -> {
            Call<Void> call = loanApiClient.closePaidLoan(new IdRequest(loan.getId()));
            BackgroundOperations.callApi("Closing loan", call, t -> {
                Notifications.trayNotification("Loan closed");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void useOverpayment(LoanRecord loan) {
        Dialogs.confirm("Use overpayment as repayment?", (Button.ClickListener) event -> {
            Call<Void> call = loanApiClient.useOverpayment(
                new UseOverpaymentRequest(loan.getId(), loan.getOverpaymentAvailable(), "Repaying loan with overpayment"));
            BackgroundOperations.callApi("Repaying with overpayment", call, t -> {
                Notifications.trayNotification("Overpayment used");
                refresh();
            }, Notifications::errorNotification);
        });
    }

    private void buildTabs(LoanRecord loan, BusinessObjectLayout layout) {
        addTabsBefore(layout);
        layout.addTab("Payments", () -> payments(loan));
        layout.addTab("Transactions", () -> transactions(loan));
        layout.addTab("Schedule", () -> schedule(loan));
        layout.addTab("Disbursements", () -> disbursements(loan));
        layout.addTab("Workflows", () -> workflows(loan));
        layout.addTab("Accounting", () -> accounting(loan));
        addTabsAfter(layout);
    }

    private void buildLeft(LoanRecord loan, BusinessObjectLayout layout) {
        layout.addLeftComponent(loanComponents.loanInfo(loan));
        ClientDTO client = clientRepository.getRequired(loan.getClientId());
        layout.addLeftComponent(clientComponents.clientInfo(client));
        for (LoanApplicationRecord loanApplication : loanApplicationQueries.findByLoanId(loan.getId())) {
            layout.addLeftComponent(ApplicationInfo.fromApplication(loanApplication));
        }
    }

    private Component payments(LoanRecord loan) {
        PaymentTransactionDataProvider dataProvider = transactionComponents.paymentTransactionsDataProvider();
        dataProvider.setLoanId(loan.getId());
        return transactionComponents.paymentTransactionsGrid(dataProvider);
    }

    private Component transactions(LoanRecord loan) {
        TransactionDataProvider dataProvider = transactionComponents.dataProvider();
        dataProvider.setLoanId(loan.getId());
        return transactionComponents.grid(dataProvider);
    }

    private Component disbursements(LoanRecord loan) {
        DisbursementDataProvider dataProvider = disbursementComponents.dataProvider();
        dataProvider.setLoanId(loan.getId());
        return disbursementComponents.grid(dataProvider);
    }

    private Component workflows(LoanRecord loan) {
        WorkflowDataProvider dataProvider = workflowComponents.workflowDataProvider();
        dataProvider.setLoanId(loan.getId());
        return workflowComponents.workflowGrid(dataProvider);
    }

    protected Component invoices(LoanRecord loan) {
        InvoiceDataProvider dataProvider = invoiceComponents.dataProvider();
        dataProvider.setLoanId(loan.getId());
        return invoiceComponents.grid(dataProvider);
    }

    protected Component schedule(LoanRecord loan) {
        LoanScheduleComponent component = new LoanScheduleComponent(db, fileApiClient, loan.getId());
        component.setMargin(false);
        return component;
    }

    private Component accounting(LoanRecord loan) {
        AccountingTrialBalanceProvider trialBalanceProvider = accountingComponents.trialBalanceDataProvider();
        trialBalanceProvider.setLoanId(loan.getId());

        AccountingEntryDataProvider entryDataProvider = accountingComponents.entryDataProvider();
        entryDataProvider.setLoanId(loan.getId());

        return accountingComponents.accountingTabs(trialBalanceProvider, entryDataProvider);
    }
}
