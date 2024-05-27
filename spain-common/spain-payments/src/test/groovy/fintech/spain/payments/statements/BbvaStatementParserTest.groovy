package fintech.spain.payments.statements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class BbvaStatementParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("statement-test/bbva.xls").getInputStream()

        when:
        def result = new BbvaStatementParser().parse(is)

        then:
        result.accountNumber == "ES3501821797310203745924"
        result.startDate == date("2017-03-31")
        result.endDate == date("2017-05-02")
        result.accountCurrency == "EUR"
        result.rows.size() == 603

        and: "First row"
        with(result.rows[0]) {
            date == date("2017-04-28")
            valueDate == date("2017-04-28")
            transactionCode == "0007"
            description == "REQUEST ID 516395"
            amount == -600.00g
            balance == 4994.17g
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("0007")
            sourceJson.contains("0171")
        }

        and: "Date and booking dates do not match"
        with(result.rows[10]) {
            date == date("2017-05-02")
            valueDate == date("2017-04-28")
            transactionCode == "0009"
            description == "10833858F  PRORROGA"
            amount == 156.00g
            balance == 7065.06g
        }

        and: "Last row"
        with(result.rows[602]) {
            date == date("2017-03-31")
            valueDate == date("2017-04-03")
            balance == 1963.43g
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new BbvaStatementParser().parse(is)

        then:
        thrown(IllegalArgumentException.class)
    }
}
