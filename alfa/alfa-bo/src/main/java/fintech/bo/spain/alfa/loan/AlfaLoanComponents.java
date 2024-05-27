package fintech.bo.spain.alfa.loan;

import com.vaadin.ui.Grid;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.components.JooqGridBuilder;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.client.ClientComponents;
import fintech.bo.components.client.JooqClientDataService;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanConstants;
import fintech.bo.components.loan.LoanDataProvider;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

import static fintech.bo.components.client.ClientDataProviderUtils.FIELD_CLIENT_NAME;
import static fintech.bo.db.jooq.crm.tables.Client.CLIENT;
import static fintech.bo.db.jooq.lending.Tables.DISCOUNT;
import static fintech.bo.db.jooq.lending.tables.Loan.LOAN;

@Component
public class AlfaLoanComponents extends LoanComponents {

    protected AlfaLoanComponents(DSLContext db, LoanApiClient loanApiClient, LoanQueries loanQueries, JooqClientDataService jooqClientDataService) {
        super(db, loanApiClient, loanQueries, jooqClientDataService);
    }

    protected Grid<Record> mainGrid(LoanDataProvider dataProvider) {
        JooqGridBuilder<Record> builder = new JooqGridBuilder<>();
        builder.addNavigationColumn("Open", r -> "loan/" + r.get(LOAN.ID));
        builder.addColumn(LOAN.LOAN_NUMBER);
        builder.addLinkColumn(FIELD_CLIENT_NAME, r -> ClientComponents.clientLink(r.get(LOAN.CLIENT_ID)));
        builder.addColumn(DISCOUNT.RATE_IN_PERCENT);
        builder.addColumn(LOAN.STATUS);
        builder.addColumn(LOAN.STATUS_DETAIL).setStyleGenerator(loanStatusStyle());
        addLineOfCreditLimit(builder);
        builder.addColumn(LOAN.ISSUE_DATE);
        builder.addColumn(LOAN.PAYMENT_DUE_DATE);
        builder.addColumn(LOAN.CLOSE_DATE);
        builder.addColumn(LOAN.OVERDUE_DAYS);
        builder.addColumn(LOAN.TOTAL_DUE);
        builder.addColumn(LOAN.TOTAL_OUTSTANDING);
        builder.addColumn(CLIENT.DOCUMENT_NUMBER);
        builder.addColumn(LoanDataProvider.FIELD_CLIENT_CREATED_AT);
        builder.addAuditColumns(LOAN);
        builder.addColumn(LOAN.ID);
        return builder.build(dataProvider);
    }

    @Override
    protected void addGridColumns(JooqGridBuilder<Record> builder) {
        builder.addNavigationColumn("Open", r -> "loan/" + r.get(LOAN.ID));
        builder.addColumn(LOAN.LOAN_NUMBER);
        builder.addColumn(LOAN.STATUS);
        builder.addColumn(LOAN.STATUS_DETAIL).setStyleGenerator(loanStatusStyle());
        builder.addColumn(LOAN.ISSUE_DATE);
        addLineOfCreditLimit(builder);
        builder.addColumn(LOAN.TOTAL_DUE);
        builder.addColumn(LOAN.TOTAL_OUTSTANDING);
        builder.addColumn(LOAN.PAYMENT_DUE_DATE);
        builder.addColumn(LOAN.CLOSE_DATE);
        builder.addColumn(LOAN.OVERDUE_DAYS);
        builder.addColumn(LoanDataProvider.FIELD_CLIENT_CREATED_AT);
        builder.addAuditColumns(LOAN);
        builder.addColumn(LOAN.ID);
    }

    @Override
    public PropertyLayout loanInfo(LoanRecord loan) {
        PropertyLayout layout = new PropertyLayout("Loan");
        if (loan.getOverdueDays() > 0 && LoanConstants.STATUS_OPEN.equals(loan.getStatus())) {
            layout.addWarning(String.format("Loan is delayed for %d day(s)", loan.getOverdueDays()));
        }
        layout.addLink("Number", loan.getLoanNumber(), LoanComponents.loanLink(loan.getId()));
        layout.add("Status", loan.getStatus());
        layout.add("Status detail", loan.getStatusDetail());
        layout.add("Issue date", loan.getIssueDate());
        layout.addDiscount("Discount percent", loan.getInterestDiscountPercent(), loan.getInterestDiscountAmount());
        layout.add("Credit limit", loan.getCreditLimit());
        layout.add("Principal", loan.getPrincipalGranted());
        layout.add("Initial interest", loan.getInterestApplied());
        layout.add("Term", loan.getPeriodCount());
        layout.add("Close date", loan.getCloseDate());
        layout.add("Payment due date", loan.getPaymentDueDate());
        layout.add("Overdue days", loan.getOverdueDays());

        layout.addSpacer();
        layout.add("Extensions", loan.getExtensions());
        layout.add("Extended by days", loan.getExtendedByDays());

        layout.addSpacer();
        layout.add("Cash out", loan.getCashOut());
        layout.add("Cash in", loan.getCashIn());

        layout.addSpacer();
        BigDecimal totalPaid = loan.getPrincipalPaid()
            .add(loan.getInterestPaid())
            .add(loan.getFeePaid())
            .add(loan.getPenaltyPaid());
        layout.add("Total paid", totalPaid);
        layout.add("Principal paid", loan.getPrincipalPaid());
        layout.add("Interest paid", loan.getInterestPaid());
        layout.add("Fee paid", loan.getFeePaid());
        layout.add("Penalties paid", loan.getPenaltyPaid());

        layout.addSpacer();
        layout.add("Outstanding total", loan.getTotalOutstanding());
        layout.add("Outstanding principal", loan.getPrincipalOutstanding());
        layout.add("Outstanding interest", loan.getInterestOutstanding());
        layout.add("Outstanding fee", loan.getFeeOutstanding());
        layout.add("Outstanding penalty", loan.getPenaltyOutstanding());

//        layout.addSpacer();
//        layout.add("Total due", loan.getTotalDue());
//        layout.add("Principal due", loan.getPrincipalDue());
//        layout.add("Interest due", loan.getInterestDue());
//        layout.add("Fee due", loan.getFeeDue());
//        layout.add("Penalty due", loan.getPenaltyDue());
//        layout.add("AEMIP compliant", Boolean.TRUE.equals(loan.getCompliantWithAemip()) ? "Yes" : "No");

        layout.addSpacer();
        layout.add("Overpayment received", loan.getOverpaymentReceived());
        layout.add("Overpayment used", loan.getOverpaymentUsed());
        layout.add("Overpayment refunded", loan.getOverpaymentRefunded());
        layout.add("Overpayment available", loan.getOverpaymentAvailable());
        return layout;
    }
}
