package fintech.spain.alfa.bo.api;

import fintech.Validate;
import fintech.crm.CrmConstants;
import fintech.crm.address.ClientAddressService;
import fintech.crm.address.SaveClientAddressCommand;
import fintech.crm.bankaccount.AddClientBankAccountCommand;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.Gender;
import fintech.crm.client.UpdateClientCommand;
import fintech.crm.contacts.AddEmailContactCommand;
import fintech.crm.contacts.AddPhoneCommand;
import fintech.crm.contacts.EmailContactService;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.contacts.PhoneSource;
import fintech.crm.contacts.PhoneType;
import fintech.crm.documents.AddIdentityDocumentCommand;
import fintech.crm.documents.IdentityDocumentNumberUtils;
import fintech.crm.documents.IdentityDocumentService;
import fintech.crm.logins.ChangeEmailCommand;
import fintech.crm.logins.EmailLoginService;
import fintech.spain.platform.web.validations.IbanNumberValidator;
import fintech.spain.alfa.bo.model.AddClientAddressRequest;
import fintech.spain.alfa.bo.model.DocumentCheckUpdateRequest;
import fintech.spain.alfa.bo.model.SaveIdentificationDocumentRequest;
import fintech.spain.alfa.bo.model.UpdateClientDataRequest;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.documents.DocumentType;
import fintech.spain.alfa.product.documents.IdentificationDocumentsService;
import fintech.spain.alfa.product.documents.SaveIdentificationDocumentCommand;
import fintech.task.TaskService;
import fintech.task.model.Task;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.notBlank;

@Slf4j
@Component
public class AlfaApiFacade {

    @Autowired
    private ClientService clientService;

    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    private ClientBankAccountService bankAccountService;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private EmailContactService emailContactService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private IdentificationDocumentsService identificationDocumentsService;

    @Transactional
    public void documentCheckUpdate(DocumentCheckUpdateRequest request) {
        Task task = taskService.get(request.getTaskId());
        Client client = clientService.get(task.getClientId());

        Validate.notBlank(request.getAccountNumber(), "Blank account number");

        boolean firstNameChanged = !StringUtils.equals(client.getFirstName(), request.getFirstName());
        boolean lastNameChanged = !StringUtils.equals(client.getLastName(), request.getLastName());
        boolean dateOfBirthChanged = !Objects.equals(client.getDateOfBirth(), request.getDateOfBirth());
        boolean secondLastNameChanged = !StringUtils.equals(client.getSecondLastName(), request.getSecondLastName());

        if (firstNameChanged || lastNameChanged || dateOfBirthChanged || secondLastNameChanged) {
            notBlank(request.getFirstName(), "First name not valid: [%s]", request.getFirstName());
            notBlank(request.getLastName(), "Last name not valid: [%s]", request.getLastName());

            UpdateClientCommand command = UpdateClientCommand.fromClient(client);
            command.setFirstName(request.getFirstName());
            command.setLastName(request.getLastName());
            command.setDateOfBirth(request.getDateOfBirth());
            command.setSecondLastName(request.getSecondLastName());
            clientService.update(command);
        }

        boolean bankAccountChanged = !equalsIgnoreCase(client.getAccountNumber(), request.getAccountNumber());
        if (bankAccountChanged) {
            Validate.isTrue(IbanNumberValidator.isValid(request.getAccountNumber()),
                "Bank account number not valid: [%s]", request.getAccountNumber());

            AddClientBankAccountCommand bankAccountCommand = new AddClientBankAccountCommand();
            bankAccountCommand.setAccountNumber(request.getAccountNumber());
            bankAccountCommand.setClientId(client.getId());
            Long bankAccountId = bankAccountService.addBankAccount(bankAccountCommand);
            bankAccountService.makePrimary(bankAccountId);
        }

        boolean documentNumberChanged = !equalsIgnoreCase(client.getDocumentNumber(), request.getDocumentNumber());
        if (documentNumberChanged) {
            Validate.isTrue(IdentityDocumentNumberUtils.isValidDniOrNie(request.getDocumentNumber()),
                "Document number not valid: [%s]", request.getDocumentNumber());

            AddIdentityDocumentCommand identityDocumentCommand = new AddIdentityDocumentCommand();
            identityDocumentCommand.setClientId(client.getId());
            identityDocumentCommand.setNumber(request.getDocumentNumber());
            identityDocumentCommand.setType(CrmConstants.IDENTITY_DOCUMENT_DNI);
            Long docId = identityDocumentService.addDocument(identityDocumentCommand);
            identityDocumentService.makeDocumentPrimary(docId);
        }
    }

    @Transactional
    public void updateClient(UpdateClientDataRequest request) {
        Client client = clientService.get(request.getClientId());
        log.info("Updating client data [{}] for client [{}]", request, client);

        boolean firstNameChanged = !StringUtils.equals(client.getFirstName(), request.getFirstName());
        boolean lastNameChanged = !StringUtils.equals(client.getLastName(), request.getLastName());
        boolean dateOfBirthChanged = !Objects.equals(client.getDateOfBirth(), request.getDateOfBirth());
        boolean secondLastNameChanged = !StringUtils.equals(client.getSecondLastName(), request.getSecondLastName());
        boolean blockCommunicationChanged = client.isBlockCommunication() != request.isBlockCommunication();
        boolean excludedFromASNEFChanged = client.isExcludedFromASNEF() != request.isExcludedFromASNEF();
        boolean genderChanged = !client.getGender().name().equalsIgnoreCase(request.getGender());
        if (firstNameChanged || lastNameChanged || dateOfBirthChanged || secondLastNameChanged ||
            blockCommunicationChanged || excludedFromASNEFChanged || genderChanged) {
            notBlank(request.getFirstName(), "First name not valid: [%s]", request.getFirstName());
            notBlank(request.getLastName(), "Last name not valid: [%s]", request.getLastName());

            UpdateClientCommand command = UpdateClientCommand.fromClient(client);
            command.setFirstName(request.getFirstName());
            command.setGender(Gender.valueOf(request.getGender()));
            command.setLastName(request.getLastName());
            command.setDateOfBirth(request.getDateOfBirth());
            command.setSecondLastName(request.getSecondLastName());
            command.setBlockCommunication(request.isBlockCommunication());
            command.setExcludedFromASNEF(request.isExcludedFromASNEF());
            clientService.update(command);
        }

        boolean emailChanged = !StringUtils.equals(client.getEmail(), request.getEmail());
        if (emailChanged) {
            Validate.isTrue(EmailValidator.getInstance().isValid(request.getEmail()),
                "Email not valid: [%s]", request.getEmail());

            ChangeEmailCommand changeEmailCommand = new ChangeEmailCommand();
            changeEmailCommand.setClientId(client.getId());
            changeEmailCommand.setNewEmail(request.getEmail());
            changeEmailCommand.setCurrentEmail(client.getEmail());
            emailLoginService.changeEmail(changeEmailCommand);

            AddEmailContactCommand emailCommand = new AddEmailContactCommand();
            emailCommand.setClientId(client.getId());
            emailCommand.setEmail(request.getEmail());
            final Long emailContactId = emailContactService.addEmailContact(emailCommand);
            emailContactService.makeEmailPrimary(emailContactId);
        }

        boolean phoneChanged = !StringUtils.equals(client.getPhone(), request.getPhone());
        if (phoneChanged) {
            AddPhoneCommand addPhoneCommand = new AddPhoneCommand()
                .setClientId(client.getId())
                .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
                .setLocalNumber(request.getPhone())
                .setType(PhoneType.MOBILE)
                .setSource(PhoneSource.OTHER)
                .setLegalConsent(true);

            Long phoneContactId = phoneContactService.addPhoneContact(addPhoneCommand);
            phoneContactService.makePhonePrimary(phoneContactId);
        }

        if (isNotBlank(request.getAdditionalPhone())) {
            AddPhoneCommand addPhoneCommand = new AddPhoneCommand()
                .setClientId(client.getId())
                .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
                .setLocalNumber(request.getAdditionalPhone())
                .setType(PhoneType.OTHER)
                .setSource(PhoneSource.OTHER)
                .setLegalConsent(true);

            phoneContactService.addPhoneContact(addPhoneCommand);
        }

        boolean bankAccountChanged = !equalsIgnoreCase(client.getAccountNumber(), request.getAccountNumber());
        if (bankAccountChanged) {
            Validate.isTrue(IbanNumberValidator.isValid(request.getAccountNumber()),
                "Bank account number not valid: [%s]", request.getAccountNumber());

            AddClientBankAccountCommand bankAccountCommand = new AddClientBankAccountCommand();
            bankAccountCommand.setAccountNumber(request.getAccountNumber());
            bankAccountCommand.setClientId(client.getId());
            bankAccountCommand.setPrimaryAccount(true);
            bankAccountService.addBankAccount(bankAccountCommand);
        }
    }

    @Transactional
    void addClientAddress(AddClientAddressRequest request) {
        SaveClientAddressCommand command = new SaveClientAddressCommand();
        command.setClientId(request.getClientId());
        command.setType(request.getType());
        command.setStreet(request.getStreet());
        command.setHouseNumber(request.getHouseNumber());
        command.setProvince(request.getProvince());
        command.setCity(request.getCity());
        command.setPostalCode(request.getPostalCode());
        command.setHousingTenure(request.getHousingTenure());

        clientAddressService.addAddress(command);
    }

    @Transactional
    public void saveIdentificationDocument(SaveIdentificationDocumentRequest request) {
        Validate.notNull(request.getDocumentNumber(), "Null document number");
        Validate.notNull(request.getDocumentType(), "Null document number");
        Validate.notNull(request.getFrontAttachment(), "Null front attachment");

        identificationDocumentsService.saveIdentificationDocument(new SaveIdentificationDocumentCommand()
            .setClientId(request.getClientId())
            .setTaskId(request.getTaskId())
            .setDocumentType(DocumentType.valueOf(request.getDocumentType().name()))
            .setDocumentNumber(request.getDocumentNumber())
            .setName(request.getName())
            .setSurname1(request.getSurname1())
            .setSurname2(request.getSurname2())
            .setGender(request.getGender())
            .setNationality(request.getNationality())
            .setDateOfBirth(request.getDateOfBirth())
            .setExpirationDate(request.getExpirationDate())
            .setStreet(request.getStreet())
            .setHouse(request.getHouse())
            .setCity(request.getCity())
            .setProvince(request.getProvince())
            .setPlaceOfBirth(request.getPlaceOfBirth())
            .setFrontFileId(request.getFrontAttachment().getFileId())
            .setFrontFileName(request.getFrontAttachment().getFileName())
            .setBackFileId(request.getBackAttachment() != null ? request.getBackAttachment().getFileId() : null)
            .setBackFileName(request.getBackAttachment() != null ? request.getBackAttachment().getFileName() : null)
            .setNotifyOnSave(request.isNotifyOnSave())
        );

        Client client = clientService.get(request.getClientId());
        log.info("Updating documentNumber [{}] for client [{}]", request.getDocumentNumber(), client);

        boolean documentNumberChanged = !equalsIgnoreCase(client.getDocumentNumber(), request.getDocumentNumber());
        if (documentNumberChanged) {
            Validate.isTrue(IdentityDocumentNumberUtils.isValidDniOrNie(request.getDocumentNumber()),
                "Document number not valid: [%s]", request.getDocumentNumber());

            AddIdentityDocumentCommand identityDocumentCommand = new AddIdentityDocumentCommand();
            identityDocumentCommand.setClientId(client.getId());
            identityDocumentCommand.setNumber(request.getDocumentNumber());
            identityDocumentCommand.setType(CrmConstants.IDENTITY_DOCUMENT_DNI);
            Long docId = identityDocumentService.addDocument(identityDocumentCommand);
            identityDocumentService.makeDocumentPrimary(docId);
        }
    }
}
