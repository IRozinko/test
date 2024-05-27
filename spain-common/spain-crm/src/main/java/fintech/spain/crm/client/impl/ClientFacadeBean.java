package fintech.spain.crm.client.impl;

import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.documents.IdentityDocumentService;
import fintech.crm.logins.EmailLoginService;
import fintech.risk.checklist.CheckListConstants;
import fintech.risk.checklist.CheckListService;
import fintech.risk.checklist.commands.AddCheckListEntryCommand;
import fintech.risk.checklist.model.CheckListQuery;
import fintech.spain.crm.client.ClientFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class ClientFacadeBean implements ClientFacade {

    private final IdentityDocumentService identityDocumentService;
    private final CheckListService checkListService;
    private final EmailLoginService emailLoginService;
    private final PhoneContactService phoneContactService;
    private final ClientService clientService;

    @Autowired
    public ClientFacadeBean(IdentityDocumentService identityDocumentService, CheckListService checkListService,
                            EmailLoginService emailLoginService, PhoneContactService phoneContactService,
                            ClientService clientService) {
        this.identityDocumentService = identityDocumentService;
        this.checkListService = checkListService;
        this.emailLoginService = emailLoginService;
        this.phoneContactService = phoneContactService;
        this.clientService = clientService;
    }

    @Override
    public void blacklistDocument(Long clientId, String docType, String comment) {
        identityDocumentService.findPrimaryDocument(clientId, docType)
            .ifPresent(identityDocument -> checkListService.addEntry(
                new AddCheckListEntryCommand(CheckListConstants.CHECKLIST_TYPE_DNI,
                    identityDocument.getNumber(), comment))
            );
    }

    @Override
    public boolean isDocumentBlacklisted(Long clientId, String docType) {
        return identityDocumentService.findPrimaryDocument(clientId, docType)
            .map(identityDocument -> !checkListService.isAllowed(
                CheckListQuery.builder()
                    .type(CheckListConstants.CHECKLIST_TYPE_DNI)
                    .value1(identityDocument.getNumber())
                    .build()))
            .orElse(false);
    }

    @Override
    public void blacklistEmail(Long clientId, String comment) {
        emailLoginService.findByClientId(clientId)
            .ifPresent(emailLogin -> checkListService.addEntry(
                new AddCheckListEntryCommand(CheckListConstants.CHECKLIST_TYPE_EMAIL,
                    emailLogin.getEmail(), comment))
            );
    }

    @Override
    public boolean isEmailBlacklisted(Long clientId) {
        return emailLoginService.findByClientId(clientId)
            .map(emailLogin -> !checkListService.isAllowed(
                CheckListQuery.builder()
                    .type(CheckListConstants.CHECKLIST_TYPE_EMAIL)
                    .value1(emailLogin.getEmail())
                    .build()))
            .orElse(false);
    }

    @Override
    public void blacklistPhone(Long clientId, String comment) {
        Client client = clientService.get(clientId);
        Optional.ofNullable(client.getPhone())
            .ifPresent(phone -> checkListService.addEntry(
                new AddCheckListEntryCommand(CheckListConstants.CHECKLIST_TYPE_PHONE, phone, comment))
            );

        phoneContactService.findPrimaryPhone(clientId)
            .ifPresent(phone -> checkListService.addEntry(
                new AddCheckListEntryCommand(CheckListConstants.CHECKLIST_TYPE_PHONE, phone.getPhoneNumber(), comment))
            );
    }

    @Override
    public boolean isPhoneBlacklisted(Long clientId) {
        boolean blacklisted = isClientPhoneBlacklisted(clientId).orElse(false);
        if (blacklisted) {
            Optional<Boolean> isPrimaryPhoneBlacklisted = isPrimaryPhoneBlacklisted(clientId);
            if (isPrimaryPhoneBlacklisted.isPresent()) {
                blacklisted = isPrimaryPhoneBlacklisted.get();
            }
        }

        return blacklisted;
    }

    private Optional<Boolean> isClientPhoneBlacklisted(Long clientId) {
        Client client = clientService.get(clientId);
        return Optional.ofNullable(client.getPhone())
            .map(phone -> !checkListService.isAllowed(CheckListQuery.builder()
                .type(CheckListConstants.CHECKLIST_TYPE_PHONE)
                .value1(phone)
                .build()));
    }

    private Optional<Boolean> isPrimaryPhoneBlacklisted(Long clientId) {
        return phoneContactService.findPrimaryPhone(clientId)
            .map(phone -> !checkListService.isAllowed(CheckListQuery.builder()
                .type(CheckListConstants.CHECKLIST_TYPE_PHONE)
                .value1(phone.getPhoneNumber())
                .build()));
    }

}
