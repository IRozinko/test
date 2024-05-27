package fintech.spain.alfa.web.services

import fintech.crm.client.Client
import fintech.lending.core.application.LoanApplication
import fintech.lending.core.application.LoanApplicationQuery
import fintech.lending.core.application.LoanApplicationService
import fintech.lending.core.loan.Loan
import fintech.lending.core.loan.LoanQuery
import fintech.lending.core.loan.LoanService
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class ClientInfoServiceTest extends Specification {

    LoanApplicationService loanApplicationService
    LoanService loanService

    @Subject
    ClientInfoService clientInfoService

    def "setup"() {
        loanApplicationService = Mock(LoanApplicationService)
        loanService = Mock(LoanService)
        clientInfoService = new ClientInfoService( null, loanApplicationService,
                null, null, loanService, null, null)
    }

    @Unroll
    def "IsQualifiedForNewLoan - transferredToLoc: #transferedToLoc, application present: #application.isPresent(), loans: #loans.size()"() {
        given:
        loanApplicationService.findLatest(_ as LoanApplicationQuery) >> application
        loanService.findLoans(_ as LoanQuery) >> loans

        expect:
        clientInfoService.isQualifiedForNewLoan(new Client(transferredToLoc: transferedToLoc)) == qualifiedForNewLoan

        where:
        transferedToLoc | application                        | loans                     | qualifiedForNewLoan
        false           | Optional.empty()                   | Collections.emptyList()   | true
        true            | Optional.empty()                   | Collections.emptyList()   | false
        false           | Optional.of(new LoanApplication()) | Collections.emptyList()   | false
        false           | Optional.empty()                   | Arrays.asList(new Loan()) | false
    }
}
