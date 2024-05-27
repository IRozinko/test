package fintech.instantor

import fintech.JsonUtils
import fintech.instantor.db.InstantorResponseRepository
import fintech.instantor.events.InstantorResponseProcessed
import fintech.instantor.json.common.InstantorCommonResponse
import fintech.instantor.model.InstantorResponseStatus
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import spock.lang.Ignore

import static fintech.instantor.api.InstantorCallbackApi.API_PATH
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_ACTION
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_ENCRYPTION
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_HASH
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_MESSAGE_ID
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_PAYLOAD
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_SOURCE
import static fintech.instantor.api.InstantorCallbackApi.PARAMETER_TIMESTAMP

class InstantorApiTest extends BaseApiTest {

    @Autowired
    InstantorResponseRepository repository

    def "Response fails"() {
        expect:
        repository.count() == 0

        when:
        def request = formPostRequest([
            (PARAMETER_ACTION)    : "test",
            (PARAMETER_ENCRYPTION): "test",
            (PARAMETER_HASH)      : "test",
            (PARAMETER_MESSAGE_ID): "test",
            (PARAMETER_PAYLOAD)   : "test",
            (PARAMETER_SOURCE)    : "test",
            (PARAMETER_TIMESTAMP) : "test",
        ])
        def response = restTemplate.postForEntity(API_PATH, request, String.class)

        then:
        response.statusCodeValue == 500

        and:
        repository.count() == 1

        and:
        repository.findAll()[0].status == InstantorResponseStatus.FAILED

        and:
        !eventConsumer.containsEvent(InstantorResponseProcessed.class)
    }


    def "Response: invalid login"() {
        expect:
        repository.count() == 0

        when:
        def request = formPostRequest([
            (PARAMETER_ACTION)    : "report/user/data",
            (PARAMETER_ENCRYPTION): "B64/MD5/AES/CBC/PKCS5",
            (PARAMETER_HASH)      : "b74353c7026ea4e440c2a638acc459570ea329a8",
            (PARAMETER_MESSAGE_ID): "msg-15e29e789a7-1503941593511",
            (PARAMETER_PAYLOAD)   : new ClassPathResource("instantor-payload-invalid-login").inputStream.text,
            (PARAMETER_SOURCE)    : "lineofcredit.es",
            (PARAMETER_TIMESTAMP) : "2017-08-24T18:07:51.210+02:00",
        ])
        def response = restTemplate.postForEntity(API_PATH, request, String.class)

        then:
        response.statusCodeValue == 200
        response.body == "OK: msg-15e29e789a7-1503941593511"

        and:
        repository.count() == 1
        def entity = repository.findAll()[0]

        and:
        with(entity) {
            status == InstantorResponseStatus.FAILED
            error == "Process status: "
            JsonUtils.readValue(payloadJson, InstantorCommonResponse.class).scrape.scrapeReport.status == "invalid_login"
        }

        and:
        !eventConsumer.containsEvent(InstantorResponseProcessed.class)
    }

    @Ignore
    def "Response: success"() {
        expect:
        repository.count() == 0

        when:
        def request = formPostRequest([
            (PARAMETER_ACTION)    : "report/user/data",
            (PARAMETER_ENCRYPTION): "B64/MD5/AES/CBC/PKCS5",
            (PARAMETER_HASH)      : "b74353c7026ea4e440c2a638acc459570ea329a8",
            (PARAMETER_MESSAGE_ID): "msg-15e29e789a7-1503941593511",
            (PARAMETER_PAYLOAD)   : new ClassPathResource("instantor-payload-success").inputStream.text,
            (PARAMETER_SOURCE)    : "lineofcredit.es",
            (PARAMETER_TIMESTAMP) : "2017-08-24T18:07:51.210+02:00",
        ])
        def response = restTemplate.postForEntity(API_PATH, request, String.class)

        then:
        response.statusCodeValue == 200
        response.body == "OK: msg-15e29e789a7-1503941593511"

        and:
        repository.count() == 1
        def entity = repository.findAll()[0]

        and:
        with(entity) {
            status == InstantorResponseStatus.FAILED
            clientId != null
            JsonUtils.readValue(payloadJson, InstantorCommonResponse.class).scrape.scrapeReport.status == "ok"
        }

        and:
        eventConsumer.containsEvent(InstantorResponseProcessed.class)
    }
}
