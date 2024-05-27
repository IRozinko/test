package fintech.spain.payments.statements


import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class UnnaxPayInStatementParserTest extends Specification {


    def "Parse UNNAX CSV file - EN localization"() {
        given:
        def is = new ClassPathResource("statement-test/UNNAX_Payin_orders_05_08_2020.csv").getInputStream()

        when:
        def result = new UnnaxPayInStatementParser("ACCOUNT01").parse(is)

        then:
        result.accountNumber == "ACCOUNT01"
        result.startDate == date("2020-05-08")
        result.endDate == date("2020-05-08")
        result.accountCurrency == "EUR"
        result.rows.size() == 1

        and: "First row"
        with(result.rows[0]) {
            date == date("2020-05-08")
            valueDate == date("2020-05-08")
            description == new String("UNX C86507773484".bytes, "ISO-8859-1")
            amount == 5000.00
            balance == 0.00
            currency == "EUR"
            accountNumber == result.accountNumber
            reference == "Loan repayment C8650777-001"
            uniqueKey == "C86507773484"
        }
    }
    
    def "Parse valid Direct Payment CSV file"() {
        given:
        def is = new ClassPathResource("statement-test/UNNAX_Payin_DirectPayment.csv").getInputStream()

        when:
        def result = new UnnaxPayInStatementParser("ACCOUNT01").parse(is)

        then:
        result.accountNumber == "ACCOUNT01"
        result.startDate == date("2020-02-28")
        result.endDate == date("2020-03-17")
        result.accountCurrency == "EUR"
        result.rows.size() == 22

        and: "First row"
        with(result.rows[0]) {
            date == date("2020-03-17")
            valueDate == date("2020-03-17")
            description == new String("UNX ouzqlnkb".bytes, "ISO-8859-1")
            amount == 135.00
            balance == 0.00
            currency == "EUR"
            accountNumber == result.accountNumber
            reference == "20922144X -  TOTAL - 17/03/2020 11:14:47"
            uniqueKey == "ouzqlnkb"
        }

        and: "Last row"
        with(result.rows[21]) {
            date == date("2020-02-28")
            valueDate == date("2020-02-28")
            description == new String("UNX 79412".bytes, "ISO-8859-1")
            amount == 0.00
            balance == 0.00
            currency == "EUR"
            accountNumber == result.accountNumber
            reference == "Unnax V3 Card Preauthotize"
            uniqueKey == "79412"
        }
    }

    def "Parse valid Credit Card Payment CSV file"() {
        given:
        def is = new ClassPathResource("statement-test/UNNAX_Payin_CC.csv").getInputStream()

        when:
        def result = new UnnaxPayInStatementParser("ACCOUNT01").parse(is)

        then:
        result.accountNumber == "ACCOUNT01"
        result.startDate == date("2020-03-19")
        result.endDate == date("2020-03-19")
        result.accountCurrency == "EUR"
        result.rows.size() == 1

        and: "First row"
        with(result.rows[0]) {
            date == date("2020-03-19")
            valueDate == date("2020-03-19")
            description == new String("UNX acezw".bytes, "ISO-8859-1")
            amount == 1.00
            balance == 0.00
            currency == "EUR"
            accountNumber == result.accountNumber
            reference == "39917607A -  CUSTOM - 19/03/2020 13:29:31"
            uniqueKey == "acezw"
        }

    }
}
