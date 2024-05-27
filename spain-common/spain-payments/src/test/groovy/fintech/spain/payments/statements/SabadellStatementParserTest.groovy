package fintech.spain.payments.statements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class SabadellStatementParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("statement-test/sabadell-new.xls").getInputStream()

        when:
        def result = new SabadellStatementParser().parse(is)

        then:
        result.accountNumber == "ES2500815029140002457055"
        result.startDate == date("2020-04-21")
        result.endDate == date("2020-07-20")
        result.accountCurrency == "EUR"
        result.rows.size() == 450

        and: "First row"
        with(result.rows[0]) {
            date == date("2020-07-20")
            valueDate == date("2020-07-20")
            description.contains("JOSE PRIETO VAZQ")
            amount == 608.00
            balance == 4409.12
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("20/07/2020")
            sourceJson.contains("DE JOSE PRIETO VAZQUEZ")
        }

        and: "Row with reference fields"
        with(result.rows[18]) {
            balance == 3069.10
            description.contains("INGRESO EFECTIVO CAJERO AUTOMATICO 008122130001/34257678Y PRORROGAR")
        }

        and: "Row with different dates"
        with(result.rows[21]) {
            balance == 2882.10
            date == date("2020-07-14")
            valueDate == date("2020-07-13")
        }

        and: "Last row"
        with(result.rows[448]) {
            amount == 50.00
            balance == 2518.84
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new SabadellStatementParser().parse(is)

        then:
        thrown(IllegalArgumentException.class)
    }
}
