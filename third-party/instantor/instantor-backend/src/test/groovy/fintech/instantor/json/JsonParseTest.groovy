package fintech.instantor.json

import fintech.JsonUtils
import fintech.instantor.json.insight.InstantorInsightResponse
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

class JsonParseTest extends Specification {

    def "Parse JSON to POJOs"() {
        given:
        def json = new ClassPathResource("instantor-insight-example.json").inputStream.text

        when:
        def model = JsonUtils.readValue(json, InstantorInsightResponse.class)

        then:
        model.processStatus == "ok"
        model.accountReportList[0].totalNumberOfTransactions == 100
        model.accountReportList[0].averageAmountOfOutgoingTransactionsWholeMonth == -1838.04
        model.userDetails.getPersonalIdentifier().find { it.name == "dni" }.value == "y6011221r"
    }
}
