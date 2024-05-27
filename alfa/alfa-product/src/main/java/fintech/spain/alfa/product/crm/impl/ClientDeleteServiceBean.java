package fintech.spain.alfa.product.crm.impl;

import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.logins.EmailLoginService;
import fintech.lending.core.loan.LoanService;
import fintech.spain.crm.client.ClientDeleteService;
import fintech.spain.crm.client.impl.AbstractClientDeleteServiceBean;
import fintech.workflow.WorkflowService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.InputStream;
import java.nio.charset.Charset;

@Slf4j
@Transactional
@Component
class ClientDeleteServiceBean extends AbstractClientDeleteServiceBean implements ClientDeleteService {

    @Autowired
    public ClientDeleteServiceBean(ClientRepository clientRepository, WorkflowService workflowService,
                                   EmailLoginService emailLoginService, EntityManager entityManager,
                                   LoanService loanService) {
        super(clientRepository, workflowService, emailLoginService, loanService, entityManager);
    }

    @Override
    public void hardDelete(Long clientId) {
        ClientEntity client = clientRepository.getRequired(clientId);
        log.warn("Hard deleting client [{}]", client);

        executeSqlScript(client.getId());
    }

    @Override
    public void partialDelete(Long clientId) {

    }

    @SneakyThrows
    protected String hardDeleteScript() {
        try (InputStream deletionScriptIs =
                 this.getClass().getResourceAsStream("/product/hard_delete_client.sql")) {
            return IOUtils.toString(deletionScriptIs, Charset.defaultCharset());
        }
    }

}
