package fintech.spain.alfa.product

import fintech.instantor.InstantorSimulation
import org.apache.commons.lang3.StringUtils
import spock.lang.Unroll

class InstantorRulesTest extends AbstractAlfaTest {

    @Unroll
    def "instantor dni rule - missing instantor dni"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        and:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .setInstantorResponse(InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, dni, client.fullName(), client.iban.toString()))
            .runAll()

        then:
        workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL

        where:
        dni   | _
        ""    | _
        "N/A" | _
        "n/a" | _
    }

    def "instantor dni rule - name similarity no match enough"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        and:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .setInstantorResponse(InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, StringUtils.left(client.dni, 8) + "_", client.fullName(), client.iban.toString()))
            .runAll()

        then:
        workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.MANUAL
    }

    def "instantor dni rule - name similarity no match"() {
        when:
        def client = fintech.spain.alfa.product.testing.TestFactory.newClient()

        and:
        def workflow = client
            .signUp()
            .toLoanWorkflow()
            .setInstantorResponse(InstantorSimulation.simulateOkResponseWithSingleAccount(client.clientId, StringUtils.left(client.dni, 5) + "____", client.fullName(), client.iban.toString()))
            .runAll()

        then:
        workflow.getActivityResolution(fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows.Activities.INSTANTOR_RULES) == fintech.spain.alfa.product.workflow.common.Resolutions.REJECT
    }
}
