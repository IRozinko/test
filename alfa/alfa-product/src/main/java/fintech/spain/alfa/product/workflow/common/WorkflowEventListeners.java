package fintech.spain.alfa.product.workflow.common;


import fintech.crm.attachments.event.AttachmentSavedEvent;
import fintech.crm.bankaccount.events.ClientPrimaryBankAccountSetEvent;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.contacts.PhoneVerifiedEvent;
import fintech.instantor.events.InstantorResponseFailed;
import fintech.instantor.events.InstantorResponseProcessed;
import fintech.iovation.model.IovationBlackBoxCreatedEvent;
import fintech.lending.core.loan.events.LoanDisbursedEvent;
import fintech.lending.core.loan.events.LoanVoidedEvent;
import fintech.sms.IncomingSms;
import fintech.sms.spi.ReceivedIncomingSmsEvent;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.lending.events.OfferApprovedEvent;
import fintech.spain.alfa.product.lending.events.OfferRejectedEvent;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.platform.web.model.SpecialLinkActivated;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.registration.events.ClientDataUpdatedEvent;
import fintech.spain.alfa.product.workflow.dormants.event.InstantorCanceledByClient;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class WorkflowEventListeners {

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @EventListener
    public void onClientDataUpdated(ClientDataUpdatedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onAttachmentSaved(AttachmentSavedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onPhoneVerified(PhoneVerifiedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getPhoneContact().getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onPrimaryBankAccountSet(ClientPrimaryBankAccountSetEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getAccount().getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onLoanDisbursed(LoanDisbursedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getLoan().getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onInstantorFailed(InstantorResponseFailed event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(workflow -> {
            if (!workflow.getAttributes().containsKey(Attributes.INSTANTOR_FAILED_RESPONSE_ID)) {
                workflowService.setAttribute(workflow.getId(), Attributes.INSTANTOR_FAILED_RESPONSE_ID, event.getId().toString());
            } else {
                log.warn("Workflow [{}] already has Instantor failed response id attribute set, ignoring response with id [{}]", workflow.getId(), event.getId());
            }
        });
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onInstantorProcessed(InstantorResponseProcessed event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(workflow -> {
            if (!workflow.getAttributes().containsKey(Attributes.INSTANTOR_RESPONSE_ID)) {
                workflowService.setAttribute(workflow.getId(), Attributes.INSTANTOR_RESPONSE_ID, event.getId().toString());
            } else {
                log.warn("Workflow [{}] already has Instantor response id attribute set, ignoring response with id [{}]", workflow.getId(), event.getId());
            }
        });
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onIncomingSms(ReceivedIncomingSmsEvent event) {
        IncomingSms sms = event.getSms();

        String phoneNumber = StringUtils.substringAfter(sms.getPhoneNumber(), AlfaConstants.PHONE_COUNTRY_CODE);
        List<PhoneContact> contacts = phoneContactService.findByLocalPhoneNumber(phoneNumber);
        Optional<PhoneContact> firstPrimary = contacts.stream().filter(contact -> contact.isPrimary() && contact.isVerified()).findFirst();

        if (!firstPrimary.isPresent()) {
            log.info("Client with phone number {} not found. Processing SMS {} will be skipped", phoneNumber, sms);
            return;
        }

        String keyword = getSmsKeyword(sms);
        if (!StringUtils.isBlank(keyword)) {
            underwritingFacade.approveApplicationWithSms(firstPrimary.get().getClientId(), keyword);
        }
    }

    private String getSmsKeyword(IncomingSms sms) {
        return StringUtils.substringBefore(sms.getText(), " ").trim();
    }

    @EventListener
    public void onOfferApproved(OfferApprovedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onOfferRejected(OfferRejectedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onIovationBlackBox(IovationBlackBoxCreatedEvent event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onSpecialLinkActivated(SpecialLinkActivated event) {
        SpecialLink specialLink = event.getLink();
        List<Workflow> activeWorkflows = findActiveWorkflows(specialLink.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void onInstantorCanceledByClient(InstantorCanceledByClient event) {
        List<Workflow> activeWorkflows = findActiveWorkflows(event.getClientId());
        activeWorkflows.forEach(trigger(event));
    }

    @EventListener
    public void loanVoided(LoanVoidedEvent event) {
        List<Workflow> activeWorkflows = workflowService.findWorkflows(WorkflowQuery.byLoanId(event.getLoan().getId(), WorkflowStatus.ACTIVE));
        activeWorkflows.forEach(w -> workflowService.terminateWorkflow(w.getId(), "LoanVoided"));
    }

    private Consumer<Workflow> trigger(Object event) {
        return w -> workflowService.trigger(w.getId(), event);
    }

    private List<Workflow> findActiveWorkflows(Long clientId) {
        return workflowService.findWorkflows(WorkflowQuery.byClientId(clientId, WorkflowStatus.ACTIVE));
    }

}
