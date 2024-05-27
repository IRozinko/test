package fintech.bo.components.application.info;

import fintech.bo.components.ProductResolver;
import fintech.bo.components.PropertyLayout;
import fintech.bo.components.application.LoanApplicationComponents;
import fintech.bo.db.jooq.lending.tables.records.LoanApplicationRecord;
import org.apache.commons.lang3.StringUtils;

import static fintech.bo.components.application.LoanApplicationComponents.scoreBar;

public class ApplicationInfo extends PropertyLayout {

    ApplicationInfo(LoanApplicationRecord application) {
        super("Loan application");
        basic(application);
        requested(application);
        offered(application);
        add("Credit limit", application.getCreditLimit());
        score(application);
        apr(application);
        source(application);

    }

    public static ApplicationInfo fromApplication(LoanApplicationRecord application) {
        if (StringUtils.equalsIgnoreCase(application.getType(), "LINE_OF_CREDIT")) {
            return new LocApplicationInfo(application);
        } else if (ProductResolver.isPayday()) {
            return new PaydayApplicationInfo(application);
        } else if (ProductResolver.isRevolving()) {
            return new RevolvingApplicationInfo(application);
        } else {
            return new ApplicationInfo(application);
        }
    }

    protected void basic(LoanApplicationRecord application) {
        addLink("Number", application.getApplicationNumber(), LoanApplicationComponents.applicationLink(application.getId()));
        add("Status", application.getStatus());
        add("Status detail", application.getStatusDetail());
        add("Type", application.getType());
        add("Close reason", application.getCloseReason());
        add("Submitted At", application.getSubmittedAt());
        if (ProductResolver.isLineOfCredit()) {
            add("Invoice payment day", application.getInvoicePaymentDay());
        }
    }

    protected void requested(LoanApplicationRecord application) {
        add("Requested principal", application.getRequestedPrincipal());
    }

    protected void offered(LoanApplicationRecord application) {
        add("Offered principal", application.getOfferedPrincipal());
        add("Offered interest", application.getOfferedInterest());
        addPercentage("Offered discount percent", application.getOfferedInterestDiscountPercent());
        add("Offered discount amount", application.getOfferedInterestDiscountAmount());
    }

    protected void score(LoanApplicationRecord application) {
        add("Score", scoreBar(application));
        add("Score", application.getScore());
        add("Score bucket", application.getScoreBucket());
    }

    protected void apr(LoanApplicationRecord application) {
        addPercentage("Nominal APR", application.getNominalApr());
        addPercentage("Effective APR", application.getEffectiveApr());
    }

    protected void source(LoanApplicationRecord application) {
        add("Source type", application.getSourceType());
        add("Source name", application.getSourceName());
    }
}
