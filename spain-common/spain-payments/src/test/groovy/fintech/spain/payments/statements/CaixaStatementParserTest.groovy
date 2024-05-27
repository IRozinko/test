package fintech.spain.payments.statements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class CaixaStatementParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("statement-test/caixa.xls").getInputStream()

        when:
        def result = new CaixaStatementParser().parse(is)

        then:
        result.accountNumber == "ES6821000844240200657804"
        result.startDate == date("2017-04-30")
        result.endDate == date("2017-04-30")
        result.accountCurrency == "EUR"
        result.rows.size() == 5

        and: "First row"
        with(result.rows[0]) {
            date == date("2017-05-02")
            valueDate == date("2017-04-30")
            description.contains("INGRESO CAJERO")
            amount == 30.00g
            balance == 12649.83
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("INGRESO CAJERO")
            sourceJson.contains("12649.83")
        }

        and: "Second row"
        with(result.rows[1]) {
            date == date("2017-04-30")
            valueDate == date("2017-04-30")
            description.contains("TRASPASO L.ABIERTA")
            amount == 52
            balance == 12619.83

        }

        and: "Last row"
        with(result.rows[4]) {
            amount == 20.00
            balance == 12309.55
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new CaixaStatementParser().parse(is)

        then:
        thrown(IllegalArgumentException.class)
    }
}
