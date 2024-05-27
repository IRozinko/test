package fintech.bo.components.application.info;

import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;

public class PaydayApplicationInfo extends ApplicationInfo {

    PaydayApplicationInfo(LoanApplicationRecord application) {
        super(application);
    }

    @Override
    protected void requested(LoanApplicationRecord application) {
        add("Requested principal", application.getRequestedPrincipal());
        if (!application.getType().equals("UPSELL")) {
            add("Requested term", application.getRequestedPeriodCount());
        }
    }

    @Override
    protected void offered(LoanApplicationRecord application) {
        add("Offered principal", application.getOfferedPrincipal());
        add("Offered interest", application.getOfferedInterest());
        if (!application.getType().equals("UPSELL")) {
            add("Offered term", application.getOfferedPeriodCount());
        }
        addPercentage("Offered discount percent", application.getOfferedInterestDiscountPercent());
        add("Offered discount amount", application.getOfferedInterestDiscountAmount());
    }
}
