package fintech.spain.alfa.product.crosscheck;

import com.google.common.collect.ImmutableMap;
import fintech.Validate;
import fintech.crm.CrmConstants;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.documents.IdentityDocument;
import fintech.crm.documents.IdentityDocumentService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.model.CheckListQuery;
import fintech.workflow.Activity;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Transactional
@Slf4j
@Component
public class CrosscheckFacade {

    private static final Map<String, Function<CrosscheckRequest, String>> CHECKLIST_TYPE_VALUE_SELECTOR = ImmutableMap.of(
        CheckListConstants.CHECKLIST_TYPE_DNI, (r) -> StringUtils.trim(StringUtils.upperCase(r.getDni())),
        CheckListConstants.CHECKLIST_TYPE_EMAIL, CrosscheckRequest::getEmail,
        CheckListConstants.CHECKLIST_TYPE_PHONE, CrosscheckRequest::getPhone
    );

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private CheckListService checkListService;

    public CrosscheckResult crosscheck(CrosscheckRequest request) {
        Validate.notBlank(request.getDni(), "Blank dni");
        Validate.notBlank(request.getEmail(), "Blank email");
        Validate.notBlank(request.getPhone(), "Blank phone");
        String dni = StringUtils.trim(StringUtils.upperCase(request.getDni()));
        boolean blacklisted = !isAllowed(request);

        Optional<IdentityDocument> document = identityDocumentService.findByNumber(dni, CrmConstants.IDENTITY_DOCUMENT_DNI, true);
        if (!document.isPresent()) {
            log.info("Client not found by DNI [{}]", dni);
            return CrosscheckResult.notFound().setBlacklisted(blacklisted);
        }
        Long clientId = document.get().getClientId();
        Client client = clientService.get(clientId);

        int openLoans = loanService.findLoans(LoanQuery.openLoans(clientId)).size();
        Integer maxDpd = loanService.findLoans(LoanQuery.nonVoidedLoans(clientId)).stream()
            .map(Loan::getMaxOverdueDays)
            .max(Integer::compareTo).orElse(0);
        boolean repeatedClient = loanService.findLoans(LoanQuery.paidLoans(clientId)).size() > 0;

        Optional<Workflow> maybeWorkflow = workflowService.findWorkflows(WorkflowQuery.byClientId(clientId, WorkflowStatus.ACTIVE)).stream()
            .max(Comparator.comparing(Workflow::getId));

        CrosscheckResult result = new CrosscheckResult()
            .setFound(true)
            .setClientNumber(client.getNumber())
            .setBlacklisted(blacklisted)
            .setMaxDpd(maxDpd)
            .setOpenLoans(openLoans)
            .setRepeatedClient(repeatedClient)
            .setActiveRequest(maybeWorkflow.isPresent())
            .setActiveRequestStatus(maybeWorkflow.flatMap(workflow -> workflow.getActivities().stream()
                .filter(Activity::isActive)
                .max(Comparator.comparing(Activity::getId)))
                .map(Activity::getName).orElse(null));
        log.info("Crosscheck result by DNI [{}]: [{}]", dni, result);
        return result;
    }

    private boolean isAllowed(CrosscheckRequest request) {
        return CHECKLIST_TYPE_VALUE_SELECTOR.entrySet()
            .stream()
            .map(e -> new CheckListQuery(e.getKey(), e.getValue().apply(request)))
            .map(checkListService::isAllowed)
            .reduce((a, b) -> a && b).orElse(false);
    }
}
