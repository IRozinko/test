package fintech.spain.alfa.product.workflow.undewrtiting;

import fintech.workflow.spi.WorkflowDefinition;


public interface UnderwritingWorkflows {

    String FIRST_LOAN = "UnderwritingFirstLoan";
    String FIRST_LOAN_AFFILIATE = "UnderwritingFirstLoanAffiliate";

    WorkflowDefinition firstLoanWorkflow();

    //TODO these steps definition should be on implementation side
    //but because they are  tide coupled to other components kept them here
    class Activities {
        public static final String APPLICATION_FORM = "ApplicationForm";
        public static final String COLLECT_BASIC_INFORMATION = "CollectBasicInformation";
        public static final String MANDATORY_LENDING_RULES = "MandatoryLendingRules";
        public static final String BASIC_LENDING_RULES = "BasicLendingRules";
        public static final String PRESTO_CROSSCHECK = "PrestoCrosscheck";
        public static final String PRESTO_CROSSCHECK_RULES = "PrestoCrosscheckRules";
        public static final String PHONE_VERIFICATION = "PhoneVerification";
        @Deprecated
        public static final String IOVATION_BLACKBOX = "IovationBlackBox";
        @Deprecated
        public static final String IOVATION = "Iovation";
        @Deprecated
        public static final String IOVATION_CHECK_REPEATED = "IovationCheckRepeated";
        @Deprecated
        public static final String IOVATION_RULES = "IovationRules";

        public static final String IOVATION_BLACKBOX_RUN_1 = "IovationBlackBoxRun1";

        @Deprecated
        public static final String IOVATION_BLACKBOX_RUN_AFFILIATES = "IovationBlackBoxRunAffiliates";
        public static final String IOVATION_RUN_1 = "IovationRun1";
        public static final String IOVATION_CHECK_REPEATED_RUN_1 = "IovationCheckRepeatedRun1";
        public static final String IOVATION_RULES_RUN_1 = "IovationRulesRun1";
        public static final String IOVATION_BLACKBOX_RUN_2 = "IovationBlackBoxRun2";
        public static final String IOVATION_RUN_2 = "IovationRun2";
        public static final String IOVATION_CHECK_REPEATED_RUN_2 = "IovationCheckRepeatedRun2";
        public static final String IOVATION_RULES_RUN_2 = "IovationRulesRun2";
        public static final String DOCUMENT_FORM = "DocumentForm";
        public static final String INSTANTOR_FORM = "InstantorForm";
        public static final String INSTANTOR_CALLBACK = "InstantorCallback";
        public static final String INSTANTOR_RULES = "InstantorRules";
        public static final String INSTANTOR_REVIEW = "InstantorReview";
        public static final String INSTANTOR_MANUAL_CHECK = "InstantorManualCheck";
        public static final String CREDIT_LIMIT = "CreditLimit";
        public static final String ASSIGN_STRATEGIES = "AssignStrategies";
        public static final String REVALIDATE_ID_DOC = "RevalidateIdDoc";
        public static final String CHECK_VALID_ID_DOC = "CheckValidIdDoc";
        public static final String INGLOBALY = "Inglobaly";
        public static final String INGLOBALY_RULES = "InglobalyRules";
        public static final String EXPERIAN_CAIS_RESUMEN_RUN_1 = "ExperianCaisResumenRun1";
        public static final String EXPERIAN_CAIS_OPERACIONES_RUN_1 = "ExperianCaisOperacionesRun1";
        public static final String EXPERIAN_RULES_RUN_1 = "ExperianRulesRun1";
        public static final String EQUIFAX_RUN_1 = "EquifaxRun1";
        public static final String EQUIFAX_RULES_RUN_1 = "EquifaxRulesRun1";
        public static final String EXPERIAN_CAIS_RESUMEN_RUN_2 = "ExperianCaisResumenRun2";
        public static final String EXPERIAN_CAIS_OPERACIONES_RUN_2 = "ExperianCaisOperacionesRun2";
        public static final String EXPERIAN_RULES_RUN_2 = "ExperianRulesRun2";
        public static final String EQUIFAX_RUN_2 = "EquifaxRun2";
        public static final String EQUIFAX_RULES_RUN_2 = "EquifaxRulesRun2";
        public static final String NORDIGEN_RULES = "NordigenRules";
        public static final String WEALTHINESS_CHECK = "WealthinessCheck";
        public static final String DOCUMENT_CHECK = "DocumentCheck";

        public static final String REQUEST_SCORE = "RequestScore";
        public static final String SCORING_DECISION = "ScoringDecision";
        public static final String SCORING_MANUAL_VERIFICATION = "ScoringManualVerification";

        public static final String PREPARE_OFFER = "PrepareOffer";
        public static final String UPSELL_AVAILABLE = "UpsellAvailable";
        public static final String LOAN_WITH_UPSELL_OFFER_EMAIL = "LoanWithUpsellOfferEmail";
        public static final String LOAN_WITH_UPSELL_OFFER_SMS = "LoanWithUpsellOfferSms";
        public static final String LOAN_OFFER_EMAIL = "LoanOfferEmail";
        public static final String LOAN_OFFER_SMS = "LoanOfferSms";
        public static final String APPROVE_LOAN_OFFER = "ApproveLoanOffer";
        public static final String ID_DOCUMENT_MANUAL_TEXT_EXTRACTION = "IdDocumentManualTextExtraction";
        public static final String DECISION_ENG_ID_VALIDATION = "DecisionEngIdValidation";
        public static final String DECISION_ENG_ID_VALIDATION_2 = "DecisionEngIdValidation2";
        public static final String ID_DOCUMENT_MANUAL_VALIDATION = "IdDocumentManualValidation";
        public static final String DNI_DOC_UPLOAD = "DniDocUpload";
        public static final String ISSUE_LOAN = "IssueLoan";
        public static final String EXPORT_DISBURSEMENT = "ExportDisbursement";
        public static final String WAITING_EXPORT_DISBURSEMENT = "WaitingExportDisbursement";
        public static final String UNRESPONSIVE_BUREAU_RULE = "UnresponsiveBureauRules";
        public static final String DOWJONES = "DowJones";
        public static final String DOWJONES_MANUAL_CHECK = "DowJonesManualCheck";
    }
}
