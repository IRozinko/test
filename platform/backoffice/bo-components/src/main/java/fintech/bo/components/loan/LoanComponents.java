package fintech.bo.components.loan;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.StyleGenerator;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.components.BackofficeTheme;
import fintech.bo.components.Formats;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.ProductResolver;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.dialogs.ActionDialog;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.jooq.DSLContext;
import org.jooq.Record;

import java.math.BigDecimal;

import static com.google.common.base.MoreObjects.firstNonNull;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_ACTIVE;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_PAID;
import static fintech.bo.components.loan.LoanConstants.STATUS_DETAIL_VOIDED;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;
import static org.apache.commons.lang3.text.WordUtils.capitalizeFully;

public abstract class LoanComponents {

    private final DSLContext db;
    private final LoanApiClient loanApiClient;
    private final LoanQueries loanQueries;
    private final JooqClientDataService jooqClientDataService;

    protected LoanComponents(DSLContext db, LoanApiClient loanApiClient, LoanQueries loanQueries, JooqClientDataService jooqClientDataService) {
        this.db = db;
        this.loanApiClient = loanApiClient;
        this.loanQueries = loanQueries;
        this.jooqClientDataService = jooqClientDataService;
    }

    public ComboBox<Record> loansComboBox(LoanDataProvider dataProvider) {
        ComboBox<Record> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Select loan...");
        comboBox.setPageLength(20);
        comboBox.setDataProvider(dataProvider);
        comboBox.setPopupWidth("600px");
        comboBox.setItemCaptionGenerator(item -> String.format("%s | %s | %s | %s",
            item.get(LOAN.LOAN_NUMBER),
            Joiner.on(" ").join(
                capitalizeFully(firstNonNull(item.get(CLIENT.FIRST_NAME), "")),
                capitalizeFully(firstNonNull(item.get(CLIENT.LAST_NAME), "")),
                capitalizeFully(firstNonNull(item.get(CLIENT.SECOND_LAST_NAME), ""))
            ),
            capitalizeFully(firstNonNull(item.get(CLIENT.DOCUMENT_NUMBER), "")),
            Formats.decimalFormat().format(MoreObjects.firstNonNull(item.get(LOAN.TOTAL_DUE), 0))
        ));
        return comboBox;
    }

    public ComboBox<String> statusComboBox() {
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.setPlaceholder("Loan status");
        comboBox.setItems(loanQueries.findStatuses());
        comboBox.setTextInputAllowed(false);
        return comboBox;
    }

    public LoanDataProvider dataProvider() {
        return new LoanDataProvider(db, jooqClientDataService);
    }

    public Grid<Record> grid(LoanDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        addGridColumns(builder);
        builder.sortDesc(LOAN.CREATED_AT);
        return builder.build(dataProvider);
    }

    protected abstract void addGridColumns(JooqGridBuilder<Record> builder);

    protected void addLineOfCreditLimit(JooqGridBuilder<Record> builder) {
        if (ProductResolver.isLineOfCredit()) {
            builder.addColumn(LOAN.CREDIT_LIMIT);
        } else if (ProductResolver.isPayday()) {
            builder.addColumn(LOAN.PRINCIPAL_GRANTED).setCaption("Amount");
            builder.addColumn(LOAN.PERIOD_COUNT).setCaption("Term (days)");
        }
    }

    public static String loanLink(Long loanId) {
        return AbstractLoanView.NAME + "/" + loanId;
    }

    public static StyleGenerator<Record> loanStatusStyle() {
        return item -> {
            String status = item.get(LOAN.STATUS_DETAIL);
            Integer overdueDays = MoreObjects.firstNonNull(item.get(LOAN.OVERDUE_DAYS, Integer.class), 0);
            if (STATUS_DETAIL_PAID.equals(status)) {
                return BackofficeTheme.TEXT_SUCCESS;
            } else if (STATUS_DETAIL_VOIDED.equals(status)) {
                return BackofficeTheme.TEXT_GRAY;
            } else if (STATUS_DETAIL_ACTIVE.equals(status)) {
                return BackofficeTheme.TEXT_ACTIVE;
            } else if (overdueDays > 0) {
                return BackofficeTheme.TEXT_DANGER;
            } else {
                return "";
            }
        };
    }

    public PropertyLayout loanInfo(Long loanId) {
        LoanRecord loan = loanQueries.findById(loanId);
        return loanInfo(loan);
    }

    public abstract PropertyLayout loanInfo(LoanRecord loan);

    public PropertyLayout loanInfoSimple(Long loanId) {
        LoanRecord loan = loanQueries.findById(loanId);
        return loanInfoSimple(loan);
    }

    public PropertyLayout loanInfoSimple(LoanRecord loan) {
        PropertyLayout layout = new PropertyLayout("Loan");
        layout.addLink("Number", loan.getLoanNumber(), LoanComponents.loanLink(loan.getId()));
        layout.add("Status detail", loan.getStatusDetail());
        layout.add("Payment due date", loan.getPaymentDueDate());
        layout.add("Total due", loan.getTotalDue());
        layout.add("Total outstanding", loan.getTotalOutstanding());
        BigDecimal totalPaid = loan.getPrincipalPaid().add(loan.getInterestPaid()).add(loan.getFeePaid()).add(loan.getPenaltyPaid());
        layout.add("Total paid", totalPaid);
        layout.add("Overpayment available", loan.getOverpaymentAvailable());
        return layout;
    }

    public ActionDialog writeOffDialog(Long loanId) {
        LoanRecord loan = loanQueries.findById(loanId);
        return new LoanWriteOffDialog(loan, loanApiClient);
    }

    public ActionDialog breakLoanDialog(Long loanId) {
        return new BreakLoanDialog(loanId);
    }

    public LoanApiClient getLoanApiClient() {
        return loanApiClient;
    }

    public LoanQueries getLoanQueries() {
        return loanQueries;
    }

    public DSLContext getDb() {
        return db;
    }
}
