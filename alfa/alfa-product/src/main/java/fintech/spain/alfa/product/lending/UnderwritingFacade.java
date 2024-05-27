package fintech.spain.alfa.product.lending;

import fintech.lending.core.application.LoanApplication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public interface UnderwritingFacade {

    String UPSELL_AB_TEST_WORKFLOW_ATTRIBUTE = "UpsellABTest";

    OfferSettings publicOfferSettings();

    OfferSettings clientOfferSettings(Long clientId, LocalDate when);

    DiscountOffer getDiscountOffer(Long clientId, LocalDate when);

    Offer makeOffer(Inquiry inquiry);

    Long submitApplication(Long clientId, Inquiry inquiry);

    Long startFirstLoanApplicationWorkflow(Long applicationId, Map<String, String> attributes);

    Long startFirstLoanAffiliatesApplicationWorkflow(Long applicationId, Map<String, String> attributes);

    Long startUpsellWorkflow(Long clientId, Long loanId);

    void prepareOffer(Long applicationId, LocalDate offerDate);

    void prepareOffer(Long applicationId, LocalDate offerDate, BigDecimal principalRequested);

    void sendLoanOfferSms(Long applicationId);

    void sendLoanOfferEmail(Long applicationId, Long agreementAttachmentId, Long standardInformationAttachmentId);

    void sendLoanOfferSmsUpsell(Long applicationId);

    void sendLoanOfferEmailUpsell(Long applicationId, Long agreementAttachmentId, Long standardInformationAttachmentId);

    LoanApplication approveApplicationWithLongCode(String longCode, String ipAddress);

    void approveApplicationWithSms(Long clientId, String shortCode);

    void webApproveApplication(Long clientId, Long applicationId, String ipAddress);

    void webApproveUpsellOffer(Long clientId, Long applicationId, BigDecimal principal, String ip, String abSource);

    Long generateLoanAgreement(Long applicationId);

    Long generateStandardInformation(Long applicationId);

    Long generateUpsellAgreement(Long applicationId);

    LoanIssueResult issueLoan(Long applicationId, LocalDate issueDate);

    Long retryApplication(Long applicationId);
}
