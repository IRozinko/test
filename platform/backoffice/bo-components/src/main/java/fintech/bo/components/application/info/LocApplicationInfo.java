package fintech.bo.components.application.info;

import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;

public class LocApplicationInfo extends ApplicationInfo {

    LocApplicationInfo(LoanApplicationRecord application) {
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
    }

    @Override
    protected void requested(LoanApplicationRecord application) {
    }

    @Override
    protected void offered(LoanApplicationRecord application) {
    }

    @Override
    protected void apr(LoanApplicationRecord application) {
    }
}
