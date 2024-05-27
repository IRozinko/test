package fintech.spain.alfa.product

import fintech.TimeMachine
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.spain.scoring.model.ScoringModelType
import fintech.spain.scoring.spi.ScoringResponse
import spock.lang.Ignore
import spock.lang.Unroll

@Ignore
class ScoringTest extends AbstractAlfaTest {

    AlfaSettings.ScoringSettings settings

    def setup() {
        settings = settingsService.getJson(AlfaSettings.SCORING_SETTINGS, AlfaSettings.ScoringSettings.class)

        settings.newClient.avgDeviationThresholdOfGreenScore = 100.00
        settings.newClient.avgDeviationThresholdOfRedScore = 50.00
        settings.newClient.dedicatedLowerThreshold = 0.5
        settings.newClient.dedicatedUpperThreshold = 0.9

        settings.repeatedClient.avgDeviationThresholdOfGreenScore = 80.00
        settings.repeatedClient.avgDeviationThresholdOfRedScore = 40.00
        settings.repeatedClient.dedicatedLowerThreshold = 0.5
        settings.repeatedClient.dedicatedUpperThreshold = 0.9

        saveJsonSettings(AlfaSettings.SCORING_SETTINGS, settings)
    }

    @Unroll
    def "score bucket is calculated for new client: #dedicatedScore, deviation: #avgDeviation, scoreBucket: #scoreBucket"() {
        given:
        mockSpainScoringProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "{}", dedicatedScore))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()
        def application = workflow.toApplication().getApplication()

        then:
        application.score == avgDeviation
        application.scoreBucket == scoreBucket

        where:
        dedicatedScore | avgDeviation                                      | scoreBucket
        1.0            | (((1.0 - 0.5) / (0.9 - 0.5) * 100)) /* >100.00 */ | "GREEN"
        0.7            | (((0.7 - 0.5) / (0.9 - 0.5) * 100)) /* 50.00 */   | "YELLOW"
        0.6            | (((0.6 - 0.5) / (0.9 - 0.5) * 100)) /* <50.00 */  | "RED"
    }

    @Unroll
    def "score bucket is calculated for repeated client: #dedicatedScore, deviation: #avgDeviation, scoreBucket: #scoreBucket"() {
        given:
        mockSpainScoringProvider.setResponse(ScoringModelType.DEDICATED_MODEL, ScoringResponse.ok(200, "{}", dedicatedScore))

        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .registerDirectly()
            .issueActiveLoan(300.00, 30, TimeMachine.today().minusDays(60))
            .repayAll(TimeMachine.today().minusDays(30))
            .toClient()
            .submitApplicationAndStartFirstLoanWorkflow(300.00, 30, TimeMachine.today())
            .toLoanWorkflow()
            .runAll()
        def application = workflow.toApplication().getApplication()

        then:
        application.score == avgDeviation
        application.scoreBucket == scoreBucket

        where:
        dedicatedScore | avgDeviation                                      | scoreBucket
        0.85           | (((0.85 - 0.5) / (0.9 - 0.5) * 100)) /* >80.00 */ | "GREEN"
        0.68           | (((0.68 - 0.5) / (0.9 - 0.5) * 100)) /* 45.00 */  | "YELLOW"
        0.6            | (((0.6 - 0.5) / (0.9 - 0.5) * 100)) /* <40.00 */  | "RED"
    }
}
