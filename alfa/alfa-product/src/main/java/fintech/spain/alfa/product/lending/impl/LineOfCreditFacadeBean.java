package fintech.spain.alfa.product.lending.impl;

import fintech.retrofit.RetrofitHelper;
import fintech.Validate;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.UpdateClientCommand;
import fintech.crm.client.model.DormantsClientConverter;
import fintech.crm.client.model.DormantsClientData;
import fintech.crm.client.model.PrestoDormantsResponse;
import fintech.db.AuditInfoProvider;
import fintech.geoip.GeoIpService;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationSourceType;
import fintech.lending.core.application.LoanApplicationType;
import fintech.lending.core.application.commands.AttachWorkflowCommand;
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand;
import fintech.lending.core.application.impl.LoanApplicationNumberProvider;
import fintech.lending.core.application.model.DormantsApplicationConverter;
import fintech.lending.core.application.model.DormantsApplicationData;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.product.lending.LineOfCreditFacade;
import fintech.spain.platform.web.model.DormantsData;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.presto.api.LineOfCreditCrossApiClient;
import fintech.spain.alfa.product.presto.api.MockLineOfCreditCrossApiClient;
import fintech.spain.alfa.product.workflow.dormants.event.LocClientRedirectionCompleted;
import fintech.workflow.StartWorkflowCommand;
import fintech.workflow.WorkflowService;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Transactional
@Component
public class LineOfCreditFacadeBean implements LineOfCreditFacade {

    @Autowired
    private AuditInfoProvider auditInfoProvider;
    @Autowired
    private LoanApplicationNumberProvider loanApplicationNumberProvider;
    @Autowired
    private ClientService clientService;
    @Autowired
    private GeoIpService geoIpService;
    @Autowired
    private LoanApplicationService loanApplicationService;
    @Autowired
    private WorkflowService workflowService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Resource(name = "${loc.cross.api:" + MockLineOfCreditCrossApiClient.NAME + "}")
    private LineOfCreditCrossApiClient locApiClient;

    @Autowired
    private DormantsClientConverter dormantsClientConverter;
    @Autowired
    private DormantsApplicationConverter dormantsApplicationConverter;
    @Autowired
    private LoanService loanService;

    @Override
    public Long apply(Long clientId, BigDecimal amount, LocalDateTime date) {
        Long applicationId = submitApplication(clientId, amount, date);
        startApplicationWorkflow(applicationId);
        return applicationId;
    }

    @Override
    public PrestoDormantsResponse sendClientToPresto(Long clientId, Long applicationId) {
        Client client = clientService.get(clientId);
        LoanApplication loanApplication = loanApplicationService.get(applicationId);
        DormantsClientData dormantsClientData = dormantsClientConverter.convert(client);
        DormantsApplicationData dormantsApplicationData = dormantsApplicationConverter.convert(loanApplication);
        DormantsData dormantsData = new DormantsData(dormantsClientData, dormantsApplicationData);
        Optional<PrestoDormantsResponse> response = RetrofitHelper.syncCall(locApiClient.sendClient(dormantsData));
        Validate.isTrue(response.isPresent(), "Couldn't send client [%d] to Presto", clientId);
        return response.get();
    }

    @Override
    public void completeWorkflow(Long clientId) {
        eventPublisher.publishEvent(new LocClientRedirectionCompleted(clientId));
    }

    @Override
    public void markClientAsTransferred(Long clientId) {
        Client client = clientService.get(clientId);
        UpdateClientCommand command = UpdateClientCommand.fromClient(client);
        command.setTransferredToLoc(true);
        clientService.update(command);
    }

    public Long submitApplication(Long clientId, BigDecimal amount, LocalDateTime date) {
        Client client = clientService.get(clientId);
        String ipAddress = auditInfoProvider.getInfo().getIpAddress();
        String ipCountry = geoIpService.getCountryCode(ipAddress).orElse(AlfaConstants.UNKNOWN_IP_COUNTRY);

        String longApproveCode = RandomStringUtils.randomAlphanumeric(12);
        String shortApproveCode = StringUtils.upperCase(AlfaConstants.SMS_APPROVE_CODE);

        long loansPaid = loanService.findLoans(LoanQuery.paidLoans(clientId)).size();

        SubmitLoanApplicationCommand command = new SubmitLoanApplicationCommand();
        command.setType(LoanApplicationType.LINE_OF_CREDIT);
        command.setApplicationNumber(loanApplicationNumberProvider.newNumber(client.getNumber(), "-", 3));
        command.setPrincipal(amount);
        command.setPeriodCount(0L);
        command.setPeriodUnit(PeriodUnit.DAY);
        command.setSubmittedAt(date);
        command.setClientId(client.getId());
        command.setInvoiceDay(AlfaConstants.LOC_DEFAULT_INVOICE_DAY);
        command.setProductId(AlfaConstants.PRODUCT_ID);
        command.setIpAddress(ipAddress);
        command.setIpCountry(ipCountry);
        command.setLongApproveCode(longApproveCode);
        command.setShortApproveCode(shortApproveCode);
        command.setInterestDiscountPercent(BigDecimal.ZERO);
        command.setSourceType(LoanApplicationSourceType.ORGANIC);
        command.setLoansPaid(loansPaid);
        return loanApplicationService.submit(command);
    }

    private Long startApplicationWorkflow(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);

        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setClientId(application.getClientId());
        command.setApplicationId(application.getId());
        Long workflowId = workflowService.startWorkflow(command);

        AttachWorkflowCommand attachWorkflowCommand = new AttachWorkflowCommand();
        attachWorkflowCommand.setWorkflowId(workflowId);
        attachWorkflowCommand.setApplicationId(applicationId);
        loanApplicationService.attachWorkflow(attachWorkflowCommand);

        return workflowId;
    }


}
