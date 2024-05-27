package fintech.bo.spain.alfa.dc;

import fintech.bo.api.client.DcApiClient;
import fintech.bo.api.client.LoanApiClient;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.dc.DcComponents;
import fintech.bo.components.dc.DcQueries;
import fintech.bo.components.loan.LoanComponents;
import fintech.bo.components.loan.LoanQueries;
import fintech.bo.db.jooq.dc.tables.records.DebtRecord;
import fintech.bo.db.jooq.lending.tables.records.LoanRecord;
import org.apache.commons.lang3.StringUtils;
import org.jooq.DSLContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class AlfaDcComponents extends DcComponents {

    protected AlfaDcComponents(DcQueries dcQueries, DcApiClient apiClient, DSLContext db, LoanQueries loanQueries, LoanApiClient loanApiClient) {
        super(dcQueries, apiClient, db, loanQueries, loanApiClient);
    }

    @Override
    public PropertyLayout debtInfo(DebtRecord debt) {
        LoanRecord loan = getLoanQueries().findById(debt.getLoanId());
        PropertyLayout layout = new PropertyLayout("Debt");
        layout.add("DPD", debt.getDpd());
        layout.addLink("Loan", debt.getLoanNumber(), LoanComponents.loanLink(debt.getLoanId()));
        layout.add("Term", debt.getPeriodCount());
        layout.add("Managing Company", debt.getManagingCompany());
        layout.add("Portfolio", debt.getPortfolio());
        layout.add("Debt Status", debt.getDebtStatus());
        if (StringUtils.isNotEmpty(debt.getDebtSubStatus())) {
            layout.add("Debt Sub status", debt.getDebtSubStatus());
        }
        layout.add("Debt state", debt.getDebtState());
        layout.add("Agent", debt.getAgent());

        layout.addSpacer();
        layout.add("Loan created date", loan.getIssueDate());
        layout.add("Payment due date", debt.getPaymentDueDate());
        layout.add("Aging Bucket", debt.getAgingBucket());

        layout.addSpacer();
        layout.add("Extensions", loan.getExtensions());

        layout.addSpacer();
        layout.add("Outstanding total", debt.getTotalOutstanding());
//        layout.add("Outstanding principal", debt.getPrincipalOutstanding());
//        layout.add("Outstanding interest", debt.getInterestOutstanding());
//        layout.add("Outstanding fee", debt.getFeeOutstanding());
//        layout.add("Outstanding penalty", debt.getPenaltyOutstanding());
//        layout.addSpacer();
//        layout.add("Total outstanding", debt.getTotalOutstanding());
//        layout.add("Cash out", loan.getCashOut());

//        layout.addSpacer();
//        layout.add("Principal", loan.getPrincipalGranted());
//        layout.add("Initial interest", loan.getInterestApplied());
//        layout.add("Total due", debt.getTotalDue());
//        layout.add("Principal due", debt.getPrincipalDue());
//        layout.add("Interest due", debt.getInterestDue());
//        layout.add("Fee due", debt.getFeeDue());
//        layout.add("Penalty due", debt.getPenaltyDue());

        layout.addSpacer();
        layout.add("Total paid", debt.getTotalPaid());
//        layout.add("Principal paid", debt.getPrincipalPaid());
//        layout.add("Interest paid", debt.getInterestPaid());
//        layout.add("Penalty paid", debt.getPenaltyPaid());
//        layout.add("Fee paid", debt.getFeePaid());

        layout.addSpacer();
        layout.add("Last paid", debt.getLastPaid());
        layout.add("Last paid date", debt.getLastPaymentDate());

        layout.addSpacer();
        layout.add("Promise due date", debt.getPromiseDueDate());
        layout.add("Promise amount", debt.getPromiseAmount());

        layout.addSpacer();
        layout.add("Next action", debt.getNextAction());
        layout.add("Next action date", debt.getNextActionAt());

        layout.addSpacer();
        layout.add("Last action", debt.getLastAction());
        layout.add("Last action date", debt.getLastActionAt());
        return layout;
    }
}
