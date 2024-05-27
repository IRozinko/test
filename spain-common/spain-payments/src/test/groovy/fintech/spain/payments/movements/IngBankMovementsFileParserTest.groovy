package fintech.spain.payments.movements

import org.springframework.core.io.ClassPathResource
import spock.lang.Specification

class IngBankMovementsFileParserTest extends Specification {

    def "Parse valid XLS file"() {
        given:
        def is = new ClassPathResource("movements-test/ing.xls").getInputStream()

        when:
        def result = new IngBankMovementsFileParser().parse(is)

        then:
        result
        result.size() == 2
        with(result[0]) {
            amount == 1000.0
            description.contains("LOAN C4761401-001")
        }
        with(result[1]) {
            amount == 503.0
            description.contains("LOAN C5589668-001")
        }
    }

    def "Handle invalid file"() {
        given:
        def is = new ClassPathResource("statement-test/invalid.xls").getInputStream()

        when:
        new IngBankMovementsFileParser().parse(is)

        then:
        thrown(NullPointerException.class)
    }
}
