package fintech.bo.components.application.info;

import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;

public class RevolvingApplicationInfo extends ApplicationInfo {

    RevolvingApplicationInfo(LoanApplicationRecord application) {
        super(application);
    }

    @Override
    protected void basic(LoanApplicationRecord application) {
        addLink("Number", application.getApplicationNumber(), LoanApplicationComponents.applicationLink(application.getId()));
        add("Status", application.getStatus());
        add("Status detail", application.getStatusDetail());
        add("Type", application.getType());
        add("Close reason", application.getCloseReason());
        add("Submitted At", application.getSubmittedAt());
        add("Invoice payment day", application.getInvoicePaymentDay());
    }

    @Override
    protected void requested(LoanApplicationRecord application) {
        add("Requested principal", application.getRequestedPrincipal());
        add("Requested term", application.getRequestedPeriodCount());
        add("Requested term unit", application.getRequestedPeriodUnit());
    }

    @Override
    protected void offered(LoanApplicationRecord application) {
        add("Offered principal", application.getOfferedPrincipal());
    }

    @Override
    protected void apr(LoanApplicationRecord application) {
        addPercentage("Nominal APR", application.getNominalApr());
        addPercentage("Effective APR", application.getEffectiveApr());
    }

    @Override
    protected void source(LoanApplicationRecord application) {
    }

    @Override
    protected void score(LoanApplicationRecord application) {
        add("Score", application.getScore());
    }

}
