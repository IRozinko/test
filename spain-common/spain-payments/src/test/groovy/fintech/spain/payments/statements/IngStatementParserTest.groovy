package fintech.spain.payments.statements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class IngStatementParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("statement-test/ing.xls").getInputStream()

        when:
        def result = new IngStatementParser().parse(is)

        then:
        result.accountNumber == "ES7814650120391900214751"
        result.startDate == date("2017-04-20")
        result.endDate == date("2017-04-30")
        result.accountCurrency == "EUR"
        result.rows.size() == 200

        and: "First row"
        with(result.rows[0]) {
            date == date("2017-04-30")
            valueDate == date("2017-04-30")
            description.contains("Traspaso recibido LUZ ELENA LOSADA DOMINGUEZ")
            description.contains("35096606D PRORROGA")
            amount == 130.00g
            balance == 13360.69g
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("35096606D PRORROGA")
            sourceJson.contains("13.360,69")
            sourceJson.contains("30/04/2017")
        }

        and: "Last row"
        with(result.rows[199]) {
            amount == -450.00g
            balance == 11773.85g
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new IngStatementParser().parse(is)

        then:
        thrown(IllegalArgumentException.class)
    }
}
