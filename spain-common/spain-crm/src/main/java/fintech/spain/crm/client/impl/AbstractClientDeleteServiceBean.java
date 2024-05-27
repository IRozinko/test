package fintech.spain.crm.client.impl;

import com.google.common.collect.Lists;
import fintech.Validate;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.logins.EmailLoginService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.spain.crm.client.ClientDeleteService;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Slf4j
@Transactional
public abstract class AbstractClientDeleteServiceBean implements ClientDeleteService {

    protected final ClientRepository clientRepository;
    protected final WorkflowService workflowService;
    protected final EmailLoginService emailLoginService;
    protected final LoanService loanService;
    protected final EntityManager entityManager;

    protected AbstractClientDeleteServiceBean(ClientRepository clientRepository, WorkflowService workflowService,
                                              EmailLoginService emailLoginService, LoanService loanService,
                                              EntityManager entityManager) {
        this.clientRepository = clientRepository;
        this.workflowService = workflowService;
        this.emailLoginService = emailLoginService;
        this.loanService = loanService;
        this.entityManager = entityManager;
    }

    @Override
    public void softDelete(Long clientId) {
        ClientEntity client = clientRepository.getRequired(clientId);
        log.warn("Soft deleting client [{}]", client);

        List<Loan> openLoans = loanService.findLoans(LoanQuery.openLoans(clientId));
        Validate.isTrue(openLoans.isEmpty(), "Cannot soft delete client with open loans");

        client.setAcceptMarketing(false);
        client.setBlockCommunication(true);
        client.setDeleted(true);

        emailLoginService.delete(clientId);

        workflowService.findWorkflows(WorkflowQuery.byClientId(clientId, WorkflowStatus.ACTIVE))
            .forEach(workflow -> workflowService.terminateWorkflow(workflow.getId(), "Client soft delete"));

    }

    protected void executeSqlScript(Long clientId) {
        List<String> statements = Lists.newArrayList();
        ScriptUtils.splitSqlScript(hardDeleteScript(), ';', statements);
        executeStatements(statements, clientId);
    }

    private void executeStatements(List<String> statements, Long clientId) {
        for (String statement : statements) {
            log.info("Executing [{}] for client with id [{}]", statement, clientId);
            entityManager
                .createNativeQuery(statement)
                .setParameter("clientId", clientId)
                .executeUpdate();
            entityManager.flush();
        }
    }

    public void partialDeleteScript(Long clientId, String script) {
        List<String> statements = Lists.newArrayList();
        ScriptUtils.splitSqlScript(script, ';', statements);
        executeStatements(statements, clientId);
    }

    protected abstract String hardDeleteScript();
}
