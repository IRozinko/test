package fintech.spain.alfa.product.payments.processors

import spock.lang.Specification

class NumberMatcherTest extends Specification {

    def "extract client dni"() {
        expect:
        !NumberMatcher.extractClientDni(null).isPresent()
        !NumberMatcher.extractClientDni("").isPresent()
        !NumberMatcher.extractClientDni("38478942 B").isPresent()
        NumberMatcher.extractClientDni("38478942B").get() == "38478942B"
        NumberMatcher.extractClientDni("Repaying loan 38478942B (some text)").get() == "38478942B"
        NumberMatcher.extractClientDni("Repaying loan 38478942b (some text)").get() == "38478942B"
        NumberMatcher.extractClientDni("Repaying loan Y2798273S (some text)").get() == "Y2798273S"
        NumberMatcher.extractClientDni("38478942B Y2798273S").get() == "38478942B"
    }

    def "extract client number"() {
        expect:
        !NumberMatcher.extractClientNumber(null).isPresent()
        !NumberMatcher.extractClientNumber("").isPresent()
        !NumberMatcher.extractClientNumber("T123456").isPresent()
        !NumberMatcher.extractClientNumber("T 1234567").isPresent()
        NumberMatcher.extractClientNumber("Repaying loan T1234567 (some text)").get() == "T1234567"
        NumberMatcher.extractClientNumber("Repaying loan T1234567-001 (some text)").get() == "T1234567"
        NumberMatcher.extractClientNumber("Repaying loan T1234567-001 (some text)").get() == "T1234567"
        NumberMatcher.extractClientNumber("Repaying loan T1234567-C001-C001 (some text)").get() == "T1234567"
    }

    def "extract loan number"() {
        expect:
        !NumberMatcher.extractLoanNumber(null).isPresent()
        !NumberMatcher.extractLoanNumber("").isPresent()
        !NumberMatcher.extractLoanNumber("T123456-001").isPresent()
        !NumberMatcher.extractLoanNumber("T1234567-01").isPresent()
        !NumberMatcher.extractLoanNumber("C 1234567-001").isPresent()
        NumberMatcher.extractLoanNumber("Repaying loan T1234567-001 (some text)").get() == "T1234567-001"
        NumberMatcher.extractLoanNumber("Repaying loan T1234567\n-001 (some text)").get() == "T1234567-001"
        NumberMatcher.extractLoanNumber("Repaying loan T1234567-001 (some text)").get() == "T1234567-001"
        NumberMatcher.extractLoanNumber("Repaying loan T1234567-001-01 (some text)").get() == "T1234567-001"
    }

    def "extract disbursement reference"() {
        expect:
        NumberMatcher.extractDisbursementReference("p-135295739645-p").get() == "p-135295739645-p"
        NumberMatcher.extractDisbursementReference("P-135295739645-P").get() == "p-135295739645-p"
        NumberMatcher.extractDisbursementReference("P-135295739645-p").get() == "p-135295739645-p"
        NumberMatcher.extractDisbursementReference("ppp-135295739645-paaaa").get() == "p-135295739645-p"
        !NumberMatcher.extractDisbursementReference("ppp-135295739645").isPresent()
        !NumberMatcher.extractDisbursementReference("135295739645").isPresent()
        !NumberMatcher.extractDisbursementReference("p135295739645-p").isPresent()
        !NumberMatcher.extractDisbursementReference("").isPresent()
        !NumberMatcher.extractDisbursementReference(null).isPresent()
    }

    def "extract disbursement msg id"() {
        expect:
        NumberMatcher.extractDisbursementMsgId("20180706085323_QW7").get() == "20180706085323_QW7"
        NumberMatcher.extractDisbursementMsgId("Transferencia emitida\n" +
            "20180706085323_QW7").get() == "20180706085323_QW7"

        !NumberMatcher.extractDisbursementReference("Transferencia emitida").isPresent()
        !NumberMatcher.extractDisbursementReference("").isPresent()
        !NumberMatcher.extractDisbursementReference(null).isPresent()
    }
}
