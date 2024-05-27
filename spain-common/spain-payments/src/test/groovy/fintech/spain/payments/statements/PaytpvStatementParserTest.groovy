package fintech.spain.payments.statements

import fintech.lending.creditline.TransactionConstants
import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

import static fintech.DateUtils.date

class PaytpvStatementParserTest extends Specification {

    def "Parse valid CSV file"() {
        given:
        def is = new ClassPathResource("statement-test/paytpv.csv").getInputStream()

        when:
        def result = new PayTpvCsvStatementParser("ACCOUNT01", "DNI").parse(is)

        then:
        result.accountNumber == "ACCOUNT01"
        result.startDate == date("2017-11-08")
        result.endDate == date("2017-11-09")
        result.accountCurrency == "EUR"
        result.rows.size() == 7

        and: "First row"
        with(result.rows[0]) {
            date == date("2017-11-09")
            valueDate == date("2017-11-09")
            description == new String("Autorización\n77617882N\n201711071201339710\nXXX...1014".bytes, "ISO-8859-1")
            amount == 49.94g
            balance == 0.00g
            currency == "EUR"
            accountNumber == result.accountNumber
            sourceJson.contains("77617882N")
            sourceJson.contains("VISA")
            reference == "201711071201339710"
            uniqueKey == "0014087123|9/11/2017 2:01:34|201711071201339710|49,94"
            attributes["DNI"] == "77617882N"
        }

        and: "5th row - support legacy unique key"
        with(result.rows[4]) {
            date == date("2017-11-08")
            valueDate == date("2017-11-08")
            description == new String("Autorización\nPresto 2017-10-20 C8566540-001-1\nXXX...9002".bytes, "ISO-8859-1")
            amount == 77.61g
            reference == "Presto 2017-10-20 C8566540-001-1"
            uniqueKey == "Presto 2017-10-20 C8566540-001-1"
            attributes["DNI"] == null
        }

        and: "Before last row"
        with(result.rows[5]) {
            valueDate == date("2017-11-09")
            description == "Retirada de fondos"
            amount == -3913.39g
            reference == ""
            uniqueKey == "0000000000|09/11/2017 00:15:01||-3.913,39"
            attributes["DNI"] == null
            suggestedTransactionSubType == TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER
        }
    }
}
