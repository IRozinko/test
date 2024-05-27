package fintech.spain.alfa.web.navigation

import fintech.JsonUtils
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.web.AbstractAlfaApiTest
import fintech.spain.alfa.web.ClientApiHelper
import fintech.workflow.ActivityStatus
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.lending.core.application.LoanApplicationSourceType.AFFILIATE
import static fintech.lending.core.application.LoanApplicationSourceType.ORGANIC
import static fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.*

class UnderwritingWFsNavigationTest extends AbstractAlfaApiTest {

    @Autowired
    ClientApiHelper clientApiHelper

    @Autowired
    SettingsService settingsService

    @Unroll
    def "UnderwritingWF #applicationType: #activity -> #state"() {
        given:
        def documentSettings = settingsService.getJson(AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, AlfaSettings.IdDocumentValiditySettings.class)
        documentSettings.setRequestIdUploadForFirstLoan(true)
        documentSettings.setRequestIdUploadForSecondAndLaterLoan(true)
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ID_DOCUMENT_VALIDITY_SETTINGS, textValue: JsonUtils.writeValueAsString(documentSettings)))
        settingsService.update(new UpdatePropertyCommand(name: AlfaSettings.ENABLE_DNI_UPLOADING, booleanValue: true))

        when:
        def wf
        if (applicationType == ORGANIC) {
            wf = TestFactory.newClient().signUp().toLoanWorkflow()
                .manualDecisionEngineOnCondition({ ->
                    wf.getWorkflow().activity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
                })
                .manualScoringResponse()
        } else if (applicationType == AFFILIATE) {
            wf = TestFactory.newAffiliateClient().signUp().toLoanAffiliateWorkflow()
                .manualDecisionEngineOnCondition({ ->
                    wf.getWorkflow().activity(DECISION_ENG_ID_VALIDATION).getStatus() == ActivityStatus.ACTIVE
                })
                .manualScoringResponse()
        } else {
            throw new IllegalArgumentException("Unknown applicationType: ${applicationType}")
        }
        wf.runBeforeActivity(activity)

        and:
        def webToken = clientApiHelper.getClietToken(wf.toClient().clientId)

        then:
        wf.isActivityActive(activity)
        clientApiHelper.getClientInfo(webToken).getState() == state
        clientApiHelper.getClientInfo(webToken).getData() == stateData

        cleanup:
        wf.resetManualScoring().resetDecisionEngine()

        where:
        applicationType | activity                           | state                                   | stateData
        ORGANIC         | APPLICATION_FORM                   | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PERSONAL_DATA      | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | PHONE_VERIFICATION                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PHONE_VERIFICATION | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | ID_DOCUMENT_MANUAL_TEXT_EXTRACTION | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | ID_DOCUMENT_MANUAL_VALIDATION      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | INSTANTOR_MANUAL_CHECK             | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | SCORING_MANUAL_VERIFICATION        | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | DOWJONES_MANUAL_CHECK              | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | DOCUMENT_FORM                      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR          | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | INSTANTOR_REVIEW                   | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR_REVIEW   | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | INSTANTOR_RULES                    | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | LOAN_OFFER_SMS                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_APPROVE_LOAN_OFFER | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | APPROVE_LOAN_OFFER                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_APPROVE_LOAN_OFFER | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | COLLECT_BASIC_INFORMATION          | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | MANDATORY_LENDING_RULES            | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | CHECK_VALID_ID_DOC                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | DNI_DOC_UPLOAD                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_DNI_DOC_UPLOAD     | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | DECISION_ENG_ID_VALIDATION         | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | DOWJONES                           | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | BASIC_LENDING_RULES                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | PRESTO_CROSSCHECK                  | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | PRESTO_CROSSCHECK_RULES            | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | IOVATION_RUN_1                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | IOVATION_CHECK_REPEATED_RUN_1      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | IOVATION_RULES_RUN_1               | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | INSTANTOR_CALLBACK                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | EQUIFAX_RUN_1                      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | EQUIFAX_RULES_RUN_1                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | EXPERIAN_CAIS_RESUMEN_RUN_1        | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | EXPERIAN_CAIS_OPERACIONES_RUN_1    | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | EXPERIAN_RULES_RUN_1               | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        ORGANIC         | ISSUE_LOAN                         | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | EXPORT_DISBURSEMENT                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        ORGANIC         | WAITING_EXPORT_DISBURSEMENT        | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                         | [INSTANTOR_ATTEMPTS: 1]

        AFFILIATE       | APPLICATION_FORM                   | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PERSONAL_DATA      | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | PHONE_VERIFICATION                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PHONE_VERIFICATION | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | ID_DOCUMENT_MANUAL_TEXT_EXTRACTION | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | ID_DOCUMENT_MANUAL_VALIDATION      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | INSTANTOR_MANUAL_CHECK             | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | SCORING_MANUAL_VERIFICATION        | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | DOWJONES_MANUAL_CHECK              | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_MANUAL_TASK        | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | DOCUMENT_FORM                      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR          | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | INSTANTOR_REVIEW                   | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_INSTANTOR_REVIEW   | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | INSTANTOR_RULES                    | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | LOAN_OFFER_SMS                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_APPROVE_LOAN_OFFER | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | APPROVE_LOAN_OFFER                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_APPROVE_LOAN_OFFER | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | COLLECT_BASIC_INFORMATION          | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | MANDATORY_LENDING_RULES            | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | CHECK_VALID_ID_DOC                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | DNI_DOC_UPLOAD                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_DNI_DOC_UPLOAD     | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | DECISION_ENG_ID_VALIDATION         | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | DOWJONES                           | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | BASIC_LENDING_RULES                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | PRESTO_CROSSCHECK                  | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | PRESTO_CROSSCHECK_RULES            | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | IOVATION_RUN_1                     | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | IOVATION_CHECK_REPEATED_RUN_1      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | IOVATION_RULES_RUN_1               | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | INSTANTOR_CALLBACK                 | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | EQUIFAX_RUN_1                      | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | EQUIFAX_RULES_RUN_1                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | EXPERIAN_CAIS_RESUMEN_RUN_1        | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | EXPERIAN_CAIS_OPERACIONES_RUN_1    | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | EXPERIAN_RULES_RUN_1               | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 0]
        AFFILIATE       | ISSUE_LOAN                         | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | EXPORT_DISBURSEMENT                | fintech.spain.alfa.web.services.navigation.UiState.REGISTRATION_PROCESSING         | [INSTANTOR_ATTEMPTS: 1]
        AFFILIATE       | WAITING_EXPORT_DISBURSEMENT        | fintech.spain.alfa.web.services.navigation.UiState.PROFILE                         | [INSTANTOR_ATTEMPTS: 1]
    }

}
