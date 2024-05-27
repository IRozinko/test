package fintech.spain.alfa.web.services;

import com.google.common.annotations.VisibleForTesting;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.web.models.ClientInfoResponse;
import fintech.spain.alfa.web.models.convertor.ApplicationInfoConverter;
import fintech.spain.alfa.web.models.convertor.ClientInfoConverter;
import fintech.spain.alfa.web.services.navigation.spi.NavigationContext;
import fintech.spain.alfa.product.web.spi.PopupService;
import fintech.workflow.WorkflowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
public class ClientInfoService {

    private final LoanApplicationService loanApplicationService;
    private final ClientService clientService;
    private final EmailLoginService emailLoginService;
    private final LoanService loanService;
    private final PopupService popupService;
    private final ClientInfoConverter clientInfoConverter;
    private final ApplicationInfoConverter applicationInfoConverter;
    private final NavigationContext navigationContext;

    @Autowired
    public ClientInfoService(WorkflowService workflowService,
                             LoanApplicationService loanApplicationService, ClientService clientService,
                             EmailLoginService emailLoginService, LoanService loanService, PopupService popupService,
                             NavigationContext navigationContext) {
        this.loanApplicationService = loanApplicationService;
        this.clientService = clientService;
        this.emailLoginService = emailLoginService;
        this.loanService = loanService;
        this.popupService = popupService;
        this.navigationContext = navigationContext;
        this.clientInfoConverter = new ClientInfoConverter();
        this.applicationInfoConverter = new ApplicationInfoConverter(workflowService);
    }

    @Transactional
    public ClientInfoResponse get(Long clientId) {
        Client client = clientService.get(clientId);

        ClientInfoResponse info = clientInfoConverter.convert(client)
            .setAuthenticated(true)
            .setState(getState())
            .setData(getStateData())
            .setTemporaryPassword(emailLoginService.findByClientId(clientId).map(EmailLogin::isTemporaryPassword).orElse(false))
            .setApplication(findLoanApplication(clientId).map(applicationInfoConverter::convert).orElse(null))
            .setPopups(popupService.getActual(clientId))
            .setQualifiedForNewLoan(isQualifiedForNewLoan(client))
            .setTransferredToLoc(client.isTransferredToLoc());
        log.debug("Resolved client state for client id [{}]: [{}]", clientId, info);
        return info;
    }

    @VisibleForTesting
    protected boolean isQualifiedForNewLoan(Client client) {
        if (client.isTransferredToLoc())
            return false;

        Optional<LoanApplication> openApplication = loanApplicationService.findLatest(LoanApplicationQuery.byClientId(client.getId(), LoanApplicationStatus.OPEN));
        if (openApplication.isPresent())
            return false;

        List<Loan> openLoans = loanService.findLoans(LoanQuery.openLoans(client.getId()));
        return openLoans.isEmpty();
    }

    private Optional<LoanApplication> findLoanApplication(Long clientId) {
        return loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId));
    }

    private String getState() {
        return navigationContext.getProvider().getState();
    }

    private Map<String, Object> getStateData() {
        return navigationContext.getProvider().getStateData();
    }

}
