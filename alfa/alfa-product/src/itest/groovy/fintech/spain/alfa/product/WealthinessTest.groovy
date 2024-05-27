package fintech.spain.alfa.product

import fintech.ClasspathUtils
import fintech.DateUtils
import fintech.TimeMachine
import fintech.instantor.model.InstantorResponseStatus
import fintech.instantor.model.SaveInstantorResponseCommand
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.nordigen.impl.NordigenResponse
import fintech.spain.alfa.product.db.Entities
import fintech.spain.alfa.product.db.WealthinessCategoryRepository
import fintech.spain.alfa.product.db.WealthinessRepository
import fintech.spain.alfa.product.settings.AlfaSettings
import fintech.workflow.ActivityStatus
import org.apache.commons.lang3.StringUtils
import org.iban4j.Iban
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Ignore

@Ignore
class WealthinessTest extends AbstractAlfaTest {

    @Autowired
    WealthinessRepository wealthinessRepository

    @Autowired
    WealthinessCategoryRepository categoryRepository

    def setup() {
        TimeMachine.useFixedClockAt(DateUtils.date("2017-09-25"))

        def settings = new AlfaSettings.WealthinessCalculationSettings(
            monthsToCheck: 2,
            thresholds: [
                new AlfaSettings.WealthinessCalculationSettings.Threshold(
                    approveThreshold: 75.00g,
                    rejectThreshold: 1.00g,
                    scoringBuckets: ["GREEN"],
                    categories: ["Salary", "Gambling"]
                ),
                new AlfaSettings.WealthinessCalculationSettings.Threshold(
                    approveThreshold: 75.00g,
                    rejectThreshold: -1000.00g,
                    scoringBuckets: ["YELLOW"],
                    categories: ["Salary", "Gambling"]
                ),
            ],
            categories: [
                new AlfaSettings.WealthinessCalculationSettings.Category(name: "Salary", nordigenCategories: [1, 2], weightInPercent: 50.00),
                new AlfaSettings.WealthinessCalculationSettings.Category(name: "Gambling", nordigenCategories: [3], weightInPercent: -25.00),
            ],
            fragmentToExcludeFromTransactionDetails: [
                "fuck; ", "Description:"
            ]
        )
        saveJsonSettings(AlfaSettings.WEALTHINESS_CALCULATION_SETTINGS, settings)
    }

    def "wealthiness check process"() {
        when:
        // as in wealthiness-instantor.json file
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .setFirstName("John")
            .setLastName("Smith")
            .setDni("48805442V")
            .setIban(Iban.valueOf("ES4314650150511714695458"))
            .signUp()
        def instantorPayload = StringUtils.replace(ClasspathUtils.resourceToString("wealthiness-instantor.json"), "#clientId#", "" + client.getClientId())
        def workflow = client
            .toLoanWorkflow()
            .setInstantorResponse(new SaveInstantorResponseCommand(status: InstantorResponseStatus.OK, payloadJson: instantorPayload))
            .setNordigenResponse(NordigenResponse.ok(200, ClasspathUtils.resourceToString("wealthiness-nordigen.json")))
            .runAll()
            .exportDisbursement()
        workflow.print()

        then:
        assert categoryRepository.count() == 2 // salary and gambling
        def salary = categoryRepository.findOne(Entities.category.category.eq("Salary"))
        def gambling = categoryRepository.findOne(Entities.category.category.eq("Gambling"))

        assert salary.manualWeightedWealthiness == ((100.00g + 200.00g) / 2) * 0.5g // 75.00g
        assert salary.nordigenWeightedWealthiness == ((100.00g + 200.00g) / 2) * 0.5g // 1.25g

        assert gambling.manualWeightedWealthiness == ((10.00g + -20.00g) / 2) * -0.25g
        assert gambling.nordigenWeightedWealthiness == ((10.00g + -20.00g) / 2) * -0.25g

        and:
        assert wealthinessRepository.count() == 1
        def wealthiness = wealthinessRepository.findAll()[0]
        assert wealthiness.manualWeightedWealthiness == 75.00g + 1.25g
        assert wealthiness.nordigenWeightedWealthiness == 75.00g + 1.25g

        and: "auto approved, no need for manual wealthiness check task"
        workflow.getActivityStatus(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.WEALTHINESS_CHECK) == ActivityStatus.CANCELLED
        workflow.toApplication().getStatusDetail() == LoanApplicationStatusDetail.APPROVED
    }

    def "auto reject"() {
        when:
        def workflow = fintech.spain.alfa.product.testing.TestFactory.newClient()
            .signUp()
            .toLoanWorkflow()
            .runAll()

        then: "by default, wealthiness value is 0 and in settings threshold is 1"
        workflow.isTerminated()
        workflow.toApplication().getCloseReason() == AlfaConstants.REJECT_REASON_NORDIGEN_WEALTHINESS_BELOW_THRESHOLD
    }
}
