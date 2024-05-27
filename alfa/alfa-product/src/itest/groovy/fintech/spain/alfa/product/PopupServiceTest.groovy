package fintech.spain.alfa.product


import fintech.TimeMachine
import org.springframework.beans.factory.annotation.Autowired

class PopupServiceTest extends AbstractAlfaTest {

    @Autowired
    fintech.spain.alfa.product.testing.acceptance.DcTestCases dcTestCases

    @Autowired
    fintech.spain.alfa.product.web.spi.PopupService popupService

    @Autowired
    fintech.spain.alfa.product.web.db.PopupRepository popupRepository

    def "Straight scenario: show -> resolve"() {
        when:
        def clientId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().clientId
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)

        then:
        def actual = popupService.getActual(clientId)
        assert !actual.isEmpty()

        def popupInfo = actual.get(0)
        assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE

        when:
        popupService.resolve(popupInfo.id, fintech.spain.alfa.product.web.model.PopupResolution.DISMISSED)

        then:
        assert popupService.getActual(clientId).isEmpty()

        and:
        def popupEntity = popupRepository.getRequired(popupInfo.id)
        assert popupEntity.resolution == fintech.spain.alfa.product.web.model.PopupResolution.DISMISSED
        assert popupEntity.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupEntity.resolvedAt
    }

    def "Client should not be spammed"() {
        when:
        def clientId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().clientId
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)

        then:
        def actual = popupService.getActual(clientId)
        assert actual.size() == 1
        def popupInfo = actual.get(0)
        assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE
    }

    def "Changing resolution of resolved popup is forbidden"() {
        when:
        def clientId = fintech.spain.alfa.product.testing.TestFactory.newClient().registerDirectly().clientId
        popupService.show(clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION)

        then:
        def actual = popupService.getActual(clientId)
        assert !actual.isEmpty()

        when:
        def popupInfo = actual.get(0)
        assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
        assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE

        and:
        popupService.resolve(popupInfo.id, fintech.spain.alfa.product.web.model.PopupResolution.ACCEPTED)

        and:
        popupService.resolve(popupInfo.id, fintech.spain.alfa.product.web.model.PopupResolution.DISMISSED)

        then:
        thrown(IllegalStateException)
    }

    def "DPD Popup should be EXHAUSTED after loan is Paid"() {
        when:
        def loan = dcTestCases.loanInDpd(5)
        def client = loan.toClient()

        and:
        loan.postToDc()
        loan.triggerDcActions()

        then:
        with(popupService.getActual(client.clientId)) { actual ->
            assert !actual.isEmpty()

            with(actual.get(0)) { popupInfo ->
                assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
                assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE
            }
        }

        when:
        loan.repayAll(TimeMachine.today())
        loan.triggerDcActions()

        then:
        with(loan.getDebt()) { debt ->
            debt.getPortfolio() == "Paid"
            debt.getStatus() == "NoStatus"
        }
        assert popupService.getActual(client.clientId).isEmpty()

        and:
        with(popupRepository.findByClientIdAndTypeAndResolution(client.clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION, fintech.spain.alfa.product.web.model.PopupResolution.EXHAUSTED)) { actual ->
            assert actual.size() == 1
        }
    }

    def "DPD Popup should be EXHAUSTED after RescheduleOffered"() {
        when:
        def loan = dcTestCases.loanInDpd(20)
        def client = loan.toClient()

        and:
        loan.postToDc()
        loan.triggerDcActions()

        then:
        with(popupService.getActual(client.clientId)) { actual ->
            assert !actual.isEmpty()

            with(actual.get(0)) { popupInfo ->
                assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION
                assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE
            }
        }

        when:
        dcTestCases.rescheduleOffered(loan, TimeMachine.today())

        then: "reschedule offered"
        with(loan.getDebt()) { debt ->
            debt.getPortfolio() == "Rescheduled"
            debt.getStatus() == "RescheduleOffered"
        }

        and: "popup is exhausted"
        with(popupRepository.findByClientIdAndTypeAndResolution(client.clientId, fintech.spain.alfa.product.web.model.PopupType.DPD_NOTIFICATION, fintech.spain.alfa.product.web.model.PopupResolution.EXHAUSTED)) { actual ->
            assert actual.size() == 1
        }
    }

    def "Reschedule offer Popup check"() {
        when:
        def loan = dcTestCases.loanInDpd(20)
        def client = loan.toClient()

        and:
        loan.postToDc()
        loan.triggerDcActions()

        and:
        dcTestCases.rescheduleOffered(loan, TimeMachine.today())

        then: "reschedule offered"
        with(loan.getDebt()) { debt ->
            debt.getPortfolio() == "Rescheduled"
            debt.getStatus() == "RescheduleOffered"
        }

        and:
        with(popupService.getActual(client.clientId)) { actual ->
            assert !actual.isEmpty()

            with(actual.get(0)) { popupInfo ->
                assert popupInfo.type == fintech.spain.alfa.product.web.model.PopupType.RESCHEDULE_OFFERED
                assert popupInfo.resolution == fintech.spain.alfa.product.web.model.PopupResolution.NONE
            }
        }

        when: "repay"
        loan.repay(loan.getLoan().getTotalDue(), TimeMachine.today().plusDays(2))
        loan.resolveDerivedValues()
        loan.triggerDcActions()

        then: "exhaust popup"
        with(popupService.getActual(client.clientId)) { actual ->
            assert actual.isEmpty()
        }

    }

}
