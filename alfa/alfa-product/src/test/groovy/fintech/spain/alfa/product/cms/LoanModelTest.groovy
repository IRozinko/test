package fintech.spain.alfa.product.cms

import fintech.strategy.model.ExtensionOffer
import spock.lang.Specification

import java.time.temporal.ChronoUnit

class LoanModelTest extends Specification {


    def "extensionModelKey"() {
        given:
        def loanModel = new LoanModel()

        when:
        def key = loanModel.extensionModelKey(ChronoUnit.DAYS, 1)

        then:
        key == "extension1Days"
    }

    def "setExtensions"() {
        given:
        def loanModel = new LoanModel()
        def extensions = [new ExtensionOffer().setPeriodUnit(ChronoUnit.DAYS).setPeriodCount(1).setPrice(100.00),
                          new ExtensionOffer().setPeriodUnit(ChronoUnit.DAYS).setPeriodCount(5).setPrice(300.00),
                          new ExtensionOffer().setPeriodUnit(ChronoUnit.MONTHS).setPeriodCount(1).setPrice(1000.00)]

        when:
        loanModel.setExtensions(extensions)

        then:
        loanModel.extensions['extension1Days'].price == 100.00
        loanModel.extensions['extension5Days'].price == 300.00
        loanModel.extensions['extension1Months'].price == 1000.00
    }
}
