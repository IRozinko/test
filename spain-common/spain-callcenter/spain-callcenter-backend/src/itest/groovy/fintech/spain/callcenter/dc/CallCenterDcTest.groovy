package fintech.spain.callcenter.dc

import fintech.ClasspathUtils
import fintech.JsonUtils
import fintech.TimeMachine
import fintech.crm.client.ClientService
import fintech.crm.client.CreateClientCommand
import fintech.crm.client.UpdateClientCommand
import fintech.crm.contacts.AddPhoneCommand
import fintech.crm.contacts.PhoneContactService
import fintech.crm.contacts.PhoneType
import fintech.dc.DcService
import fintech.dc.DcSettingsService
import fintech.dc.commands.PostLoanCommand
import fintech.dc.model.DcSettings
import fintech.dc.spi.DcDefaults
import fintech.spain.callcenter.BaseSpecification
import fintech.spain.callcenter.CallCenterDataService
import fintech.spain.callcenter.CallQuery
import fintech.spain.callcenter.CallStatus
import org.apache.commons.lang3.RandomStringUtils
import org.springframework.beans.factory.annotation.Autowired

class CallCenterDcTest extends BaseSpecification {

    @Autowired
    DcService dcService

    @Autowired
    DcSettingsService dcSettingsService

    @Autowired
    DcDefaults dcDefaults

    @Autowired
    ClientService clientService

    @Autowired
    CallCenterDcSetup callCenterDcSetup

    @Autowired
    CallCenterDataService callCenterDataService

    @Autowired
    PhoneContactService phoneContactService

    def setup() {
        dcDefaults.init()
        callCenterDcSetup.init()

        DcSettings settings = JsonUtils.readValue(ClasspathUtils.resourceToString("test-dc-settings.json"), DcSettings.class)
        dcSettingsService.saveSettings(settings, true)
    }

    def "Add call to list when debt is moved to collections portfolio"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        def id = dcService.postLoan(postCommand(clientId))

        when:
        dcService.postLoan(postCommand(clientId).setDpd(10))
        def debt = dcService.get(id)
        def calls = callCenterDataService.find(new CallQuery().setClientId(debt.clientId))

        then:
        debt.portfolio == "Collections"
        calls
        calls.size() == 1
        calls[0].status == CallStatus.PENDING
        calls[0].clientId == debt.clientId
    }

    def "Remove call from list when debt is moved to paid portfolio"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        def id = dcService.postLoan(postCommand(clientId).setDpd(10))

        when:
        dcService.postLoan(postCommand(clientId).setTotalDue(0.0))
        def debt = dcService.get(id)
        def calls = callCenterDataService.find(new CallQuery().setClientId(debt.clientId))

        then:
        debt.portfolio == "Paid"
        calls
        calls.size() == 1
        calls[0].status == CallStatus.REMOVED
        calls[0].clientId == debt.clientId
    }

    def "Remove call that is not in call list"() {
        given:
        def clientId = clientService.create(new CreateClientCommand(RandomStringUtils.randomAlphanumeric(10)))
        clientService.update(new UpdateClientCommand(clientId: clientId, firstName: "Name", lastName: "Surname"))
        phoneContactService.addPhoneContact(new AddPhoneCommand(clientId: clientId, countryCode: "34", localNumber: "666123456", type: PhoneType.MOBILE))
        def id = dcService.postLoan(postCommand(clientId))

        when:
        dcService.postLoan(postCommand(clientId).setTotalDue(0.0))
        def debt = dcService.get(id)
        def calls = callCenterDataService.find(new CallQuery().setClientId(debt.clientId))

        then:
        noExceptionThrown()
        debt.portfolio == "Paid"
        !calls
    }

    PostLoanCommand postCommand(clientId) {
        new PostLoanCommand(
            loanId: 1L,
            loanNumber: "1001",
            clientId: clientId,
            dpd: -2,
            maxDpd: 10,
            totalDue: 100.00g,
            interestDue: 20.00g,
            principalDue: 75.00g,
            penaltyDue: 10.00g,
            feeDue: 5.00g,
            totalOutstanding: 500.00g,
            interestOutstanding: 100.00g,
            principalOutstanding: 300.00g,
            penaltyOutstanding: 70.00g,
            feeOutstanding: 30.00g,
            totalPaid: 50.00g,
            interestPaid: 30.00g,
            principalPaid: 15.00g,
            penaltyPaid: 5.00g,
            feePaid: 0.00g,
            triggerActionsImmediately: true,
            maturityDate: TimeMachine.today().plusDays(365),
            paymentDueDate: TimeMachine.today().plusDays(2),
            loanStatus: "OPEN",
            loanStatusDetail: "ACTIVE"
        )
    }
}
