package fintech.spain.unnax.transfer.model

import spock.lang.Specification

class   TransferAutoTypeTest extends Specification {

    def "Type order"() {
        expect:
        TransferAutoType.STANDARD.getNumeric() == 0
        TransferAutoType.SAME_DAY.getNumeric() == 1
    }

}
