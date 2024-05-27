package fintech.spain.equifax.json.client

import fintech.spain.equifax.model.EquifaxRequest
import spock.lang.Specification

class EquifaxJsonRequestTest extends Specification {

    def "create"() {
        given:
        def req = new EquifaxRequest(clientId: 1, applicationId: 2, documentNumber: '123')

        when:
        def res = new EquifaxJsonRequest('1', 'ID', 'F', req)

        then:
        res.applicants.primaryConsumer.personalInformation.idCountryCode == '1'
        res.applicants.primaryConsumer.personalInformation.idType == 'ID'
        res.applicants.primaryConsumer.personalInformation.idCode == '123'
    }

}
