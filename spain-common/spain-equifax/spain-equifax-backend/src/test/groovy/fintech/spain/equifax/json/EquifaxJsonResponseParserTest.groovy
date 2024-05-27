package fintech.spain.equifax.json

import fintech.JsonUtils
import fintech.spain.equifax.json.client.EquifaxJsonResponse
import fintech.spain.equifax.model.EquifaxStatus
import spock.lang.Specification
import spock.lang.Subject

import static org.springframework.util.ResourceUtils.getFile

class EquifaxJsonResponseParserTest extends Specification {

    @Subject
    EquifaxJsonResponseParser parser = new EquifaxJsonResponseParser()

    def "Parse - Not Found"() {
        given:
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_NoPresence.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.NOT_FOUND
        }
    }

    def "Parse - Validation Error"() {
        given:
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_IdValidationError.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.ERROR
            error.contains("Invalid id")
        }
    }

    def "Parse - Field Error"() {
        given:
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_CompulsoryFieldNeeded.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.ERROR
            error.contains("Errors present in [EIPG] response: [code: REQUESTERROR, description: The request sent by the client was syntactically incorrect]")
        }
    }

    def "Parse - Data source error"() {
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_DSError.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.ERROR
            error.contains("Errors present in [EIPG] response")
        }
    }

    def "Parse - Found"() {
        given:
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_Presence.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.FOUND
            totalNumberOfOperations == 1
            numberOfConsumerCreditOperations == 2
            numberOfMortgageOperations == 3
            numberOfPersonalLoanOperations == 4
            numberOfCreditCardOperations == 5
            numberOfTelcoOperations == 6
            totalNumberOfOtherUnpaid == 7
            totalUnpaidBalance == 8.0
            unpaidBalanceOwnEntity == 9.0
            unpaidBalanceOfOther == 10.0
            unpaidBalanceOfConsumerCredit == 11.0
            unpaidBalanceOfMortgage == 12.0
            unpaidBalanceOfPersonalLoan == 13.0
            unpaidBalanceOfCreditCard == 14.0
            unpaidBalanceOfTelco == 15.0
            unpaidBalanceOfOtherProducts == 16.0
            worstUnpaidBalance == 17.0
            worstSituationCode == '18'
            numberOfDaysOfWorstSituation == 19
            numberOfCreditors == 20
            delincuencyDays == 21
        }
    }

    def "Parse - Risk score and scoring category"() {
        given:
        def response = JsonUtils.readValue(getFile("classpath:equifax/json/RESPONSE_4F_attributes.json").text,
            EquifaxJsonResponse.class)

        when:
        def parsed = parser.parse(response)

        then:
        parsed
        with(parsed) {
            status == EquifaxStatus.FOUND
            riskScore == "1"
            scoringCategory == "A"
        }
    }
}
