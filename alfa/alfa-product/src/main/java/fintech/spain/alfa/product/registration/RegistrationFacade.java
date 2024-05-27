package fintech.spain.alfa.product.registration;

import fintech.spain.alfa.product.lending.Inquiry;
import fintech.spain.alfa.product.registration.forms.AffiliateData;
import fintech.spain.alfa.product.registration.forms.AnalyticsData;
import fintech.spain.alfa.product.registration.forms.ApplicationForm;
import fintech.spain.alfa.product.registration.forms.DocumentNumberForm;
import fintech.spain.alfa.product.registration.forms.SignUpForm;

import java.math.BigDecimal;

public interface RegistrationFacade {

    Long signUp(SignUpForm form, boolean submitApplication);

    void saveDocumentNumber(Long clientId, DocumentNumberForm form);

    void saveApplicationData(Long clientId, ApplicationForm form);

    SendVerificationCodeResult sendPhoneVerificationCode(Long clientId);

    VerifyPhoneResult verifyPhone(Long clientId, String code);

    void changePhone(Long clientId, String mobilePhone);

    void saveAnalyticsData(Long clientId, Long applicationId, AnalyticsData data, String eventType);

    void saveAffiliateData(Long clientId, Long applicationId, AffiliateData affiliate);

    Inquiry buildInquiry(Long clientId, BigDecimal amount, Long termInDays, String promoCode, String affiliateName);
}
