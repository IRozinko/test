package fintech.spain.alfa.web.controllers.web

import fintech.TimeMachine
import fintech.spain.platform.web.SpecialLinkType
import fintech.spain.platform.web.model.command.BuildLinkCommand
import fintech.spain.platform.web.spi.SpecialLinkService
import fintech.spain.alfa.product.testing.TestFactory
import fintech.spain.alfa.product.web.WebAuthorities
import fintech.spain.alfa.product.web.WebJwtTokenService
import fintech.spain.alfa.web.AbstractAlfaApiTest
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject
import spock.lang.Unroll

class SpecialLinkApiTest extends AbstractAlfaApiTest {

    @Subject
    @Autowired
    SpecialLinkService specialLinkService

    @Autowired
    WebJwtTokenService webJwtTokenService

    @Unroll
    def "Activate #specialLinkType link"() {
        given:
        def client = TestFactory.newClient().registerDirectly()

        and:
        def link = specialLinkService.buildLink(new BuildLinkCommand(
            clientId: client.clientId,
            type: specialLinkType,
            reusable: true,
            autoLoginRequired: true
        ))

        when:
        def entity = restTemplate.postForEntity("/api/public/web/special-link/activate/{token}", null, fintech.spain.alfa.web.models.LoginResponse.class, link.token)

        then:
        entity.statusCode.is2xxSuccessful()
        StringUtils.isNoneBlank(entity.getBody().getToken())
        def jwt = webJwtTokenService.parse(entity.getBody().getToken())
        WebJwtTokenService.role(jwt) == role

        where:
        specialLinkType                   | role
        SpecialLinkType.ADD_PAYMENT       | WebAuthorities.WEB_PAYMENT_ONLY
        SpecialLinkType.LOC_SPECIAL_OFFER | WebAuthorities.WEB_FULL
    }

    def "Try to activate expired link"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def link = specialLinkService.buildLink(new BuildLinkCommand(
            clientId: client.clientId,
            type: SpecialLinkType.ADD_PAYMENT,
            reusable: true,
            autoLoginRequired: true,
            expiresAt: TimeMachine.now().plusHours(1)
        ))

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusHours(3))
        def entity = restTemplate.postForEntity("/api/public/web/special-link/activate/{token}", null, fintech.spain.alfa.web.models.LoginResponse.class, link.token)

        then:
        entity.statusCode.is4xxClientError()
    }

    def "Get data related to special link"() {
        given:
        def client = TestFactory.newClient().registerDirectly()
        def link = specialLinkService.buildLink(new BuildLinkCommand(
            clientId: client.clientId,
            type: SpecialLinkType.ADD_PAYMENT,
            reusable: true,
            autoLoginRequired: true
        ))

        when:
        def entity = restTemplate.getForEntity("/api/public/web/special-link/data/{token}", fintech.spain.alfa.web.models.SpecialLinkDataResponse.class, link.token)

        then:
        entity.statusCode.is2xxSuccessful()
        with(entity.getBody()) { it ->
            it.clientId == client.clientId
            it.type == SpecialLinkType.ADD_PAYMENT
            it.autoLogin
            it.reusable
            it.payload == Collections.EMPTY_MAP
        }
    }
}
