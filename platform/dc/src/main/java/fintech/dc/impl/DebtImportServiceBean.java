package fintech.dc.impl;

import fintech.Validate;
import fintech.crm.CrmConstants;
import fintech.crm.address.ClientAddressService;
import fintech.crm.address.SaveClientAddressCommand;
import fintech.crm.client.Client;
import fintech.crm.client.ClientImportService;
import fintech.crm.client.ClientService;
import fintech.crm.client.CreateClientCommand;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.contacts.*;
import fintech.crm.documents.AddIdentityDocumentCommand;
import fintech.crm.documents.IdentityDocumentNumberUtils;
import fintech.crm.documents.IdentityDocumentService;
import fintech.dc.DebtImportService;
import fintech.dc.DebtParser;
import fintech.dc.commands.DebtImportCommand;
import fintech.dc.db.DebtImportRepository;
import fintech.dc.model.DebtImport;
import fintech.dc.model.DebtParseResult;
import fintech.dc.model.DebtRow;
import fintech.filestorage.FileStorageService;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.CreateLoanCommand;
import fintech.lending.core.loan.events.LoanApplyStrategiesEvent;
import fintech.quartz.QuartzService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
public class DebtImportServiceBean implements DebtImportService {

    @Autowired
    private IdentityDocumentService identityDocumentService;

    @Autowired
    private DebtImportRepository debtImportRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private DebtProcessorRegistryBean debtProcessorRegistryBean;

    @Autowired
    private EmailContactService emailContactService;

    @Autowired
    private ClientService clientService;
    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private LoanService loanService;
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private QuartzService quartzService;

    @Autowired
    private ClientImportService clientImportService;
    public ProcessedInfo importDebts(DebtImportCommand command) {

        Long fileId = command.getFileId();
        Long debtImportId = command.getDebtImportId();

        log.info("Importing debts from fileId [{}]", fileId);

        DebtImport debtImport = debtImportRepository.getRequired(debtImportId).toValueObject();

        fileStorageService.get(fileId).orElseThrow(() -> new RuntimeException("Failed to find file : " + fileId));

        String fileFormatName = debtImport.getDebtImportFormat();
        Validate.notNull(fileFormatName, String.format("File format for company [%s] not defined", debtImport.getName()));
        DebtParser parser = debtProcessorRegistryBean.getParser(fileFormatName);

        DebtParseResult result = fileStorageService.readContents(fileId, parser::parse);
        result.setCompany(parser.getCompany());
        result.setPortfolio(command.getPortfolioName());
        result.setStatus(command.getStatus());
        result.setState(command.getState());
        return saveDebts(result);
    }

    @Data
    public static class ProcessedInfo {
        private int processedCount;
        private int totalCount;

        public ProcessedInfo(int processedCount, int totalCount) {
            this.processedCount = processedCount;
            this.totalCount = totalCount;
        }

        public int getProcessedCount() {
            return processedCount;
        }

        public int getTotalCount() {
            return totalCount;
        }
    }

    public ProcessedInfo saveDebts(DebtParseResult result) {
        int processedCount = 0;
        int total = result.getRows().size();
        if (result.getRows() != null && !result.getRows().isEmpty()) {
            for (DebtRow debtRow : result.getRows()) {
                CreateClientCommand command = new CreateClientCommand(debtRow.getClientNumber(),
                    debtRow.getFirstName(), debtRow.getLastName(), debtRow.getDni(), debtRow.getPhone(), debtRow.getIban());
                ClientEntity clientEntityId = clientImportService.createClient(command);

                Client client = clientService.get(clientEntityId.getId());
                Long clientId = client.getId();
                addAddress(clientId, "Actual", debtRow.getStreet(), debtRow.getDoorNumber(), debtRow.getProvince(), debtRow.getCity(), debtRow.getPostCode());
                addIdentityDocument(clientId, debtRow.getDni());
                addEmailContact(clientId, debtRow.getEmail());
                addPrimaryPhone(clientId, debtRow.getPhone());
                CreateLoanCommand createLoanCommand = new CreateLoanCommand();
                createLoanCommand.setClientId(clientId);
                createLoanCommand.setPortfolio(result.getPortfolio());
                createLoanCommand.setProductId(2L);
                createLoanCommand.setIssueDate(debtRow.getIssueDate());
                createLoanCommand.setLoanNumber(debtRow.getLoanNumber());
                createLoanCommand.setMaturityDate(debtRow.getDueDate());
                createLoanCommand.setPrincipalDisbursed(debtRow.getPrincipalDisbursed());
                createLoanCommand.setTotalOutstanding(debtRow.getTotalOutstandingAmount());
                createLoanCommand.setTotalDue(debtRow.getTotalOutstandingAmount());
                createLoanCommand.setCompany(result.getCompany());
                createLoanCommand.setDebtState(result.getState());
                createLoanCommand.setDebtStatus(result.getStatus());
                boolean loanExists = loanService.findLoans(LoanQuery.allLoansByNumber(debtRow.getLoanNumber())).size() > 0;
                if (!loanExists) {
                    Long loanId = loanService.issueLoan(createLoanCommand);
                    eventPublisher.publishEvent(new LoanApplyStrategiesEvent(loanId));
                    processedCount++;
                } else {
                    log.warn("Skip creating loan");
                }
                logProgress(processedCount, total);
            }
        }
        //run loan daily scheduler
        quartzService.triggerJob("LoanDailyScheduler");

        ProcessedInfo processedInfo = new ProcessedInfo(processedCount, total);
        log.info("Imported: {}", processedInfo);
        return processedInfo;
    }

    private static void logProgress(int processedCount, int totalCount) {
        double percentage = (double) processedCount / totalCount * 100;
        log.info(String.format("Progress: %.2f%% (%d/%d)", percentage, processedCount, totalCount));

    }

    private void addIdentityDocument(Long clientId, String documentNumber) {
        if (!IdentityDocumentNumberUtils.isValidDniOrNie(documentNumber)) {
            log.warn("The document number " + documentNumber + " is not valid");
        }
        String code = "es";
        AddIdentityDocumentCommand command = new AddIdentityDocumentCommand();
        command.setClientId(clientId);
        command.setNumber(documentNumber);
        command.setType(CrmConstants.IDENTITY_DOCUMENT_DNI);
        command.setCountryCodeOfNationality(code);
        Long docId = identityDocumentService.addDocument(command);
        try {
            identityDocumentService.makeDocumentPrimary(docId);
        } catch (Exception e) {
            log.warn("Skip making document primary ");
        }
    }

    private void addEmailContact(Long clientId, String email) {
        AddEmailContactCommand command = new AddEmailContactCommand();
        command.setClientId(clientId);
        command.setEmail(email);
        Long emailContactId = emailContactService.addEmailContact(command);
        try {
            emailContactService.makeEmailPrimary(emailContactId);
        } catch (Exception e) {
            log.warn("Skipping");
        }
    }

    private void addPrimaryPhone(Long clientId, String mobilePhone) {
        AddPhoneCommand command = new AddPhoneCommand()
            .setClientId(clientId)
            .setCountryCode("34")
            .setLocalNumber(PhoneNumberUtils.normalize(mobilePhone))
            .setType(PhoneType.MOBILE)
            .setSource(PhoneSource.REGISTRATION)
            .setLegalConsent(true);
        Long phoneContactId = phoneContactService.addPhoneContact(command);
        try {
            phoneContactService.makePhonePrimary(phoneContactId);
        } catch (Exception e) {
            log.warn("Skipping");
        }

    }

    @Autowired
    private ClientAddressService clientAddressService;
    private void addAddress(Long clientId, String type, String street, String houseNumber, String province, String city, String postalCode) {
        SaveClientAddressCommand command = new SaveClientAddressCommand();
        command.setClientId(clientId);
        command.setType(type);
        command.setStreet(street);
        command.setHouseNumber(houseNumber);
        command.setProvince(province);
        command.setCity(city);
        command.setPostalCode(postalCode);
//        command.setHousingTenure(housingTenure);

        clientAddressService.addAddress(command);
    }

}
