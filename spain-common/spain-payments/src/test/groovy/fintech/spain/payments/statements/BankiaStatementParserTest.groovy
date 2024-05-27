package fintech.spain.payments.statements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class BankiaStatementParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("statement-test/bankia.xls").getInputStream()

        when:
        def result = new BankiaStatementParser().parse(is)

        then:
        result.accountNumber == "ES9820389261996000253613"
        result.startDate == date("2020-09-22")
        result.endDate == date("2020-09-23")
        result.accountCurrency == "EUR"
        result.rows.size() == 40

        and: "First row"
        with(result.rows[0]) {
            date == date("2020-09-23")
            valueDate == date("2020-09-23")
            description.contains("TRANSFERENCIA DE MARIANO VILLALBA SAN")
            amount == 26.00
            balance == 42023.12
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("BENEFICIARIO : alfa sl")
            sourceJson.contains("42023.12")
        }

        and: "Second row"
        with(result.rows[1]) {
            date == date("2020-09-23")
            valueDate == date("2020-09-23")
            description.contains("TRANSFERENCIA A Miguel Angel Aranda Ferreiro")
            amount == -200.00
            balance == 41997.12
        }
        and: "Last row"
        with(result.rows[39]) {
            date == date("2020-09-23")
            valueDate == date("2020-09-23")
            amount == -500.00
            balance == 46190.06
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new BankiaStatementParser().parse(is)

        then:
        thrown(IllegalArgumentException.class)
    }
}
