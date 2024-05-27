package fintech.lending.core

import fintech.TimeMachine
import fintech.lending.BaseSpecification
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.application.LoanApplicationStatus
import fintech.lending.core.application.LoanApplicationStatusDetail
import fintech.lending.core.application.commands.ApproveLoanApplicationCommand
import fintech.lending.core.application.commands.AttachWorkflowCommand
import fintech.lending.core.application.commands.LoanApplicationOfferCommand
import fintech.lending.core.application.commands.SaveScoreCommand
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand
import fintech.lending.core.application.commands.UpdateInquiryCommand
import fintech.lending.core.application.commands.UpdateLoanApplicationInterestRateCommand
import fintech.lending.core.application.impl.LoanApplicationNumberProvider
import fintech.lending.core.creditlimit.AddCreditLimitCommand
import fintech.lending.core.creditlimit.CreditLimitService
import org.springframework.beans.factory.annotation.Autowired

import static fintech.DateUtils.date
import static fintech.DateUtils.dateTime
import static fintech.TimeMachine.today

class LoanApplicationServiceTest extends BaseSpecification {

    @Autowired
    LoanApplicationService loanApplicationService

    @Autowired
    LoanApplicationNumberProvider numberProvider


    @Autowired
    CreditLimitService creditLimitService

    Long applicationId

    def "Rejected"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.reject(applicationId, "no money")

        then:
        with(loanApplicationService.get(applicationId)) {
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.REJECTED
            closeReason == "no money"
        }
    }

    def "Ip address is present"() {
        given:
        applicationId = submit()

        when:
        def application = loanApplicationService.get(applicationId)

        then:
        application.ipAddress == "127.0.0.1"

    }

    def "Cancelled"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.cancel(applicationId, "too lazy")

        then:
        with(loanApplicationService.get(applicationId)) {
            status == LoanApplicationStatus.CLOSED
            statusDetail == LoanApplicationStatusDetail.CANCELLED
            closeReason == "too lazy"
        }
    }

    def "Update amount"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.updateOffer(new LoanApplicationOfferCommand(
            id: applicationId,
            principal: 99.00g,
            interest: 10.00g,
            nominalApr: 121.00,
            effectiveApr: 501.00,
            offerDate: date("2001-01-01"),
            periodUnit: PeriodUnit.MONTH,
            periodCount: 1
        ))

        then:
        with(loanApplicationService.get(applicationId)) {
            offeredPrincipal == 99.00g
            offeredInterest == 10.00g
            requestedPrincipal == 100.00g
            nominalApr == 121.00
            effectiveApr == 501.00
            offerDate == date("2001-01-01")
            offeredPeriodCount == 1
            offeredPeriodUnit == PeriodUnit.MONTH
        }
    }

    def "Update inquiry"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.updateInquiry(new UpdateInquiryCommand(
            applicationId: applicationId,
            requestedPrincipal: 99.00g,
            termInMonth: 12))

        then:
        with(loanApplicationService.get(applicationId)) {
            requestedPrincipal == 99.00g
        }
    }

    def "Can not update inquiry after offer is made"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.updateOffer(new LoanApplicationOfferCommand(
            id: applicationId,
            principal: 99.00g,
            interest: 10.00g,
            nominalApr: 121.00,
            effectiveApr: 501.00,
            offerDate: today(),
            periodUnit: PeriodUnit.MONTH,
            periodCount: 1
        ))

        and:
        loanApplicationService.updateInquiry(new UpdateInquiryCommand(
            applicationId: applicationId,
            requestedPrincipal: 99.00g,
            termInMonth: 12))

        then:
        thrown(IllegalArgumentException.class)
    }

    def "Attach workflow"() {
        given:
        applicationId = submit()

        expect:
        loanApplicationService.get(applicationId).workflowId == null

        when:
        loanApplicationService.attachWorkflow(new AttachWorkflowCommand(applicationId: applicationId, workflowId: 99L))

        then:
        loanApplicationService.get(applicationId).workflowId == 99L
    }

    def "Find application"() {
        given:
        applicationId = submit()
        def clientId = 1L

        when:
        def applications = loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN))

        then:
        assert applications.size() == 1

        when:
        loanApplicationService.cancel(applications[0].id, "test")
        loanApplicationService.submit(new SubmitLoanApplicationCommand(clientId: clientId, productId: 1L, submittedAt: TimeMachine.now(), principal: 100.00g, applicationNumber: "123", periodCount: 0, periodUnit: PeriodUnit.MONTH, loansPaid: 0))

        then:
        assert loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.CLOSED)).size() == 1
        assert loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN)).size() == 1
    }

    def "Find latest application"() {
        given:
        def id0 = submit()
        def clientId = 1L
        loanApplicationService.approve(new ApproveLoanApplicationCommand(id: id0, loanId: 1L, approveDate: TimeMachine.today()))

        when: "submit another application"
        def id1 = loanApplicationService.submit(new SubmitLoanApplicationCommand(clientId: 1L, productId: 1L, ipAddress: "127.0.0.1", submittedAt: TimeMachine.now(), principal: 100.00g, applicationNumber: "4321", periodCount: 0, periodUnit: PeriodUnit.MONTH, loansPaid: 0))

        then:
        loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId)).get().id == id1
    }

    def "BUG FIX: Created at and updated at fields should work"() {
        given:
        applicationId = submit()
        def app = loanApplicationService.get(applicationId)

        when:
        Thread.sleep(1000)
        loanApplicationService.cancel(applicationId, "too lazy")

        then:
        assert loanApplicationService.get(applicationId).createdAt == app.createdAt
        assert loanApplicationService.get(applicationId).updatedAt > app.updatedAt

    }

    def "Credit limit is saved in application"() {
        given:
        creditLimitService.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 101.00g, reason: "test", activeFrom: date("2001-01-01")))
        creditLimitService.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 102.00g, reason: "test", activeFrom: date("2001-01-02")))
        creditLimitService.addLimit(new AddCreditLimitCommand(clientId: 1, limit: 103.00g, reason: "test", activeFrom: date("2001-01-03")))

        when:
        applicationId = loanApplicationService.submit(new SubmitLoanApplicationCommand(clientId: 1L, productId: 1L, ipAddress: "127.0.0.1", submittedAt: dateTime("2001-01-02 11:00:00"), principal: 100.00g, applicationNumber: "1234", periodCount: 0, periodUnit: PeriodUnit.MONTH, loansPaid: 0))

        then:
        loanApplicationService.get(applicationId).creditLimit == 102.00g
    }

    def "find by uuid"() {
        given:
        applicationId = submit()

        expect:
        loanApplicationService.findByUuid(loanApplicationService.get(applicationId).uuid).isPresent()
        !loanApplicationService.findByUuid(loanApplicationService.get(applicationId).uuid + "1").isPresent()
    }

    def "score is saved"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.saveScore(new SaveScoreCommand(applicationId, 100.00, 'FINTECH_MARKET'))

        then:
        loanApplicationService.get(applicationId).score == 100.00
        loanApplicationService.get(applicationId).scoreSource == 'FINTECH_MARKET'
    }

    def "Interest rates are updated"() {
        given:
        applicationId = submit()

        when:
        loanApplicationService.updateInterestRate(new UpdateLoanApplicationInterestRateCommand(
            applicationId: applicationId,
            nominalInterestRate: 35.00,
            effectiveInterestRate: 42.00))

        then:
        with(loanApplicationService.get(applicationId)) {
            nominalApr == 35.00
            effectiveApr == 42.00
        }
    }

    private long submit() {
        loanApplicationService.submit(new SubmitLoanApplicationCommand(clientId: 1L, productId: 1L, ipAddress: "127.0.0.1", submittedAt: TimeMachine.now(), principal: 100.00g, applicationNumber: "1234", periodCount: 0, periodUnit: PeriodUnit.MONTH, loansPaid: 0))
    }
}
