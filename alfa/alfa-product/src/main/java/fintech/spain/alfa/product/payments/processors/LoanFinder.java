package fintech.spain.alfa.product.payments.processors;

import fintech.crm.CrmConstants;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.documents.IdentityDocument;
import fintech.crm.documents.IdentityDocumentService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.payments.StatementService;
import fintech.payments.model.Payment;
import fintech.payments.model.StatementRow;
import fintech.spain.alfa.product.AlfaConstants;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class LoanFinder {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private StatementService statementService;

    @Autowired
    private IdentityDocumentService identityDocumentService;

    Optional<Loan> findLoan(Payment payment) {
        // try to find by loan number
        Optional<Loan> maybeLoan = NumberMatcher.extractClientNumber(payment.getDetails())
            .flatMap(loanNumber -> loanService.findLoanByNumber(loanNumber));
        if (maybeLoan.isPresent()) {
            return maybeLoan;
        }

        Optional<Long> clientId = findClientId(payment);
        if (clientId.isPresent()) {
            return loanService.findLastLoan(LoanQuery.openLoans(clientId.get()));
        }
        return Optional.empty();
    }

    Optional<Long> findClientId(Payment payment) {
        // try to find by client number
        Optional<String> clientNumber = NumberMatcher.extractClientNumber(payment.getDetails());
        Optional<Long> clientId = clientNumber.flatMap(number -> clientService.findByClientNumber(number)).map(Client::getId);
        if (clientId.isPresent()) {
            return clientId;
        }

        // try to find by client DNI/NIE
        Optional<String> dni = NumberMatcher.extractClientDni(payment.getDetails());
        clientId = dni.flatMap(this::findClientByDni);
        if (clientId.isPresent()) {
            return clientId;
        }

        // try to find client by statement row document number attribute
        Optional<StatementRow> rowMaybe = statementService.findStatementRowByPayment(payment.getId());
        if (rowMaybe.isPresent()) {
            StatementRow row = rowMaybe.get();
            String documentNumber = row.getAttributes().get(AlfaConstants.STATEMENT_ROW_ATTRIBUTE_DOCUMENT_NUMBER);
            if (!StringUtils.isBlank(documentNumber)) {
                clientId = findClientByDni(documentNumber.toUpperCase());
            }
        }
        return clientId;
    }

    private Optional<Long> findClientByDni(String dni) {
        return identityDocumentService.findByNumber(dni, CrmConstants.IDENTITY_DOCUMENT_DNI, true).map(IdentityDocument::getClientId);
    }
}
