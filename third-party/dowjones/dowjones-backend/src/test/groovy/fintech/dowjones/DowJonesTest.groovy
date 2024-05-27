package fintech.dowjones

import org.apache.http.HttpStatus

import java.time.LocalDate

class DowJonesTest extends BaseSpecification {

    def "Error"() {
        given:
        mockProvider.setThrowError(true)

        when:
        Map<String, String> params = new HashMap<>()
        params.put("name", "Smith")
        params.put("date-of-birth", LocalDate.now().minusYears(30).toString())
        def response = service.search(new DowJonesRequestData(clientId: 1L, parameters: params))

        then:
        with(service.getDowJonesRequest(response.id)) {
            status == DowJonesResponseStatus.FAILED
        }
    }

    def "Success"() {
        given:
        Map<String, String> params = new HashMap<>()
        params.put("name", "Smith")
        params.put("date-of-birth", LocalDate.now().minusYears(30).toString())
        service.search(new DowJonesRequestData(clientId: 1L, parameters: params))
        params.put("name", "Smith Jr.")
        service.search(new DowJonesRequestData(clientId: 1L, parameters: params))
        params.put("name", "James")
        params.put("date-of-birth", LocalDate.now().minusYears(50).toString())
        service.search(new DowJonesRequestData(clientId: 2L, parameters: params))

        when:
        params.put("name", "Smeeth")
        params.put("date-of-birth", LocalDate.now().minusYears(30).toString())
        def response = service.search(new DowJonesRequestData(clientId: 1L, parameters: params))

        then:
        with(service.getDowJonesRequest(response.id)) {
            status == DowJonesResponseStatus.OK
            clientId == 1L
            responseStatusCode == HttpStatus.SC_OK
            response.responseBody
            !error
        }
    }
}
