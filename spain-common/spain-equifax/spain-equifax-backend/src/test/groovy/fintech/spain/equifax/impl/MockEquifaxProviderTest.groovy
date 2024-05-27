package fintech.spain.equifax.impl

import fintech.spain.equifax.mock.MockEquifaxProvider
import fintech.spain.equifax.model.EquifaxRequest
import fintech.spain.equifax.model.EquifaxStatus
import spock.lang.Specification

class MockEquifaxProviderTest extends Specification {

    def "Get response"() {
        when:
        def response = new MockEquifaxProvider().request(new EquifaxRequest())

        then:
        response.status == EquifaxStatus.NOT_FOUND
    }
}
