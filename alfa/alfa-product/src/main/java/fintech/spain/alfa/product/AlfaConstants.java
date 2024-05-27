package fintech.spain.alfa.product;

public class AlfaConstants {

    public static final Long PRODUCT_ID = 2L;

    public static final String CURRENCY = "EUR";
    public static final String PHONE_COUNTRY_CODE = "34";
    public static final String LOCALE = "es";
    public static final String COUNTRY_CODE = "ES";
    public static final String UNKNOWN_IP_COUNTRY = "N/A";
    public static final String SMS_APPROVE_CODE = "ACEPTO";
    public static final String ADDRESS_TYPE_ACTUAL = "ACTUAL";
    public static final String CLIENT_NUMBER_PREFIX = "T";
    public static final int CLIENT_NUMBER_LENGTH = 7;

    public static final String DISBURSEMENT_REFERENCE_PREFIX = "p-";
    public static final String DISBURSEMENT_REFERENCE_ENDING = "-p";
    public static final int DISBURSEMENT_REFERENCE_LENGTH = 12;

    public static final Long GRACE_PERIOD_IN_DAYS = 2L;

    public static final String REJECT_REASON_NO_DNI = "NoDni";
    public static final String REJECT_REASON_NO_EMAIL = "NoEmail";
    public static final String REJECT_REASON_NO_PHONE = "NoPhone";
    public static final String REJECT_REASON_NO_ADDRESS = "NoAddress";
    public static final String REJECT_REASON_NO_POSTAL_CODE = "NoPostalCode";
    public static final String REJECT_REASON_AGE_TOO_YOUNG = "AgeTooYoung";
    public static final String REJECT_REASON_AGE_TOO_OLD = "AgeTooOld";
    public static final String REJECT_REASON_PROVINCE_NOT_ALLOWED = "ProvinceNotAllowed";
    public static final String REJECT_REASON_SUBMITTED_NEW_APPLICATION = "SubmittedNewApplication";
    public static final String REJECT_REASON_DNI_NOT_ALLOWED = "DniNotAllowed";
    public static final String REJECT_REASON_EMAIL_NOT_ALLOWED = "EmailNotAllowed";
    public static final String REJECT_REASON_PHONE_NOT_ALLOWED = "PhoneNotAllowed";
    public static final String REJECT_REASON_CLIENT_HAS_OPEN_LOAN = "ClientHasOpenLoan";
    public static final String REJECT_REASON_NO_INSTANTOR_NAME = "NoInstantorName";
    public static final String REJECT_REASON_INSTANTOR_DNI_NO_MATCH = "InstantorDniNoMatch";
    public static final String MANUAL_REASON_INSTANTOR_DNI_NO_MATCH = "InstantorDniNoMatchEnough";
    public static final String REJECT_REASON_INSTANTOR_NAME_NO_MATCH = "InstantorNameNoMatch";
    public static final String REJECT_REASON_INSTANTOR_AMOUNT_OF_LAST_MONTHS_LOANS_NO_MATCH = "AmountOfLastMonthsLoansNotMatch";
    public static final String MANUAL_REASON_INSTANTOR_NAME_NO_MATCH = "InstantorNameNoMatchEnough";
    public static final String REJECT_REASON_AMOUNT_BELOW_MINIMUM = "AmountBelowMinimum";
    public static final String REJECT_REASON_NO_EXPERIAN_CAIS_RESUMEN_RESPONSE = "NoExperianCaisResumenResponse";
    public static final String REJECT_REASON_NO_EXPERIAN_CAIS_OPERACIONES_RESPONSE = "NoExperianCaisOperacionesResponse";
    public static final String REJECT_REASON_NO_EXPERIAN_CAIS_RESUMEN_PROVINCE_CODE = "NoExperianCaisResumenProvinceCode";
    public static final String REJECT_REASON_EXPERIAN_DEBT_AMOUNT = "ExperianDebtAmount";
    public static final String REJECT_REASON_EXPERIAN_DEBT_COUNT = "ExperianDebtCount";
    public static final String REJECT_REASON_EXPERIAN_PAYMENT_SITUATION = "ExperianPaymentSituation";
    public static final String REJECT_REASON_NO_CROSSCHECK_RESPONSE = "NoCrosscheckResponse";
    public static final String REJECT_REASON_CROSSCHECK_MAX_DPD = "CrosscheckMaxDpd";
    public static final String REJECT_REASON_CROSSCHECK_ACTIVE_LOAN = "CrosscheckActiveLoan";
    public static final String REJECT_REASON_CROSSCHECK_ACTIVE_APPLICATION = "CrosscheckActiveApplication";
    public static final String REJECT_REASON_CROSSCHECK_BLACKLISTED = "CrosscheckBlacklisted";
    public static final String REJECT_REASON_NO_EQUIFAX_RESPONSE = "NoEquifaxResponse";
    public static final String REJECT_REASON_EQUIFAX_DEBT_AMOUNT = "EquifaxDebtAmount";
    public static final String REJECT_REASON_EQUIFAX_DEBT_COUNT = "EquifaxDebtCount";
    public static final String REJECT_REASON_EQUIFAX_DELINCUENCY_DAYS = "EquifaxDelincuencyDays";
    public static final String REJECT_REASON_EQUIFAX_NUMBER_OF_DAYS_OF_WORST_SITUATION = "EquifaxNumberOfDaysOfWorstSituation";
    public static final String REJECT_REASON_NO_NORDIGEN_RESPONSE = "NoNordigenResponse";
    public static final String REJECT_REASON_IOVATION_RESULT = "IovationResult";
    public static final String REJECT_REASON_WEALTHINESS_NOT_CALCULATED = "WealthinessNotCalculated";
    public static final String REJECT_REASON_NORDIGEN_WEALTHINESS_BELOW_THRESHOLD = "NordigenWealthinessBelowThreshold";
    public static final String REJECT_REASON_NO_TRANSACTIONS_IN_ACCOUNT = "NoTransactionsInAccount";
    public static final String AUTO_APPROVE_REPEATED_CLIENT = "AutoApproveRepeatedClient";

    public static final String CLIENT_ATTRIBUTE_NUMBER_OF_DEPENDANTS = "NumberOfDependants";
    public static final String CLIENT_ATTRIBUTE_FAMILY_STATUS = "FamilyStatus";
    public static final String CLIENT_ATTRIBUTE_EMPLOYMENT_STATUS = "EmploymentStatus";
    public static final String CLIENT_ATTRIBUTE_EMPLOYMENT_DETAIL = "EmploymentDetail";
    public static final String CLIENT_ATTRIBUTE_MONTHLY_INCOME = "MonthlyIncome";
    public static final String CLIENT_ATTRIBUTE_LOAN_PURPOSE = "LoanPurpose";
    public static final String CLIENT_ATTRIBUTE_INCOME_SOURCE = "IncomeSource";
    public static final String CLIENT_ATTRIBUTE_EDUCATION = "Education";
    public static final String CLIENT_ATTRIBUTE_EMPLOYED_SINCE = "EmployedSince";
    public static final String CLIENT_ATTRIBUTE_NEXT_SALARY_DATE = "NextSalaryDate";
    public static final String CLIENT_ATTRIBUTE_WORK_SECTOR = "WorkSector";
    public static final String CLIENT_ATTRIBUTE_MONTHLY_EXPENSES = "MonthlyExpenses";
    public static final String CLIENT_ATTRIBUTE_NETO_INCOME = "NetoIncome";
    public static final String CLIENT_ATTRIBUTE_OCCUPATION = "Occupation";

    public static final String CLIENT_SEGMENT_UNIDENTIFIED = "Unidentified";
    public static final String CLIENT_SEGMENT_AFFILIATE_API_LEAD = "AffiliateApiLead";

    public static final String FILE_DIRECTORY_AGREEMENTS = "Agreements";
    public static final String FILE_DIRECTORY_INVOICES = "Invoices";
    public static final String FILE_DIRECTORY_CERTIFICATES = "Certificates";

    public static final String ATTACHMENT_GROUP_AGREEMENTS = "Agreements";
    public static final String ATTACHMENT_GROUP_UPLOADS = "Uploads";
    public static final String ATTACHMENT_GROUP_LENDING = "Lending";
    public static final String ATTACHMENT_TYPE_UPSELL_AGREEMENT = "UpsellAgreement";
    public static final String ATTACHMENT_TYPE_OTHER = "Other";
    public static final String ATTACHMENT_TYPE_ID_DOCUMENT = "ID Document";
    public static final String ATTACHMENT_TYPE_BANK_ACC_OWNERSHIP = "Bank Account Ownership";
    public static final String ATTACHMENT_TYPE_CLIENT_UPLOAD = "ClientUpload";
    public static final String ATTACHMENT_TYPE_INVOICE = "Invoice";
    public static final String ATTACHMENT_TYPE_PRIVACY_POLICY = "PrivacyPolicy";
    public static final String ATTACHMENT_TYPE_RESCHEDULING_TOC = "ReschedulingToc";

    public static final String STATEMENT_ROW_ATTRIBUTE_DOCUMENT_NUMBER = "DocumentNumber";

    public static final String WEB_ANALYTICS_SIGN_UP_EVENT = "SIGN_UP";
    public static final String WEB_ANALYTICS_LOAN_APPLICATION_EVENT = "LOAN_APPLICATION";
    public static final String WEB_ANALYTICS_ORGANIC_SOURCE = "Organic";

    public static final String TEST_AFFILIATE_NAME = "TEST";
    public static final String TEST_AFFILIATE_API_KEY = "a5d43e2f-e631-46ef-b13d-bfd4bb3c2a4z";

    public static final String RESCHEDULING_FEE_SUB_TYPE = "RESCHEDULING_FEE";
    public static final String PREPAYMENT_FEE_SUB_TYPE = "PREPAYMENT_FEE";

    public static final int LOC_DEFAULT_INVOICE_DAY = 1;
}
