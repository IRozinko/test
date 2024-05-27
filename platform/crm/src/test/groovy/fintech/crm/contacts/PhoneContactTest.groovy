package fintech.crm.contacts

import spock.lang.Specification

class PhoneContactTest extends Specification {

    def "GetPhoneNumber"() {
        expect:
        new PhoneContact(countryCode: "+371", localNumber: "27783912").getPhoneNumber() == "+37127783912"
        new PhoneContact(countryCode: null, localNumber: "27783912").getPhoneNumber() == "27783912"
    }
}
