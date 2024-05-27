package fintech.spain.platform.web

import fintech.TimeMachine
import fintech.spain.platform.web.model.SpecialLinkActivated
import fintech.spain.platform.web.model.SpecialLinkDeactivated
import fintech.spain.platform.web.model.command.BuildLinkCommand
import fintech.spain.platform.web.spi.SpecialLinkService
import fintech.testing.integration.AbstractBaseSpecification
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Unroll

import static fintech.spain.platform.web.SpecialLinkType.SET_PASSWORD
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId

class SpecialLinkServiceTest extends AbstractBaseSpecification {

    @Autowired
    SpecialLinkService specialLinkService

    @Unroll
    def "Build link test reusable = #_reusable and autoLoginRequired = #_autoLoginRequired"() {
        given:
        long clientId = 1001L
        def expiration = TimeMachine.now().plusHours(1)

        when:
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(_reusable)
            .setAutoLoginRequired(_autoLoginRequired)
            .setExpiresAt(expiration)

        specialLinkService.buildLink(command)

        then:
        def link = specialLinkService.findRequiredLink(byClientId(clientId, SET_PASSWORD))
        with(link) {
            clientId == clientId
            type == SET_PASSWORD
            reusable == _reusable
            autoLoginRequired == _autoLoginRequired
            expiresAt == expiration
        }

        where:
        _reusable | _autoLoginRequired
        true      | true
        true      | false
        false     | true
        false     | false
    }

    def "reusable link test"() {
        given:
        long clientId = 1001L

        when:
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(true)
            .setAutoLoginRequired(true)

        def link = specialLinkService.buildLink(command)

        2.times {
            specialLinkService.activateLink(link.token)
        }

        then:
        noExceptionThrown()

        and:
        eventConsumer.countOf(SpecialLinkActivated) == 2
    }

    def "unreusable link test"() {
        given:
        long clientId = 1001L

        when:
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(false)
            .setAutoLoginRequired(true)

        def link = specialLinkService.buildLink(command)

        2.times {
            specialLinkService.activateLink(link.token)
        }

        then:
        thrown(IllegalArgumentException)

        and:
        eventConsumer.countOf(SpecialLinkActivated) == 1
        eventConsumer.countOf(SpecialLinkDeactivated) == 1
    }

    def "deactivation link test"() {
        given:
        long clientId = 1001L

        when:
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(false)
            .setAutoLoginRequired(true)

        def link = specialLinkService.buildLink(command)

        and:
        specialLinkService.deactivateLink(clientId, SET_PASSWORD)

        then:
        eventConsumer.countOf(SpecialLinkDeactivated) == 1

        and:
        specialLinkService.isExpired(link.token)
    }

    def "Not possible to re-activate reusable expired link"() {
        given:
        long clientId = 1001L

        when:
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(true)
            .setExpiresAt(TimeMachine.now().plusHours(2))
            .setAutoLoginRequired(true)

        def link = specialLinkService.buildLink(command)

        and:
        specialLinkService.activateLink(link.token)

        then:
        noExceptionThrown()

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusHours(3))
        specialLinkService.activateLink(link.token)

        then:
        thrown IllegalArgumentException

        and:
        specialLinkService.isExpired(link.token)
    }

    def "Not possible to activate expired link"() {
        given:
        long clientId = 1001L
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(true)
            .setExpiresAt(TimeMachine.now().plusHours(1))
            .setAutoLoginRequired(true)

        def link = specialLinkService.buildLink(command)

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusHours(3))
        specialLinkService.activateLink(link.token)

        then:
        thrown IllegalArgumentException
    }

    def "Find only valid links"() {
        given:
        long clientId = 1001L
        def command = new BuildLinkCommand()
            .setClientId(clientId)
            .setType(SET_PASSWORD)
            .setReusable(true)
            .setExpiresAt(TimeMachine.now().plusHours(1))
            .setAutoLoginRequired(true)

        specialLinkService.buildLink(command)

        when:
        def link = specialLinkService.findLink(byClientId(clientId, SET_PASSWORD))

        then:
        link.isPresent()
        link.get().clientId == clientId
        link.get().type == SET_PASSWORD

        when:
        TimeMachine.useFixedClockAt(TimeMachine.now().plusHours(1))
        link = specialLinkService.findLink(byClientId(clientId, SET_PASSWORD))

        then:
        !link.isPresent()
    }
}
