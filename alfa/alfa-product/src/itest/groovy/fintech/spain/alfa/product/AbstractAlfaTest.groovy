package fintech.spain.alfa.product

import fintech.JsonUtils
import fintech.TimeMachine
import fintech.cms.PdfRenderer
import fintech.crm.attachments.ClientAttachmentService
import fintech.dowjones.impl.MockDowJonesProviderBean
import fintech.iovation.impl.MockIovationProvider
import fintech.nordigen.impl.MockNordigenProvider
import fintech.risk.checklist.CheckListService
import fintech.settings.SettingsService
import fintech.settings.commands.UpdatePropertyCommand
import fintech.spain.crosscheck.impl.MockSpainCrosscheckProvider
import fintech.spain.equifax.mock.MockEquifaxProvider
import fintech.spain.equifax.mock.MockedEquifaxResponse
import fintech.spain.experian.impl.cais.MockExperianCaisProvider
import fintech.spain.scoring.SpainScoringService
import fintech.spain.scoring.impl.MockSpainScoringProvider
import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.spi.ScoringResponse
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationEventPublisher

abstract class AbstractAlfaTest extends AbstractBaseSpecification {

    @Autowired
    CrmAlfaSetup alfaSetup

    @Autowired
    MockIovationProvider mockIovationProvider

    @Autowired
    MockEquifaxProvider mockEquifaxProvider

    @Autowired
    MockExperianCaisProvider mockExperianCaisProvider

    @Autowired
    MockNordigenProvider mockNordigenProvider

    @Autowired
    MockSpainScoringProvider mockSpainScoringProvider

    @Autowired
    MockSpainCrosscheckProvider mockSpainCrosscheckProvider

    @Autowired
    SettingsService settingsService

    @Autowired
    PdfRenderer pdfRenderer

    @Autowired
    CheckListService checkListService

    @Autowired
    ClientAttachmentService clientAttachmentService

    @Autowired
    ApplicationEventPublisher eventPublisher

    @Autowired
    fintech.spain.alfa.product.extension.discounts.ExtensionDiscountService extensionDiscountService

    @Autowired
    MockDowJonesProviderBean mockDowJonesProvider

    @Autowired
    SpainScoringService scoringService

    def setup() {
        testDatabase.cleanDb([])
        alfaSetup.setUp()

        mockSpainCrosscheckProvider.setThrowError(false)
        mockSpainCrosscheckProvider.setResponse(MockSpainCrosscheckProvider.notFoundResponse())
        mockIovationProvider.response = MockIovationProvider.generateResponse("A")
        mockIovationProvider.throwError = false
        mockEquifaxProvider.setResponseSupplier(MockedEquifaxResponse.DEFAULT)
        mockEquifaxProvider.throwError = false
        mockExperianCaisProvider.setListOperacionesResponseSource(MockExperianCaisProvider.OPERACIONES_RESPONSE_NOT_FOUND)
        mockExperianCaisProvider.setResumenResponseResource(MockExperianCaisProvider.RESUMEN_RESPONSE_NOT_FOUND)
        mockExperianCaisProvider.throwError = false
        mockNordigenProvider.setResponse(MockNordigenProvider.okResponse(fintech.spain.alfa.product.testing.RandomData.randomDni().toString()))
        mockNordigenProvider.throwError = false
        mockSpainScoringProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "MOCK", 1.0))
        mockSpainScoringProvider.setResponse(ScoringModelType.LINEAR_REGRESSION_MODEL, ScoringResponse.ok(200, "MOCK", 1.0))
        mockSpainScoringProvider.setResponse(ScoringModelType.CREDIT_LIMIT_MODEL, ScoringResponse.ok(200, "MOCK", 1.0))
        mockSpainScoringProvider.throwError = false
        pdfRenderer.disableRendering(true)
        mockDowJonesProvider.setResponseResource(MockDowJonesProviderBean.SUCCESS_RESPONSE)
        mockDowJonesProvider.throwError = false

        TimeMachine.useDefaultClock()
    }

    def cleanup() {
        TimeMachine.useDefaultClock()
    }

    void saveJsonSettings(String name, Object settings) {
        settingsService.update(new UpdatePropertyCommand(name: name, textValue: JsonUtils.writeValueAsString(settings)))
    }
}
