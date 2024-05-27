package fintech.spain.alfa.product.cms;

import com.google.common.collect.ImmutableList;
import fintech.ClasspathUtils;
import fintech.TimeMachine;
import fintech.cms.spi.CmsItem;
import fintech.cms.spi.CmsItemType;
import fintech.cms.spi.CmsRegistry;
import fintech.lending.core.loan.InstallmentStatus;
import fintech.lending.core.loan.InstallmentStatusDetail;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import fintech.strategy.model.ExtensionOffer;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomStringUtils;
import org.iban4j.Iban;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static fintech.BigDecimalUtils.amount;
import static fintech.DateUtils.date;

@Component
@RequiredArgsConstructor
public class CmsSetup {

    public static final String API_LOCALIZATION = "ApiLocalization";
    public static final String REGISTRATION_LOCALIZATION = "WebLogin";
    public static final String PROFILE_LOCALIZATION = "WebProfile";
    public static final String RESET_PASSWORD_NOTIFICATION = "ResetPasswordNotification";
    public static final String LOAN_REJECTED_NOTIFICATION = "LoanRejectedNotification";
    public static final String LOAN_REJECTED_NOTIFICATION_NO_LINK = "LoanRejectedNotificationNoLink";
    public static final String PHONE_VERIFICATION_NOTIFICATION = "PhoneVerificationNotification";
    public static final String LOAN_OFFER_SMS = "LoanOfferSms";
    public static final String LOAN_OFFER_EMAIL = "LoanOfferEmail";
    public static final String LOAN_OFFER_SMS_UPSELL = "LoanOfferSmsUpsell";
    public static final String LOAN_OFFER_EMAIL_UPSELL = "LoanOfferEmailUpsell";
    public static final String APPROVE_UPSELL_OFFER_EMAIL = "ApproveUpsellOfferEmail";
    public static final String LOAN_ISSUED_NOTIFICATION = "LoanIssuedNotification";
    public static final String LOAN_ISSUE_IN_PROGRESS_NOTIFICATION = "LoanIssueInProgressNotification";
    public static final String INSTANTOR_RETRY_REQUESTED_NOTIFICATION = "InstantorRetryRequestedNotification";
    public static final String CLIENT_PAYMENT_RECEIVED_NOTIFICATION = "ClientPaymentReceivedNotification";
    public static final String LOAN_APPLICATION_REMINDER_NOTIFICATION = "CS_ApplicationReminder_Notification";
    public static final String LOAN_APPLICATION_EXPIRED_NOTIFICATION = "LoanApplicationExpiredNotification";
    public static final String PHONE_VERIFICATION_EXPIRED_NOTIFICATION = "PhoneVerificationExpiredNotification";
    public static final String RESCHEDULING_TOC_NOTIFICATION = "ReschedulingTocNotification";
    public static final String RESCHEDULING_REMINDER_48_HOURS = "ReschedulingReminderNotification_48h";
    public static final String RESCHEDULING_EXPIRED_2_DAYS = "ReschedulingExpiredNotification_2d";
    public static final String RESCHEDULING_EXPIRED_3_DAYS = "ReschedulingExpiredNotification_3d";
    public static final String RESCHEDULING_EXPIRED_4_DAYS = "ReschedulingExpiredNotification_4d";

    public static final String APPROVE_LOAN_OFFER_REMINDER_NOTIFICATION = "ApproveLoanOfferReminder";
    public static final String LOAN_SOLD_NOTIFICATION = "LoanSoldNotification";
    public static final String ACTIVITY_FAILED_NOTIFICATION = "ActivityFailedNotification";

    public static final String CS_01_INSTANTOR_NOTIFICATION = "CS_01Instantor_Notification";
    public static final String CS_02_INSTANTOR_NOTIFICATION = "CS_02Instantor_Notification";

    public static final String CS_01_INSTANTOR_REVIEW_NOTIFICATION = "CS_01InstantorReview_Notification";
    public static final String CS_02_INSTANTOR_REVIEW_NOTIFICATION = "CS_02InstantorReview_Notification";

    public static final String CS_ID_UPLOAD_REMINDER = "CS_IdUploadReminder";
    public static final String CS_02_ID_UPLOAD_REMINDER = "CS_02UploadReminder";

    public static final String LOAN_AGREEMENT_PDF = "LoanAgreementPdf";
    public static final String STANDARD_INFORMATION_PDF = "StandardInformationPdf";
    public static final String UPSELL_AGREEMENT_PDF = "UpsellAgreementPdf";
    public static final String INVOICE_PDF = "InvoicePdf";
    public static final String PRIVACY_POLICY_PDF = "PrivacyPolicyPdf";
    public static final String RESCHEDULING_TOC_PDF = "ReschedulingTocPdf";
    public static final String CONTACT_ME = "ContactMe";
    public static final String STRATEGY_PREVIEW_PDF = "StrategyPreviewPdf";

    public static final String DPD_00_NOTIFICATION = "DC_00dpd";
    public static final String DPD_01_NOTIFICATION = "DC_01dpd";
    public static final String DPD_03_NOTIFICATION = "DC_03dpd";
    public static final String DPD_03_EXTENSION_NOTIFICATION = "DC_extension_3dpd";
    public static final String DPD_07_EXTENSION_NOTIFICATION = "DC_extension_7dpd";
    public static final String DPD_10_EXTENSION_NOTIFICATION = "DC_extension_10dpd";
    public static final String DPD_17_EXTENSION_NOTIFICATION = "DC_extension_17dpd";
    public static final String DPD_25_EXTENSION_NOTIFICATION = "DC_extension_25dpd";
    public static final String DPD_35_EXTENSION_NOTIFICATION = "DC_extension_35dpd";
    public static final String DPD_07_NOTIFICATION = "DC_07dpd";
    public static final String DPD_10_NOTIFICATION = "DC_10dpd";
    public static final String DPD_17_NOTIFICATION = "DC_17dpd";
    public static final String DPD_25_NOTIFICATION = "DC_25dpd";
    public static final String DPD_35_NOTIFICATION = "DC_35dpd";
    public static final String DPD_45_NOTIFICATION = "DC_45dpd";
    public static final String DPD_55_NOTIFICATION = "DC_55dpd";
    public static final String DPD_60_NOTIFICATION = "DC_60dpd";

    public static final String CUSTOM_NOTIFICATION = "CustomNotification";
    public static final String DC_CUSTOM_NOTIFICATION = "DcCustomNotification";

    // Dormant workflow
    public static final String LOC_PRE_OFFER_EMAIL = "LocPreOfferEmail";
    public static final String LOC_PRE_OFFER_SMS = "LocPreOfferSMS";
    public static final String LOC_PRE_OFFER_EMAIL_RESEND = "LocPreOfferEmailResend";
    public static final String LOC_PRE_OFFER_SMS_RESEND = "LocPreOfferSMSResend";
    public static final String LOC_INSTANTOR_NOTIFICATION_EMAIL = "LocInstantor_NotificationEmail";
    public static final String LOC_INSTANTOR_NOTIFICATION_SMS = "LocInstantor_NotificationSMS";
    public static final String LOC_INSTANTOR_REVIEW_NOTIFICATION_EMAIL = "LocInstantorReview_NotificationEmail";
    public static final String LOC_INSTANTOR_REVIEW_NOTIFICATION_SMS = "LocInstantorReview_NotificationSMS";
    public static final String LOC_REJECTED_NOTIFICATION_EMAIL = "LocRejectedNotificationEmail";
    public static final String LOC_REJECTED_NOTIFICATION_SMS = "LocRejectedNotificationSMS";
    public static final String LOC_LOAN_OFFER_EMAIL = "LocLoanOfferEmail";
    public static final String LOC_LOAN_OFFER_SMS = "LocLoanOfferSMS";
    public static final String LOC_LOAN_OFFER_EMAIL_REMINDER = "LocLoanOfferEmail_Reminder";
    public static final String LOC_LOAN_OFFER_SMS_REMINDER = "LocLoanOfferSMS_Reminder";
    public static final String LOC_LOAN_OFFER_EMAIL_RESEND = "LocLoanOfferEmail_Resend";
    public static final String LOC_LOAN_OFFER_SMS_RESEND = "LocLoanOfferSMS_Resend";
    public static final String LOC_LOAN_APPLICATION_EXPIRED_EMAIL = "LocLoanApplication_ExpiredEmail";
    public static final String LOC_LOAN_APPLICATION_EXPIRED_SMS = "LocLoanApplication_ExpiredSMS";
    public static final String DROPOUT_NOTIFICATION = "DropoutNotification";

    public static final String CERTIFICATE_OF_DEBT = "CertificateOfDebt";
    public static final String CERTIFICATE_OF_EARLY_REPAYMENT = "CertificateOfEarlyRepayment";

    public static final String LOAN_SUMMARY_PDF = "LoanSummaryPdf";
    public static final String LOAN_CERTIFICATE_PDF = "LoanCertificatePdf";
    public static final String DISBURSEMENT_DETAILS_PDF = "DisbursementDetailsPdf";
    public static final String INSTANTOR_RESPONSE_PDF = "InstantorResponsePdf";
    public static final String CERTIFICATE_OF_DEBT_PDF = "CertificateOfDebtPdf";

    /**
     * BE CAREFUL! If set to true -> all CMS content will be overwritten by default one
     */
    private static final boolean OVERWRITE = false;

    private final CmsRegistry registry;
    private final AlfaCmsModels cmsModels;

    public void setUp() {
        baseTemplates();

        setupDefaultContext();
        setupTestingContext();

        resetPasswordNotification();
        loanRejectedNotification();
        loanRejectedNotificationNoLink();
        loanIssueInProgressNotification();
        instantorRetryRequestedNotification();
        phoneVerificationNotification();
        loanOfferEmail();
        loanOfferSms();
        loanOfferEmailUpsell();
        loanOfferSmsUpsell();
        approveUpsellOfferEmail();
        clientPaymentReceivedNotification();
        reschedulingTocNotification();
        reschedulingReminderNotification_48h();
        reschedulingExpiredNotification_2d();
        reschedulingExpiredNotification_3d();
        reschedulingExpiredNotification_4d();

        loanAgreementPdf();
        standardInformationPdf();
        upsellAgreementPdf();
        invoicePdf();
        privacyPolicyPdf();
        reschedulingTocPdf();

        strategyPreviewPdf();

        localization();
        contactMeEmail();

        dpd00Notification();
        dpd01Notification();
        dpd03Notification();
        dpd03ExtensionNotification();
        dpd07Notification();
        dpd07ExtensionNotification();
        dpd10Notification();
        dpd10ExtensionNotification();
        dpd17Notification();
        dpd17ExtensionNotification();
        dpd25Notification();
        dpd25ExtensionNotification();
        dpd35Notification();
        dpd35ExtensionNotification();
        dpd45Notification();
        dpd55Notification();
        dpd60Notification();

        customNotification();
        dcCustomNotification();

        applicationReminderNotification();
        applicationExpiredNotification();
        phoneVerificationExpiredNotification();
        approveLoanOfferReminderNotification();
        loanSoldNotification();
        activityFailedNotification();

        cs01InstantorNotification();
        cs02InstantorNotification();
        cs01InstantorReviewNotification();
        cs02InstantorReviewNotification();

        csIdUploadNotification();
        cs02IdUploadReminderNotification();

        locPreOfferEmail();
        locPreOfferSMS();
        locPreOfferEmailResend();
        locPreOfferSmsResend();
        locInstantorNotificationEmail();
        locInstantorNotificationSms();
        locInstantorReviewNotificationEmail();
        locInstantorReviewNotificationSms();
        locLoanOfferEmail();
        locLoanOfferSMS();
        locLoanOfferEmailReminder();
        locLoanOfferSmsReminder();
        locLoanOfferEmailResend();
        locLoanOfferSmsResend();
        locRejectedNotificationEmail();
        locRejectedNotificationSms();
        locLoanApplicationExpiredEmail();
        locLoanApplicationExpiredSms();

        dropoutNotification();

        certificateOfEarlyRepayment();
        certificateOfDebt();

        loanSummaryPdf();
        loanCertificatePdf();
        disbursementDetailsPdf();
        instantorResponsePdf();
        certificateOfDebtPdf();
    }

    private void baseTemplates() {
        registry.saveItem(embeddable("_email_template", ClasspathUtils.resourceToString("cms/_email_template.html"), "Default email template"), OVERWRITE);
        registry.saveItem(embeddable("_email_footer", ClasspathUtils.resourceToString("cms/_email_footer.html"), "Default email footer"), OVERWRITE);
        registry.saveItem(embeddable("_email_social", ClasspathUtils.resourceToString("cms/_email_social.html"), "Links to social networks"), OVERWRITE);
        registry.saveItem(embeddable("_email_signature", ClasspathUtils.resourceToString("cms/_email_signature.html"), "Default email signature"), OVERWRITE);
        registry.saveItem(embeddable("_email_p_begin", ClasspathUtils.resourceToString("cms/_email_p_begin.html"), "For starting new paragraph in email"), OVERWRITE);
        registry.saveItem(embeddable("_email_p_end", ClasspathUtils.resourceToString("cms/_email_p_end.html"), "For ending paragraph in email"), OVERWRITE);
        registry.saveItem(embeddable("_sms_signature", ClasspathUtils.resourceToString("cms/_sms_signature.html"), "Default SMS signature"), OVERWRITE);
        registry.saveItem(embeddable("_cs_email_signature", ClasspathUtils.resourceToString("cms/_cs_email_signature.html"), "Default client support signature"), OVERWRITE);
        registry.saveItem(embeddable("_alfa_logo", ClasspathUtils.resourceToString("cms/_alfa_logo.html"), "Alfa logo"), OVERWRITE);
        registry.saveItem(embeddable("_certificate_signature", ClasspathUtils.resourceToString("cms/_certificate_signature.html"), "Certificate Signature"), OVERWRITE);
        registry.saveItem(embeddable("_email_contact_details", ClasspathUtils.resourceToString("cms/_email_contact_details.html"), "Contact details"), OVERWRITE);
        registry.saveItem(embeddable("_email_penalty_strategy", ClasspathUtils.resourceToString("cms/_email_penalty_strategy.html"), "Penalty strategy for email"), OVERWRITE);
        registry.saveItem(embeddable("_info_penalty_strategy", ClasspathUtils.resourceToString("cms/_info_penalty_strategy.html"), "Penalty strategy for standard information"), OVERWRITE);
        registry.saveItem(embeddable("_loan_agreement_penalty_strategy", ClasspathUtils.resourceToString("cms/_loan_agreement_penalty_strategy.html"), "Loan agreement penalty strategy"), OVERWRITE);
        registry.saveItem(embeddable("_strategy_P_A", ClasspathUtils.resourceToString("cms/_strategy_P_A.html"), "Penalty strategy"), OVERWRITE);
        registry.saveItem(embeddable("_strategy_P_AV", ClasspathUtils.resourceToString("cms/_strategy_P_AV.html"), "DPD Penalty strategy"), OVERWRITE);
        registry.saveItem(embeddable("_strategy_I_X", ClasspathUtils.resourceToString("cms/_strategy_I_X.html"), "Interest strategy"), OVERWRITE);
        registry.saveItem(embeddable("_strategy_E_D", ClasspathUtils.resourceToString("cms/_strategy_E_D.html"), "Extension strategy"), OVERWRITE);
    }

    private void localization() {
        CmsItem apiLocalization = new CmsItem();
        apiLocalization.setItemType(CmsItemType.TRANSLATION);
        apiLocalization.setKey(API_LOCALIZATION);
        apiLocalization.setLocale(AlfaConstants.LOCALE);
        apiLocalization.setScope("");
        apiLocalization.setDescription("Place to localize API like validation error messages");
        apiLocalization.setContentTemplate(ClasspathUtils.resourceToString("cms/translations/es/api_localization.json"));
        registry.saveItem(apiLocalization, OVERWRITE);


        CmsItem registrationLocalization = new CmsItem();
        registrationLocalization.setItemType(CmsItemType.TRANSLATION);
        registrationLocalization.setKey(REGISTRATION_LOCALIZATION);
        registrationLocalization.setLocale(AlfaConstants.LOCALE);
        registrationLocalization.setScope("");
        registrationLocalization.setDescription("Place to store localized strings for web registration module");
        registrationLocalization.setContentTemplate(ClasspathUtils.resourceToString("cms/translations/es/web_registration.json"));
        registry.saveItem(registrationLocalization, OVERWRITE);

        CmsItem profileLocalization = new CmsItem();
        profileLocalization.setItemType(CmsItemType.TRANSLATION);
        profileLocalization.setKey(PROFILE_LOCALIZATION);
        profileLocalization.setLocale(AlfaConstants.LOCALE);
        profileLocalization.setScope("");
        profileLocalization.setDescription("Place to store localized strings for web profile module");
        profileLocalization.setContentTemplate(ClasspathUtils.resourceToString("cms/translations/es/web_profile.json"));
        registry.saveItem(profileLocalization, OVERWRITE);

    }

    public void setupDefaultContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("currencyFormat", "#0.00");
        context.put("dateFormat", "dd.MM.yyyy");
        context.put("dates", new DatesModel());
        registry.setDefaultContext(context);
    }

    private void instantorRetryRequestedNotification() {
        registry.saveItem(new CmsNotificationBuilder(INSTANTOR_RETRY_REQUESTED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .description("Sent when client should retry Instantor step")
            .build(), OVERWRITE);
    }

    private void loanIssueInProgressNotification() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_ISSUE_IN_PROGRESS_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Confirmación de datos", ClasspathUtils.resourceToString("cms/LoanIssueInProgressEmail.html"))
            .description("Sent when manual operation (task) is required to complete loan issue workflow")
            .build(), OVERWRITE);
    }

    private void loanRejectedNotification() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_REJECTED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Lo sentimos, no podemos concederte el prestamo. Para otras opciones, solicitalo en: http://tracking.adcredy.com/SHiN?aff_sub={{application.number}}", ClasspathUtils.resourceToString("cms/LoanRejectedEmail.html"))
            .description("Sent when loan application has been rejected")
            .build(), OVERWRITE);
    }

    private void loanRejectedNotificationNoLink() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_REJECTED_NOTIFICATION_NO_LINK)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Información sobre tu solicitud", ClasspathUtils.resourceToString("cms/LoanRejectedEmail.html"))
            .description("Sent when loan application has been rejected - No external link")
            .build(), OVERWRITE);
    }

    private void phoneVerificationNotification() {
        registry.saveItem(new CmsNotificationBuilder(PHONE_VERIFICATION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_PHONE_VERIFICATION)
            .sms("{{phoneVerification.code}} Este es su código de verificación para Alfa.")
            .description("SMS with code for phone number verification")
            .build(), OVERWRITE);
    }

    private void loanOfferSms() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_OFFER_SMS)
            .scopes(AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION)
            .sms("Te ofrecemos {{application.offeredPrincipal | numberformat(currencyFormat)}}€ (TAE {{application.effectiveApr  | numberformat(currencyFormat)}}%). Envia {{application.shortApproveCode}} al {{company.incomingSmsNumber}} o aceptalo en tu Area de Usuario: {{ company.webBaseUrl }}/login")
            .description("SMS with info how to approve loan offer via phone")
            .build(), OVERWRITE);
    }

    private void loanOfferEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_OFFER_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Tu solicitud ha sido aprobada.  Solo te queda aceptar.", ClasspathUtils.resourceToString("cms/LoanOfferEmail.html"))
            .description("Email for approving loan offer")
            .build(), OVERWRITE);
    }

    private void loanOfferSmsUpsell() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_OFFER_SMS_UPSELL)
            .scopes(AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION)
            .sms("Te ofrecemos los {{application.offeredPrincipal | numberformat(currencyFormat)}}€ solicitados (TAE {{application.effectiveApr  | numberformat(currencyFormat)}}%) e incluso mas. Aprueba tu prestamo y selecciona la cantidad en: {{ company.webBaseUrl }}/login")
            .description("SMS with info how to approve loan offer via phone")
            .build(), OVERWRITE);
    }

    private void loanOfferEmailUpsell() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_OFFER_EMAIL_UPSELL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Su solicitud ha sido aprobada", ClasspathUtils.resourceToString("cms/LoanOfferEmailUpsell.html"))
            .description("Email for approving loan offer")
            .build(), OVERWRITE);
    }

    private void approveUpsellOfferEmail() {
        registry.saveItem(new CmsNotificationBuilder(APPROVE_UPSELL_OFFER_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Su solicitud ha sido aprobada", ClasspathUtils.resourceToString("cms/ApproveUpsellOfferEmail.html"))
            .description("Email for approving loan offer")
            .build(), OVERWRITE);
    }

    private void contactMeEmail() {
        registry.saveItem(new CmsNotificationBuilder(CONTACT_ME)
            .scopes(AlfaCmsModels.SCOPE_CONTACT_ME)
            .email("Contáctame", ClasspathUtils.resourceToString("cms/ContactMeEmail.html"))
            .description("Email to contact ctient")
            .build(), OVERWRITE);
    }

    private void dpd00Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_00_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_CALCULATION_STRATEGY)
            .email("Hoy vence tu préstamo – Métodos de pago", ClasspathUtils.resourceToString("cms/Dpd00Email.html"))
            .sms("Hoy vence tu prestamo ALFA de {{loan.totalDue | numberformat(currencyFormat)}}€. Paga el total o una prorroga en: www.alfa.es/login")
            .description("DPD_00_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd01Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_01_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_CALCULATION_STRATEGY)
            .email("El pago de su deuda sigue pendiente", ClasspathUtils.resourceToString("cms/Dpd01Email.html"))
            .sms("Todavia no hemos recibido su pago de {{debt.totalDue | numberformat(currencyFormat)}}€. Pague ahora o prorrogue su prestamo en: {{ company.webBaseUrl }}/login o el 933 035 223")
            .description("DPD_01_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd03Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_03_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .sms("ALFA INFO: Su deuda ha empezado a acumular penalizaciones.Llame ya al 933035223 para prorrogar o liquidar con tarjeta y podra pedir nuevo prestamo hoy mismo")
            .description("DPD_03_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd03ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_03_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .sms("ALFA INFO: Llame ya al 933035223 y conozca sus opciones para pagar su deuda. Evite asi empezar a acumular intereses")
            .description("DPD_03_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd07Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_07_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .email("Su préstamo de Alfa sigue vencido", ClasspathUtils.resourceToString("cms/Dpd07Email.html"))
            .description("DPD_07_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd07ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_07_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .email("Su préstamo de Alfa sigue vencido", ClasspathUtils.resourceToString("cms/Dpd07ExtensionEmail.html"))
            .description("DPD_07_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd10Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_10_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .email("Traslado información de su deuda a ASNEF", ClasspathUtils.resourceToString("cms/Dpd10Email.html"))
            .description("DPD_10_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd10ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_10_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .email("Traslado información de su deuda a ASNEF", ClasspathUtils.resourceToString("cms/Dpd10ExtensionEmail.html"))
            .description("DPD_10_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd17Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_17_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .sms("¿Porque no ha pagado aun? ¿Sabe que puede hacerlo en 933035223 con tarjeta o en www.alfa.es/login? El total es de {{debt.totalDue | numberformat(currencyFormat)}}€ sino lo tiene aun puede prorrogar")
            .description("DPD_17_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd17ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_17_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .sms("¿Porque no ha pagado aun? ¿Sabe que puede hacerlo en 933035223 con tarjeta o en www.alfa.es/login? El importe total es de  {{debt.totalDue | numberformat(currencyFormat)}}€")
            .description("DPD_17_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd25Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_25_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .email("Últimas horas para evitar PUBLICACIÓN en ASNEF", ClasspathUtils.resourceToString("cms/Dpd25Email.html"))
            .description("DPD_25_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd25ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_25_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .email("Últimas horas para evitar PUBLICACIÓN en ASNEF", ClasspathUtils.resourceToString("cms/Dpd25ExtensionEmail.html"))
            .description("DPD_25_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd35Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_35_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY)
            .sms("No ha devuelto el dinero prestado hace mas de un mes por ALFA. ASNEF ha publicado su deuda de {{debt.totalDue | numberformat(currencyFormat)}}€. HOY aun puede liquidar o prorrogar en 933035223")
            .description("DPD_35_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd35ExtensionNotification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_35_EXTENSION_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_DEBT)
            .sms("No ha devuelto el dinero prestado hace mas de un mes por ALFA. ASNEF ha publicado su deuda de {{debt.totalDue | numberformat(currencyFormat)}}€. HOY aun puede liquidar CON TARJETA en 933035223")
            .description("DPD_35_EXTENSION_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd45Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_45_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .sms("Debido a su NEGATIVA de pago hemos derivamos su expediente al DEP. JUDICIAL. Su deuda asciende ya a {{debt.totalDue | numberformat(currencyFormat)}}€  AUN puede fraccionar y parar el tramite. 933035223")
            .description("DPD_45_NOTIFICATION")
            .build(), OVERWRITE);
    }


    private void dpd55Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_55_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .email("Su deuda Alfa sigue vencida y en trámite", ClasspathUtils.resourceToString("cms/Dpd55Email.html"))
            .description("DPD_55_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void dpd60Notification() {
        registry.saveItem(new CmsNotificationBuilder(DPD_60_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN)
            .sms("Alfa certifica su incumplimiento de contrato y su negativa de pago. Ya hemos desactivado casi todas las ayudas y descuentos. Para mas info 933035223")
            .description("DPD_60_NOTIFICATION")
            .build(), OVERWRITE);
    }

    private void customNotification() {
        registry.saveItem(new CmsNotificationBuilder(CUSTOM_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_DEBT)
            .email("Enter subject", "Enter body")
            .sms("Enter message")
            .description("Custom notification")
            .build(), OVERWRITE);
    }

    private void dcCustomNotification() {
        registry.saveItem(new CmsNotificationBuilder(DC_CUSTOM_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_DEBT)
            .email("Enter subject", "Enter body")
            .sms("Enter message")
            .description("Custom notification for DC purposes")
            .build(), OVERWRITE);
    }

    private void applicationReminderNotification() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_APPLICATION_REMINDER_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Accede a tu Area de usuario y completa el formulario: {{ company.webBaseUrl }}/login Si no puedes terminar la solicitud, llámanos al {{ company.phone }}")
            .email("¿No puedes terminar tu solicitud? Llámanos", ClasspathUtils.resourceToString("cms/CS_ApplicationReminder_Notification.html"))
            .description("A reminder to client to finish the loan application form")
            .build(), OVERWRITE);
    }

    private void applicationExpiredNotification() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_APPLICATION_EXPIRED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Tu solicitud ha expirado al no finalizar el proceso. Accede a tu Area de usuario y ¡reactivala cuando quieras!  {{ company.webBaseUrl }}/login")
            .email("Tu solicitud ha caducado", ClasspathUtils.resourceToString("cms/ApplicationExpiredNotification.html"))
            .description("Sent when loan application expired or was canceled")
            .build(), OVERWRITE);
    }

    private void phoneVerificationExpiredNotification() {
        registry.saveItem(new CmsNotificationBuilder(PHONE_VERIFICATION_EXPIRED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Tu solicitud ha expirado al no finalizar el proceso. Accede a tu Area de usuario y ¡reactivala cuando quieras! {{ company.webBaseUrl }}/login")
            .description("Sent when phone verification expired")
            .build(), OVERWRITE);
    }

    private void approveLoanOfferReminderNotification() {
        registry.saveItem(new CmsNotificationBuilder(APPROVE_LOAN_OFFER_REMINDER_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .email("Acepta el préstamo y recibirás tu transferencia", ClasspathUtils.resourceToString("cms/ApproveLoanOfferReminder.html"))
            .description("A reminder to accept the loan offer")
            .build(), OVERWRITE);
    }

    private void loanSoldNotification() {
        registry.saveItem(new CmsNotificationBuilder(LOAN_SOLD_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN)
            .email("ALFA informa del traspaso de su préstamo", ClasspathUtils.resourceToString("cms/LoanSoldEmail.html"))
            .description("Loan sold notification")
            .build(), OVERWRITE);
    }

    private void activityFailedNotification() {
        registry.saveItem(new CmsNotificationBuilder(ACTIVITY_FAILED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Tu solicitud no se ha completado correctamente. Por favor, vuelve a intentarlo más tarde en: {{ company.webBaseUrl }}/login")
            .email("Tu solicitud no se ha completado - Inténtalo más tarde", ClasspathUtils.resourceToString("cms/ActivityFailedEmail.html"))
            .description("Loan sold notification")
            .build(), OVERWRITE);
    }

    private void cs01InstantorNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_01_INSTANTOR_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Identificate como si accedieras a tu cuenta bancaria por Internet. Tus datos estaran protegidos y cifrados: {{ company.webBaseUrl }}/login")
            .email("Cómo identificarte con tu cuenta bancaria online y por qué lo solicitamos", ClasspathUtils.resourceToString("cms/CS_01_Instantor_Notification.html"))
            .description("Reminder to provide bank account in application form")
            .build(), OVERWRITE);
    }

    private void cs02InstantorNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_02_INSTANTOR_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Queda poco para que caduque tu solicitud. Identificate desde tu Area de usuario con tu cuenta bancaria online: {{ company.webBaseUrl }}/login")
            .email("{{client.firstName}}, finaliza tu solicitud antes de que caduque", ClasspathUtils.resourceToString("cms/CS_02_Instantor_Notification.html"))
            .description("Reminder to provide bank account in application form")
            .build(), OVERWRITE);
    }

    private void cs01InstantorReviewNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_01_INSTANTOR_REVIEW_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Selecciona tu cuenta corriente en: {{ company.webBaseUrl }}/login Necesitas ser su titular o cotitular y recibir tus ingresos en ella.")
            .email("{{client.firstName}}, selecciona la cuenta corriente en la que quieres recibir el préstamo", ClasspathUtils.resourceToString("cms/CS_01_Instantor_Review_Notification.html"))
            .description("Reminder to provide bank account in application form")
            .build(), OVERWRITE);
    }

    private void cs02InstantorReviewNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_02_INSTANTOR_REVIEW_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Selecciona tu cuenta corriente antes de que caduque tu solicitud. Debes ser su titular o cotitular y recibir en ella tus ingresos:  {{ company.webBaseUrl }}/login")
            .email("Selecciona tu cuenta antes de que caduque tu solicitud",
                ClasspathUtils.resourceToString("cms/CS_02_InstantorReviewNotification.html"))
            .description("Reminder to provide bank account in application form")
            .build(), OVERWRITE);
    }

    private void csIdUploadNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_ID_UPLOAD_REMINDER)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .email("Tu solicitud ha sido preaprobada, envianos fotos de ambas caras de tu DNI/NIE para recibir tu oferta. Subelas aqui: {{ company.webBaseUrl }}/login", ClasspathUtils.resourceToString("cms/CS_IdUploadReminder.html"))
            .description("Reminder to upload ID document")
            .build(), OVERWRITE);
    }

    private void cs02IdUploadReminderNotification() {
        registry.saveItem(new CmsNotificationBuilder(CS_02_ID_UPLOAD_REMINDER)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Obten tu prestamo antes de que caduque tu solicitud. Envia fotos de tu DNI/NIE (ambas caras) a info@alfa.es o cargalas en: {{ company.webBaseUrl }}/login")
            .email("{{client.firstName}}, envíanos fotografías de tu DNI/NIE antes de que caduque tu solicitud de préstamo",
                ClasspathUtils.resourceToString("cms/CS_02_IdUploadReminderEmail.html"))
            .description("Reminder to upload ID document")
            .build(), OVERWRITE);
    }

    private void locPreOfferEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_PRE_OFFER_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("¿Quieres MÁS DINERO y pagar MENOS INTERESES? - Solicítalo ahora", ClasspathUtils.resourceToString("cms/wf/dormants/LocPreOfferEmail.html"))
            .description("LOC Pre Offer Email")
            .build(), OVERWRITE);
    }

    private void locPreOfferSMS() {
        registry.saveItem(new CmsNotificationBuilder(LOC_PRE_OFFER_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .sms("MÁS DINERO y MENOS INTERESES. Te presentamos PRESTO, nuestra línea de crédito. {{company.webBaseUrl + \"/sl/\" + specialLink.token }}")
            .description("LOC Pre Offer SMS")
            .build(), OVERWRITE);
    }

    private void locPreOfferEmailResend() {
        registry.saveItem(new CmsNotificationBuilder(LOC_PRE_OFFER_EMAIL_RESEND)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("Resumen de nuestra llamada:  MÁS DINERO por MENOS INTERESES - Solicítalo ahora",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocPreOfferEmailResend.html"))
            .description("LOC Pre Offer Email Resend")
            .build(), OVERWRITE);
    }

    private void locPreOfferSmsResend() {
        registry.saveItem(new CmsNotificationBuilder(LOC_PRE_OFFER_SMS_RESEND)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .sms("Resumen de nuestra llamada:  MÁS DINERO, MENOS INTERESES. Solicítalo: {{company.webBaseUrl + \"/sl/\" + specialLink.token }}")
            .description("LOC Pre Offer SMS Resend")
            .build(), OVERWRITE);
    }

    private void locInstantorNotificationEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_INSTANTOR_NOTIFICATION_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("Identifícate con tu cuenta bancaria online… ¡para obtener tu dinero!",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocInstantor_NotificationEmail.html"))
            .description("LOC Instantor Notification Email")
            .build(), OVERWRITE);
    }

    private void locInstantorNotificationSms() {
        registry.saveItem(new CmsNotificationBuilder(LOC_INSTANTOR_NOTIFICATION_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Identíficate con tu cuenta bancaria online ¡para obtener tu dinero! Que no se te olvide: {{ company.webBaseUrl }}")
            .description("LOC Instantor Notification SMS")
            .build(), OVERWRITE);
    }

    private void locInstantorReviewNotificationEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_INSTANTOR_REVIEW_NOTIFICATION_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("Identifícate con tu cuenta bancaria online… ¡para obtener tu dinero!",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocInstantorReview_NotificationEmail.html"))
            .description("LOC Instantor Review Notification Email")
            .build(), OVERWRITE);
    }

    private void locInstantorReviewNotificationSms() {
        registry.saveItem(new CmsNotificationBuilder(LOC_INSTANTOR_REVIEW_NOTIFICATION_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("Identíficate con tu cuenta bancaria online ¡para obtener tu dinero! Que no se te olvide: {{ company.webBaseUrl }}")
            .description("LOC Instantor Review Notification SMS")
            .build(), OVERWRITE);
    }

    private void locRejectedNotificationEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_REJECTED_NOTIFICATION_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Lo sentimos… Tu línea de crédito no ha sido aprobada.",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocRejectedNotificationEmail.html"))
            .description("LOC Rejected Notification Email")
            .build(), OVERWRITE);
    }

    private void locRejectedNotificationSms() {
        registry.saveItem(new CmsNotificationBuilder(LOC_REJECTED_NOTIFICATION_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("No podemos ofrecerte una línea de crédito ahora, lo sentimos. Sigue contando con ALFA para tus minipréstamos. https://bit.ly/2Np45uL")
            .description("LOC Rejected Notification SMS")
            .build(), OVERWRITE);
    }

    private void locLoanOfferEmailReminder() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_EMAIL_REMINDER)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("¡Recuerda! Debes aceptar para recibir tu dinero",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanOfferEmail_Reminder.html"))
            .description("LOC Loan Offer Email Reminder")
            .build(), OVERWRITE);
    }

    private void locLoanOfferSmsReminder() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_SMS_REMINDER)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .sms("¡Recuerda! Acepta tu línea de crédito en: {{company.webBaseUrl + \"/sl/\" + specialLink.token }}")
            .description("LOC Loan Offer SMS Reminder")
            .build(), OVERWRITE);
    }

    private void locLoanOfferEmailResend() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_EMAIL_RESEND)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("Acepta la oferta ¡es el último paso para obtener tu dinero!",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanOfferEmail_Resend.html"))
            .description("LOC Loan Offer Email Resend")
            .build(), OVERWRITE);
    }

    private void locLoanOfferSmsResend() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_SMS_RESEND)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .sms("Como hemos hablado, acepta en: {{company.webBaseUrl + \"/sl/\" + specialLink.token }}")
            .description("LOC Loan Offer SMS Resend")
            .build(), OVERWRITE);
    }

    private void locLoanApplicationExpiredEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_APPLICATION_EXPIRED_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_APPLICATION)
            .email("Enter subject",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanApplication_ExpiredEmail.html"))
            .description("LOC Loan Application Expired Email")
            .build(), OVERWRITE);
    }

    private void locLoanApplicationExpiredSms() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_APPLICATION_EXPIRED_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT)
            .sms("SMS Body")
            .description("LOC Loan Application Expired SMS")
            .build(), OVERWRITE);
    }

    private void locLoanOfferEmail() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_EMAIL)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .email("Tu solicitud ha sido aprobada ¡acepta la oferta y recibirás tu dinero!",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanOfferEmail.html"))
            .description("LOC Loan Offer Email")
            .build(), OVERWRITE);
    }

    private void locLoanOfferSMS() {
        registry.saveItem(new CmsNotificationBuilder(LOC_LOAN_OFFER_SMS)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_SPECIAL_LINK)
            .sms(ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanOfferSMS.html"))
            .description("LOC Loan Offer SMS")
            .build(), OVERWRITE);
    }

    private void dropoutNotification() {
        registry.saveItem(new CmsNotificationBuilder(DROPOUT_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION, AlfaCmsModels.SCOPE_AUTO_LOGIN)
            .email("No te quedes con las ganas - Tu dinero está a unos minutos de distancia",
                ClasspathUtils.resourceToString("cms/wf/dormants/LocLoanOfferEmail.html"))
            .description("Dropout notification")
            .build(), OVERWRITE);
    }

    private void certificateOfEarlyRepayment() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(CERTIFICATE_OF_EARLY_REPAYMENT);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION, AlfaCmsModels.SCOPE_LOAN);
        item.setDescription("CertificateOfEarlyRepayment PDF template");
        item.setTitleTemplate("{{loan.number}}_CertificateOfEarlyRepayment.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/CertificateOfEarlyRepayment.html"));
        registry.saveItem(item, OVERWRITE);
    }

    private void certificateOfDebt() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(CERTIFICATE_OF_DEBT);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION, AlfaCmsModels.SCOPE_LOAN);
        item.setDescription("CertificateOfDebt PDF template");
        item.setTitleTemplate("{{loan.number}}_CertificateOfDebt.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/CertificateOfDebt.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void loanAgreementPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(LOAN_AGREEMENT_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", agreementScope()));
        item.setDescription("Loan agreement PDF template");
        item.setTitleTemplate("acuerdo_de_prestamo_{{application.number}}.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/LoanAgreement.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void strategyPreviewPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(STRATEGY_PREVIEW_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setDescription("Calculation strategy item preview template");
        item.setScope("");
        item.setTitleTemplate("strategy_preview.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/StrategyPreview.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void standardInformationPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(STANDARD_INFORMATION_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", standardInformationScope()));
        item.setDescription("Standard information PDF template");
        item.setTitleTemplate("información_normalizada.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/StandardInformation.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void upsellAgreementPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(UPSELL_AGREEMENT_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_UPSELL));
        item.setDescription("Upsell agreement PDF template");
        item.setTitleTemplate("acuerdo_de_prestamo_{{application.number}}.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/UpsellAgreement.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void invoicePdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(INVOICE_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_CLIENT_REPAYMENT));
        item.setDescription("Invoice PDF template");
        item.setTitleTemplate("invoice.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/InvoicePdf.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void privacyPolicyPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(PRIVACY_POLICY_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT));
        item.setDescription("Privacy policy PDF template");
        item.setTitleTemplate("privacy_policy.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/PrivacyPolicy.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void reschedulingTocPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(RESCHEDULING_TOC_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE));
        item.setDescription("Rescheduling offer PDF template");
        item.setTitleTemplate("rescheduling_offer.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/ReschedulingTocPdf.html"));
        registry.saveItem(item, OVERWRITE);
    }

    private void reschedulingTocNotification() {
        registry.saveItem(new CmsNotificationBuilder(RESCHEDULING_TOC_NOTIFICATION)
            .scopes(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE, AlfaCmsModels.SCOPE_DEBT))
            .email("Active el fraccionamiento de su deuda ALFA", ClasspathUtils.resourceToString("cms/ReschedulingTocEmail.html"))
            .sms("Active el fraccionamiento de su deuda ALFA con el PAGO de la primera cuota de {{schedule.installments[0].totalScheduled | numberformat(currencyFormat)}}E en el PLAZO MÁXIMO de 48h. Revise condiciones en la web o en su mail")
            .description("Sent when new rescheduling is offered")
            .build(), OVERWRITE);
    }

    private void reschedulingReminderNotification_48h() {
        registry.saveItem(new CmsNotificationBuilder(RESCHEDULING_REMINDER_48_HOURS)
            .scopes(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE, AlfaCmsModels.SCOPE_DEBT))
            .sms("Recuerde que su cuota mensual de {{schedule.installments[0].totalScheduled | numberformat(currencyFormat)}}€ vence en 48h. Puede pagarla con tarjeta en www.alfa.es/login o en 933035223")
            .description("Sent 2 days before the monthly payment due date")
            .build(), OVERWRITE);
    }

    private void reschedulingExpiredNotification_2d() {
        registry.saveItem(new CmsNotificationBuilder(RESCHEDULING_EXPIRED_2_DAYS)
            .scopes(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE, AlfaCmsModels.SCOPE_DEBT))
            .sms("Su cuota mensual de {{schedule.installments[0].totalScheduled | numberformat(currencyFormat)}}€ ya esta vencida. LLAME AHORA al 933035223 y regularice la situacion antes de que quede anulado el fraccionamiento")
            .description("Sent 2 days after the monthly payment due date")
            .build(), OVERWRITE);
    }

    private void reschedulingExpiredNotification_3d() {
        registry.saveItem(new CmsNotificationBuilder(RESCHEDULING_EXPIRED_3_DAYS)
            .scopes(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE, AlfaCmsModels.SCOPE_DEBT))
            .email("Fraccionamiento en riesgo", ClasspathUtils.resourceToString("cms/ReschedulingExpireEmail.html"))
            .description("Sent 4 days after the monthly payment due date")
            .build(), OVERWRITE);
    }

    private void reschedulingExpiredNotification_4d() {
        registry.saveItem(new CmsNotificationBuilder(RESCHEDULING_EXPIRED_4_DAYS)
            .scopes(String.join(",", AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_LOAN, AlfaCmsModels.SCOPE_SCHEDULE, AlfaCmsModels.SCOPE_DEBT))
            .sms("Alfa desactivara su fraccionamiento mañana y se le reclamara el total de deuda pendiente. PAGUE hoy sin falta en 933035223")
            .description("Sent 2 days after the monthly payment due date")
            .build(), OVERWRITE);
    }

    private void clientPaymentReceivedNotification() {
        registry.saveItem(new CmsNotificationBuilder(CLIENT_PAYMENT_RECEIVED_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_CLIENT_INCOMING_PAYMENT)
            .sms("Hemos recibido su pago de {{clientIncomingPayment.amount | numberformat(currencyFormat)}}€. Puede descargar su factura en su área de usuario de alfa.es. {% include \"_sms_signature\" %}")
            .email("Pago recibido. Aquí tienes tu factura.", ClasspathUtils.resourceToString("cms/ClientPaymentEmail.html"))
            .description("Sent when received incoming payment from the client")
            .build(), OVERWRITE);
    }

    @SneakyThrows
    private void loanSummaryPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(LOAN_SUMMARY_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",",
            AlfaCmsModels.SCOPE_CLIENT,
            AlfaCmsModels.SCOPE_LOAN,
            AlfaCmsModels.SCOPE_APPLICATION));
        item.setDescription("Loan Summary PDF template");
        item.setTitleTemplate("resumen_del_préstamo.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/LoanSummary.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void loanCertificatePdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(LOAN_CERTIFICATE_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",",
            AlfaCmsModels.SCOPE_COMPANY,
            AlfaCmsModels.SCOPE_CLIENT,
            AlfaCmsModels.SCOPE_LOAN,
            AlfaCmsModels.SCOPE_APPLICATION));
        item.setDescription("Loan Certificate Conclusion PDF template");
        item.setTitleTemplate("certificado_préstamo.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/LoanCertificate.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void disbursementDetailsPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(DISBURSEMENT_DETAILS_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(AlfaCmsModels.SCOPE_DISBURSEMENT);
        item.setDescription("Disbursement information PDF template");
        item.setTitleTemplate("justificante_transferencia_{{disbursement.reference}}.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/DisbursementDetails.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void instantorResponsePdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(INSTANTOR_RESPONSE_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(AlfaCmsModels.SCOPE_INSTANTOR_REPORT);
        item.setDescription("Instantor response report PDF template");
        item.setTitleTemplate("instantor_report.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/InstantorResponse.html"));
        registry.saveItem(item, OVERWRITE);
    }

    @SneakyThrows
    private void certificateOfDebtPdf() {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.PDF_HTML);
        item.setKey(CERTIFICATE_OF_DEBT_PDF);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope(String.join(",",
            AlfaCmsModels.SCOPE_LOAN,
            AlfaCmsModels.SCOPE_CLIENT,
            AlfaCmsModels.SCOPE_DEBT));
        item.setDescription("Certificate of debt");
        item.setTitleTemplate("certificado_deuda.pdf");
        item.setContentTemplate(ClasspathUtils.resourceToString("cms/CertificateOfDebt.html"));
        registry.saveItem(item, OVERWRITE);
    }

    private List<String> standardInformationScope() {
        return ImmutableList.of(AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_STANDARD_INFORMATION);
    }

    private List<String> agreementScope() {
        return ImmutableList.of(AlfaCmsModels.SCOPE_CLIENT, AlfaCmsModels.SCOPE_COMPANY, AlfaCmsModels.SCOPE_APPLICATION);
    }

    private void setupTestingContext() {
        Map<String, Supplier<Object>> context = new HashMap<>();
        context.put(AlfaCmsModels.SCOPE_PHONE_VERIFICATION, () -> new PhoneVerificationModel("12345"));
        context.put(AlfaCmsModels.SCOPE_CLIENT, CmsSetup::testClientModel);
        context.put(AlfaCmsModels.SCOPE_LOAN, this::testLoanModel);
        context.put(AlfaCmsModels.SCOPE_COMPANY, cmsModels::company);
        context.put(AlfaCmsModels.SCOPE_APPLICATION, CmsSetup::testApplicationModel);
        context.put(AlfaCmsModels.SCOPE_RESET_PASSWORD, this::testResetPasswordModel);
        context.put(AlfaCmsModels.SCOPE_STANDARD_INFORMATION, CmsSetup::testStandardInformationModel);
        context.put(AlfaCmsModels.SCOPE_DEBT, CmsSetup::testDebtModel);
        context.put(AlfaCmsModels.SCOPE_CLIENT_INCOMING_PAYMENT, CmsSetup::testClientIncomingPaymentModel);
        context.put(AlfaCmsModels.SCOPE_CLIENT_REPAYMENT, CmsSetup::testClientRepaymentModel);
        context.put(AlfaCmsModels.SCOPE_CONTACT_ME, CmsSetup::testContactMeModel);
        context.put(AlfaCmsModels.SCOPE_SCHEDULE, this::testSchedule);
        context.put(AlfaCmsModels.SCOPE_UPSELL, CmsSetup::testUpsellModel);
        context.put(AlfaCmsModels.SCOPE_SPECIAL_LINK, CmsSetup::testSpecialLinkModel);
        context.put(AlfaCmsModels.SCOPE_AUTO_LOGIN, CmsSetup::testAutoLoginModel);
        context.put(AlfaCmsModels.SCOPE_CALCULATION_STRATEGY, CmsSetup::testCalculationStrategiesModel);
        registry.setTestingContext(context);
    }

    private ResetPasswordModel testResetPasswordModel() {
        ResetPasswordModel model = new ResetPasswordModel();
        model.setUrl("http://localhost/reset-password");
        return model;
    }

    public static DebtModel testDebtModel() {
        DebtModel model = new DebtModel();
        model.setTotalDue(amount(150.67));
        return model;
    }

    public static ApplicationModel testApplicationModel() {
        ApplicationModel model = new ApplicationModel();
        model.setLongApproveCode(RandomStringUtils.randomAlphanumeric(10));
        model.setShortApproveCode(AlfaConstants.SMS_APPROVE_CODE);
        model.setOfferedPrincipal(amount(500));
        model.setCreditLimit(amount(800.00));
        model.setNominalApr(amount(120.00));
        model.setEffectiveApr(amount(213.84));
        model.setDate(TimeMachine.today());
        model.setOfferDate(TimeMachine.today().plusDays(1));
        model.setOfferMaturityDate(TimeMachine.today().plusDays(30));
        model.setOfferedPeriodCount(30L);
        model.setOfferedInterest(amount(0.90));
        model.setOfferedTotal(amount(500.90));
        model.setNumber("2614758-001");
        return model;
    }

    public static StandardInformationModel testStandardInformationModel() {
        StandardInformationModel model = new StandardInformationModel();
        model.setInterestPercentageRatePerDay(new BigDecimal(0.329).setScale(3, BigDecimal.ROUND_HALF_UP));
        model.setPenaltyPercentageRatePerDay(amount(1.00));
        model.setMaxProductAmount(amount(2000.00));
        model.setExamplePrincipalAmount(amount(100.00));
        model.setExampleRepaymentAmount(amount(117.60));
        model.setMaxProductPeriodCount(30);
        return model;
    }

    public static ClientModel testClientModel() {
        ClientModel clientModel = new ClientModel();
        clientModel.setNumber("2614758");
        clientModel.setDocumentNumber("23ABBXC32");
        clientModel.setEmail("test.client@mailinator.com");
        clientModel.setPhoneNumber("+3475078165");
        clientModel.setFirstName("John");
        clientModel.setLastName("Smith");
        clientModel.setSecondLastName("Doe");
        clientModel.setFullName("John Smith");
        clientModel.setIban("ES7218468373391724541976");
        clientModel.setIbanFormatted(Iban.valueOf("ES7218468373391724541976").toFormattedString());
        clientModel.setAddressLine1("Avda. Andalucía 54");
        clientModel.setAddressLine2("La Rioja, Cabezón de Cameros, 26135");
        clientModel.setRegistrationIpAddress("0.0.0.0");
        clientModel.setRegisteredAt(LocalDate.parse("2018-05-01").atStartOfDay());
        clientModel.setCreditLimit(amount(300));
        clientModel.setLoyaltyDiscount(amount(10));
        return clientModel;
    }

    private LoanModel testLoanModel() {
        LoanModel model = new LoanModel();
        model.setPrincipal(amount(200));
        model.setNumber("123456");
        model.setIssueDate(LocalDate.parse("2018-04-01"));
        model.setMaturityDate(LocalDate.parse("2018-05-01"));
        model.setInterestDue(amount(100.00));
        model.setTotalDue(amount(200.90));
        model.setPrePaymentInterestDue(amount(100.00));
        model.setPrePaymentTotalDue(amount(200.90));
        model.setTotalOutstanding(amount(500.90));
        model.setFeeOutstanding(amount(25));
        model.setExtensions(
            Arrays.asList(
                new ExtensionOffer(ChronoUnit.DAYS, 7L, amount(20.00), amount(0), amount(0), amount(0)),
                new ExtensionOffer(ChronoUnit.DAYS, 14L, amount(28.00), amount(0), amount(0), amount(0)),
                new ExtensionOffer(ChronoUnit.DAYS, 30L, amount(52.00), amount(0), amount(0), amount(0)),
                new ExtensionOffer(ChronoUnit.DAYS, 45L, amount(70.00), amount(0), amount(0), amount(0))
            )
        );
        return model;
    }

    public static ContactMeModel testContactMeModel() {
        return new ContactMeModel()
            .setName("Mark")
            .setEmail("mark@mail.com")
            .setPhone("+34222111444")
            .setIpAddress("12.233.22.22")
            .setComment("Test Contact me Comment");
    }

    public static ClientIncomingPaymentModel testClientIncomingPaymentModel() {
        ClientIncomingPaymentModel model = new ClientIncomingPaymentModel();
        model.setAmount(amount(90.95));
        model.setValueDate(TimeMachine.today());
        return model;
    }

    public static ClientRepaymentModel testClientRepaymentModel() {
        ClientRepaymentModel model = new ClientRepaymentModel();
        model.setPrincipalPaid(amount(500));
        model.setInterestPaid(amount(40));
        model.setExtensionFeePaid(amount(20.00));
        model.setReschedulingFeePaid(amount(25.00));
        model.setPenaltyPaid(amount(8.96));
        model.setTotalInvoiced(amount(48.96));
        model.setTotalPaid(amount(548.96));
        model.setRepaymentDate(TimeMachine.today());
        model.setTransactionId(1337L);
        return model;
    }

    public static UpsellModel testUpsellModel() {
        ApplicationModel upsell = testApplicationModel();
        ApplicationModel loan = testApplicationModel();

        return new UpsellModel()
            .setUpsell(upsell)
            .setLoan(loan)
            .setLoanNumber("TEST LOAN NUMBER")
            .setTotalPrincipal(upsell.getOfferedPrincipal().add(loan.getOfferedPrincipal()))
            .setTotalInterest(upsell.getOfferedInterest().add(loan.getOfferedInterest()))
            .setGrandTotal(upsell.getOfferedTotal().add(loan.getOfferedTotal()))
            .setAverageNominalApr(upsell.getNominalApr().add(loan.getNominalApr()).divide(amount(2), RoundingMode.HALF_UP))
            .setAverageEffectiveApr(upsell.getEffectiveApr().add(loan.getEffectiveApr()).divide(amount(2), RoundingMode.HALF_UP));
    }

    public static SpecialLink testSpecialLinkModel() {
        SpecialLink specialLink = new SpecialLink();
        specialLink.setClientId(1234);
        specialLink.setType(SpecialLinkType.LOC_SPECIAL_OFFER);
        specialLink.setToken(UUID.randomUUID().toString());
        specialLink.setAutoLoginRequired(true);
        return specialLink;
    }

    public static String testAutoLoginModel() {
        return "https://www.alfa.es/auto-login?token=" + UUID.randomUUID().toString();
    }

    public static CalculationStrategyModel testCalculationStrategiesModel() {
        CalculationStrategyModel model = new CalculationStrategyModel();

        model.setExtensionStrategy(CalculationStrategyModel.EXTENSION_STRATEGY_D);
        model.setInterestStrategy(CalculationStrategyModel.INTEREST_STRATEGY_X);
        model.setPenaltyStrategy(CalculationStrategyModel.PENALTY_STRATEGY_AV);

        model.setExtensionStrategyDProperties(new ExtensionStrategyProperties()
            .setExtensions(Collections.singletonList(new ExtensionStrategyProperties.ExtensionOption().setTerm(7).setRate(amount(12.50)))));

        model.setInterestStrategyXProperties(new MonthlyInterestStrategyProperties()
            .setMonthlyInterestRate(amount(13.00))
            .setUsingDecisionEngine(false)
            .setScenario("interest_setting"));

        model.setPenaltyStrategyAProperties(new DailyPenaltyStrategyProperties().setPenaltyRate(amount(3.14)));

        model.setPenaltyStrategyAVProperties(new DpdPenaltyStrategyProperties().setStrategies(Arrays.asList(
            new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(1).setRate(amount(12)),
            new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(10).setRate(amount(15)),
            new DpdPenaltyStrategyProperties.PenaltyStrategy().setFrom(20).setRate(amount(20))
        )));

        return model;
    }

    private static CmsItem embeddable(String key, String content, String description) {
        CmsItem item = new CmsItem();
        item.setItemType(CmsItemType.EMBEDDABLE);
        item.setKey(key);
        item.setLocale(AlfaConstants.LOCALE);
        item.setScope("");
        item.setDescription(description);
        item.setContentTemplate(content);
        return item;
    }

    private void resetPasswordNotification() {
        registry.saveItem(new CmsNotificationBuilder(RESET_PASSWORD_NOTIFICATION)
            .scopes(AlfaCmsModels.SCOPE_RESET_PASSWORD)
            .email("Actualizar contraseña", ClasspathUtils.resourceToString("cms/ResetPasswordEmail.html"))
            .description("Sent when client requests password reset")
            .build(), OVERWRITE);
    }

    private ScheduleModel testSchedule() {
        return new ScheduleModel()
            .setFeeScheduled(amount(5.00))
            .setTotalScheduled(amount(270.00))
            .setTotalDue(amount(270.00))
            .setTotalPaid(amount(0.00))
            .setStartDate(date("2018-01-01"))
            .setInstallments(ImmutableList.of(
                new ScheduleModel.InstallmentModel()
                    .setStatus(InstallmentStatus.OPEN)
                    .setStatusDetail(InstallmentStatusDetail.PENDING)
                    .setPeriodFrom(date("2018-01-01"))
                    .setPeriodTo(date("2018-01-31"))
                    .setDueDate(date("2018-02-01"))
                    .setGenerateInvoiceOnDate(date("2018-01-02"))
                    .setInstallmentSequence(1L)
                    .setInstallmentNumber("C123456-001-001")
                    .setTotalDue(amount(100.00))
                    .setTotalScheduled(amount(100.00))
                    .setPrincipalScheduled(amount(50.00))
                    .setInterestScheduled(amount(30.00))
                    .setPenaltyScheduled(amount(15.00))
                    .setFeeScheduled(amount(5.0)),
                new ScheduleModel.InstallmentModel()
                    .setStatus(InstallmentStatus.OPEN)
                    .setStatusDetail(InstallmentStatusDetail.PENDING)
                    .setPeriodFrom(date("2018-02-01"))
                    .setPeriodTo(date("2018-02-28"))
                    .setDueDate(date("2018-03-01"))
                    .setGenerateInvoiceOnDate(date("2018-02-02"))
                    .setInstallmentSequence(2L)
                    .setInstallmentNumber("C123456-001-002")
                    .setTotalDue(amount(90.00))
                    .setTotalScheduled(amount(90.00))
                    .setPrincipalScheduled(amount(50.00))
                    .setInterestScheduled(amount(30.00))
                    .setPenaltyScheduled(amount(10.00))
                    .setFeeScheduled(amount(0.0)),
                new ScheduleModel.InstallmentModel()
                    .setStatus(InstallmentStatus.OPEN)
                    .setStatusDetail(InstallmentStatusDetail.PENDING)
                    .setPeriodFrom(date("2018-03-01"))
                    .setPeriodTo(date("2018-03-31"))
                    .setDueDate(date("2018-04-01"))
                    .setGenerateInvoiceOnDate(date("2018-03-02"))
                    .setInstallmentSequence(3L)
                    .setInstallmentNumber("C123456-001-003")
                    .setTotalDue(amount(80.00))
                    .setTotalScheduled(amount(80.00))
                    .setPrincipalScheduled(amount(50.00))
                    .setInterestScheduled(amount(25.00))
                    .setPenaltyScheduled(amount(5.00))
                    .setFeeScheduled(amount(0.0))
            ));
    }
}
