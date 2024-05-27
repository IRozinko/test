package fintech.viventor

import fintech.viventor.db.ViventorLogRepository
import fintech.viventor.model.ViventorBorrower
import fintech.viventor.model.ViventorConsumer
import fintech.viventor.model.ViventorCustomSchedule
import fintech.viventor.model.ViventorNewLoan
import fintech.viventor.model.ViventorSchedule
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.Subject

import static fintech.RandomUtils.randomId
import static fintech.TimeMachine.today

class ViventorServiceTest extends BaseSpecification {

    @Subject
    @Autowired
    ViventorService viventorService

    @Autowired
    ViventorLogRepository viventorLogRepository

    def "post loan"() {
        when:
        def loan1Id = randomId()
        def viventorLoan1Id = loan1Id + "-0"
        viventorService.postLoan(new PostLoanCommand(
            loanId: loan1Id,
            loan: new ViventorNewLoan(id: viventorLoan1Id, currency: "EUR"),
            borrower: new ViventorBorrower(new ViventorConsumer(gender: "MALE")),
            schedule: new ViventorSchedule(new ViventorCustomSchedule())
        ))

        then:
        def logEntities = viventorLogRepository.findAll()
        assert logEntities.size() == 1
        with(logEntities.first()) {
            assert loanId == loan1Id
            assert viventorLoanId == viventorLoan1Id
            assert requestType == ViventorRequestType.NEW_LOAN
            assert requestUrl == "mock"
            assert requestBody.size() > 0
            assert !responseBody
            assert responseStatusCode == 200
            assert status == ViventorResponseStatus.OK
        }
    }

    def "post loan payment"() {
        when:
        def loan1Id = randomId()
        def viventorLoan1Id = "v-1"
        viventorService.postLoanPayment(
            new PostLoanPaymentCommand(loanId: loan1Id, viventorLoanId: viventorLoan1Id, number: 0, actualDate: today()))

        then:
        def logEntities = viventorLogRepository.findAll()
        assert logEntities.size() == 1
        with(logEntities.first()) {
            assert loanId == loan1Id
            assert viventorLoanId == viventorLoanId
            assert requestType == ViventorRequestType.LOAN_PAYMENT
            assert requestUrl == "mock"
            assert requestBody.size() > 0
            assert !responseBody
            assert responseStatusCode == 200
            assert status == ViventorResponseStatus.OK
        }
    }

    def "post loan paid"() {
        when:
        def loan1Id = randomId()
        def viventorLoan1Id = "v-1"
        viventorService.postLoanPaid(new PostLoanPaidCommand(loan1Id, viventorLoan1Id, today()))

        then:
        def logEntities = viventorLogRepository.findAll()
        assert logEntities.size() == 1
        with(logEntities.first()) {
            assert loanId == loan1Id
            assert viventorLoanId == viventorLoanId
            assert requestType == ViventorRequestType.LOAN_PAID
            assert requestUrl == "mock"
            assert requestBody.size() > 0
            assert !responseBody
            assert responseStatusCode == 200
            assert status == ViventorResponseStatus.OK
        }
    }

    def "post loan extension"() {
        when:
        def loan1Id = randomId()
        def viventorLoan1Id = "v-1"
        viventorService.postLoanExtension(new PostLoanExtensionCommand(loan1Id, viventorLoan1Id, today()))

        then:
        def logEntities = viventorLogRepository.findAll()
        assert logEntities.size() == 1
        with(logEntities.first()) {
            assert loanId == loan1Id
            assert viventorLoanId == viventorLoanId
            assert requestType == ViventorRequestType.LOAN_EXTENSION
            assert requestUrl == "mock"
            assert requestBody.size() > 0
            assert !responseBody
            assert responseStatusCode == 200
            assert status == ViventorResponseStatus.OK
        }
    }

}
