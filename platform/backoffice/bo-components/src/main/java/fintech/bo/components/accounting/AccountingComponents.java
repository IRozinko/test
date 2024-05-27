package fintech.bo.components.accounting;

import com.vaadin.data.provider.Query;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.FooterCell;
import com.vaadin.ui.components.grid.FooterRow;
import com.vaadin.ui.renderers.ComponentRenderer;
import com.vaadin.ui.themes.ValoTheme;
import fintech.retrofit.RetrofitHelper;
import fintech.bo.api.client.AccountingApiClient;
import fintech.bo.api.client.FileApiClient;
import fintech.bo.api.model.CloudFile;
import fintech.bo.api.model.accounting.AccountTrialBalance;
import fintech.bo.api.model.accounting.AccountTrialBalanceExportResponse;
import fintech.bo.api.model.accounting.AccountingReportQuery;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.GridHelper;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.notifications.Notifications;
import fintech.bo.components.payments.PaymentComponents;
import fintech.bo.components.transaction.TransactionComponents;
import fintech.bo.components.utils.CloudFileDownloader;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

import static fintech.bo.components.Formats.decimalRenderer;
import static fintech.bo.components.GridHelper.ALIGN_RIGHT_STYLE;
import static fintech.bo.components.GridHelper.alignRightStyle;
import static fintech.bo.db.jooq.accounting.tables.Account.ACCOUNT;
import static fintech.bo.db.jooq.accounting.tables.Entry.ENTRY;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static fintech.bo.db.jooq.transaction.tables.Transaction.TRANSACTION_;
import static java.util.stream.Collectors.toList;

@Component
public class AccountingComponents {

    @Autowired
    private AccountingApiClient accountingApiClient;

    @Autowired
    private FileApiClient fileApiClient;

    @Autowired
    private TransactionComponents transactionComponents;

    @Autowired
    private DSLContext db;

    public AccountingTrialBalanceProvider trialBalanceDataProvider() {
        return new AccountingTrialBalanceProvider(accountingApiClient);
    }

    public AccountingEntryDataProvider entryDataProvider() {
        return new AccountingEntryDataProvider(db);
    }

    public TabSheet accountingTabs(AccountingTrialBalanceProvider trialBalanceProvider, AccountingEntryDataProvider entryDataProvider) {
        TabSheet tabs = new TabSheet();
        tabs.addTab(trialBalanceGrid(trialBalanceProvider), "Trial balance");
        tabs.addTab(accountEntryGrid(entryDataProvider), "Journal entries");
        tabs.setSizeFull();
        return tabs;
    }

    private Grid<AccountTrialBalance> trialBalanceGrid(AccountingTrialBalanceProvider dataProvider) {
        return trialBalanceGrid(dataProvider, true);
    }

    private Grid<AccountTrialBalance> trialBalanceGrid(AccountingTrialBalanceProvider dataProvider, boolean refresh) {
        Grid<AccountTrialBalance> grid = new Grid<>();
        grid.setDataProvider(dataProvider);
        grid.setSizeFull();

        grid.addColumn(record -> {
            Button btn = new Button("Entries");
            btn.addStyleName(ValoTheme.BUTTON_SMALL);
            btn.addClickListener(e -> {
                AccountingEntryDataProvider entryDataProvider = entryDataProvider();
                entryDataProvider.setAccountCode(record.getAccountCode());
                entryDataProvider.setQuery(dataProvider.getQuery());

                Window dialog = new Window(String.format("%s - %s", record.getAccountCode(), record.getAccountName()));
                dialog.setContent(accountEntryGrid(entryDataProvider));
                dialog.setHeight(800, Sizeable.Unit.PIXELS);
                dialog.setWidth(1400, Sizeable.Unit.PIXELS);
                dialog.center();
                UI.getCurrent().addWindow(dialog);
            });
            return btn;
        }, new ComponentRenderer()).setStyleGenerator(item -> BackofficeTheme.ACTION_ROW).setWidth(80).setId("open").setSortable(false);
        grid.addColumn(AccountTrialBalance::getAccountCode).setCaption("Account code").setId("account-code").setSortable(false);
        grid.addColumn(AccountTrialBalance::getAccountName).setCaption("Account name").setId("account-name").setSortable(false);
        grid.addColumn(AccountTrialBalance::getOpeningDebit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Opening debit").setId("opening-debit").setSortable(false);
        grid.addColumn(AccountTrialBalance::getOpeningCredit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Opening credit").setId("opening-credit").setSortable(false);
        grid.addColumn(AccountTrialBalance::getTurnoverDebit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Turnover debit").setId("turnover-debit").setSortable(false);
        grid.addColumn(AccountTrialBalance::getTurnoverCredit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Turnover credit").setId("turnover-credit").setSortable(false);
        grid.addColumn(AccountTrialBalance::getClosingDebit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Closing debit").setId("closing-debit").setSortable(false);
        grid.addColumn(AccountTrialBalance::getClosingCredit).setRenderer(decimalRenderer()).setStyleGenerator(alignRightStyle()).setCaption("Closing credit").setId("closing-credit").setSortable(false);

        FooterRow footer = grid.appendFooterRow();
        FooterCell footerTotalCell = footer.join(grid.getColumn("account-name"), grid.getColumn("account-code"));
        footerTotalCell.setText("Total:");
        footerTotalCell.setStyleName(ALIGN_RIGHT_STYLE);


        footer.getCell("open").setComponent(exportButton(dataProvider::getQuery));

        dataProvider.addDataProviderListener(e -> {
            List<AccountTrialBalance> data = e.getSource().fetch(new Query<>()).collect(toList());

            footer.getCell("opening-debit").setText(data.stream().map(AccountTrialBalance::getOpeningDebit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("opening-debit").setStyleName(ALIGN_RIGHT_STYLE);
            footer.getCell("opening-credit").setText(data.stream().map(AccountTrialBalance::getOpeningCredit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("opening-credit").setStyleName(ALIGN_RIGHT_STYLE);
            footer.getCell("turnover-debit").setText(data.stream().map(AccountTrialBalance::getTurnoverDebit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("turnover-debit").setStyleName(ALIGN_RIGHT_STYLE);
            footer.getCell("turnover-credit").setText(data.stream().map(AccountTrialBalance::getTurnoverCredit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("turnover-credit").setStyleName(ALIGN_RIGHT_STYLE);
            footer.getCell("closing-debit").setText(data.stream().map(AccountTrialBalance::getClosingDebit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("closing-debit").setStyleName(ALIGN_RIGHT_STYLE);
            footer.getCell("closing-credit").setText(data.stream().map(AccountTrialBalance::getClosingCredit).reduce(BigDecimal.ZERO, BigDecimal::add).toString());
            footer.getCell("closing-credit").setStyleName(ALIGN_RIGHT_STYLE);
        });

        if (refresh) {
            dataProvider.refreshAll();
        }

        return grid;
    }

    private Grid<Record> accountEntryGrid(AccountingEntryDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addActionColumn(ENTRY.TRANSACTION_ID, r -> UI.getCurrent().addWindow(transactionComponents.transactionInfoDialog(r.get(ENTRY.TRANSACTION_ID))))
            .setCaption("Transaction")
            .setWidth(100);
        builder.addColumn(TRANSACTION_.TRANSACTION_TYPE);
        builder.addColumn(ENTRY.BOOKING_DATE);
        builder.addColumn(ENTRY.POST_DATE);
        builder.addColumn(ACCOUNT.CODE);
        builder.addColumn(ACCOUNT.NAME).setWidth(200);
        builder.addColumn(ENTRY.DEBIT);
        builder.addColumn(ENTRY.CREDIT);
        builder.addLinkColumn(LOAN.LOAN_NUMBER, r -> LoanComponents.loanLink(r.get(ENTRY.LOAN_ID))).setCaption("Loan");
        builder.addLinkColumn(CLIENT.CLIENT_NUMBER, r -> ClientComponents.clientLink(r.get(ENTRY.CLIENT_ID))).setCaption("Client");
        builder.addLinkColumn(ENTRY.PAYMENT_ID, r -> PaymentComponents.paymentLink(r.get(ENTRY.PAYMENT_ID))).setCaption("Payment");

        Grid<Record> grid = builder.build(dataProvider);

        GridHelper.addExportFooter(grid, String.format("%s_journal_entries.xlsx", LocalDate.now()));

        return grid;
    }

    public com.vaadin.ui.Component exportButton(Supplier<AccountingReportQuery> supplier) {
        CloudFileDownloader downloader =
            new CloudFileDownloader(
                fileApiClient,
                () -> {
                    AccountTrialBalanceExportResponse response = RetrofitHelper.syncCall(accountingApiClient.export(supplier.get()))
                        .orElseThrow(IllegalStateException::new);

                    return new CloudFile(response.getId(), response.getFilename());
                },
                file -> Notifications.trayNotification("File downloaded: " + file.getName())
            );

        Button button = new Button("Export");
        button.addStyleName(ValoTheme.BUTTON_BORDERLESS_COLORED);
        downloader.extend(button);
        return button;
    }

}
