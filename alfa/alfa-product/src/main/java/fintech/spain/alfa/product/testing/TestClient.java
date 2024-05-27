package fintech.spain.alfa.product.testing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fintech.RandomUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.affiliate.AffiliateService;
import fintech.affiliate.model.AddLeadCommand;
import fintech.crm.address.ClientAddress;
import fintech.crm.address.ClientAddressService;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.bankaccount.AddClientBankAccountCommand;
import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.Gender;
import fintech.crm.client.db.ClientEntity;
import fintech.crm.client.db.ClientRepository;
import fintech.crm.contacts.PhoneContact;
import fintech.crm.contacts.PhoneContactService;
import fintech.crm.db.Entities;
import fintech.crm.documents.db.IdentityDocumentRepository;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.crm.logins.db.EmailLoginRepository;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.decision.spi.MockDecisionEngine;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.instantor.db.InstantorResponseRepository;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.application.commands.SaveCreditLimitCommand;
import fintech.lending.core.discount.ApplyDiscountCommand;
import fintech.lending.core.discount.DiscountService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.notification.NotificationHelper;
import fintech.spain.crm.client.ClientDeleteService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1Form;
import fintech.spain.alfa.product.affiliate.AffiliateRegistrationStep1FormV1;
import fintech.spain.alfa.product.db.IdentificationDocumentEntity;
import fintech.spain.alfa.product.db.IdentificationDocumentRepository;
import fintech.spain.alfa.product.documents.DocumentType;
import fintech.spain.alfa.product.documents.IdentificationDocumentsService;
import fintech.spain.alfa.product.documents.InvalidateIdentificationDocument;
import fintech.spain.alfa.product.documents.SaveIdentificationDocumentCommand;
import fintech.spain.alfa.product.documents.ValidateIdentificationDocument;
import fintech.spain.alfa.product.lending.Inquiry;
import fintech.spain.alfa.product.lending.LineOfCreditFacade;
import fintech.spain.alfa.product.lending.LoanIssueResult;
import fintech.spain.alfa.product.lending.OfferSettings;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.registration.forms.AddressData;
import fintech.spain.alfa.product.registration.forms.AffiliateData;
import fintech.spain.alfa.product.registration.forms.ApplicationForm;
import fintech.spain.alfa.product.registration.forms.SignUpForm;
import fintech.spain.alfa.product.web.WebLoginService;
import fintech.spain.alfa.product.workflow.personal.ChangeBankAccountWorkflow;
import fintech.workflow.StartWorkflowCommand;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowQuery;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.iban4j.Iban;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static fintech.BigDecimalUtils.amount;
import static fintech.lending.core.application.LoanApplicationQuery.byClientId;
import static java.util.Objects.isNull;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Accessors(chain = true)
public class TestClient {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private InstantorResponseRepository instantorResponseRepository;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private EmailLoginRepository emailLoginRepository;

    @Autowired
    private ClientBankAccountService bankAccountService;

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LineOfCreditFacade lineOfCreditFacade;

    @Autowired
    private NotificationHelper notificationHelper;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private AffiliateService affiliateService;

    @Autowired
    private IdentificationDocumentsService identificationDocumentsService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private IdentityDocumentRepository documentRepository;

    @Autowired
    private WebLoginService webLoginService;

    @Autowired
    private IdentificationDocumentRepository identificationDocumentRepository;

    @Autowired
    private ClientDeleteService clientDeleteService;

    @Autowired
    private MockDecisionEngine decisionEngine;

    @Setter
    @Getter
    private String email = RandomData.randomEmail();

    @Setter
    @Getter
    private String mobilePhone = RandomData.randomPhoneNumber();

    @Setter
    @Getter
    private String dni = RandomData.randomDni();

    @Setter
    @Getter
    private String countryCodeOfNationality = "ES";

    @Setter
    @Getter
    private LocalDate dateOfBirth = TimeMachine.today().minusYears(25);

    @Setter
    @Getter
    private String gender = Gender.MALE.name();

    @Setter
    @Getter
    private Iban iban = RandomData.randomIban();

    @Setter
    @Getter
    private String firstName = "John";

    @Setter
    @Getter
    private String lastName = "Demo";

    @Setter
    @Getter
    private String secondLastName = "Big";

    @Setter
    @Getter
    private BigDecimal amount = amount(100);

    @Setter
    @Getter
    private Long termInDays = 30L;

    @Setter
    @Getter
    private String blackbox = "Blackbox_" + RandomStringUtils.randomAlphabetic(18);

    @Setter
    @Getter
    private String promoCode;

    @Getter
    private SignUpForm signUpForm = new SignUpForm()
        .setAcceptMarketing(true)
        .setAcceptTerms(true)
        .setAcceptVerification(true)
        .setPassword("test1234");

    @Getter
    public ApplicationForm applicationForm = new ApplicationForm()
        .setAddress(new AddressData()
            .setCity("BARCELONA")
            .setStreet("AVENIDA DIAGONAL")
            .setHouseNumber("589")
            .setPostalCode("08014")
            .setHousingTenure("OWNER"))
        .setEmploymentStatus("EMPLOYEE")
        .setEmploymentDetail("CONTRACTOR")
        .setMonthlyIncome("1500")
        .setFamilyStatus("SINGLE")
        .setNumberOfDependants("1")
        .setLoanPurpose("HOUSE_RENOVATION")
        .setGender(Gender.MALE.name())
        .setDateOfBirth(this.dateOfBirth)
        .setIncomeSource("Salary");

    @Getter
    public AffiliateRegistrationStep1Form affiliateRegistrationStep1Form = new AffiliateRegistrationStep1Form()
        .setAmount(amount(500.00))
        .setTerm(30L)
        .setFirstName(this.firstName)
        .setLastName(this.lastName)
        .setDocumentNumber(this.dni)
        .setGender(StringUtils.lowerCase(Gender.MALE.name()))
        .setDateOfBirth(this.dateOfBirth)
        .setStreet("Calle Archiduque Alberto 7")
        .setPostalCode("28050")
        .setCity("San Fernando De Henares")
        .setMobilePhone(this.mobilePhone)
        .setEmail(this.email)
        .setIban(RandomData.randomIban().toString())
        .setAcceptMarketing(true)
        .setTos(1)
        .setMonthlyExpenses(amount(1_000))
        .setMaritalStatus("Divorciado")
        .setNumberOfDependants(1)
        .setEducation("secondary")
        .setWorkSector("medicine")
        .setOccupation("tourism")
        .setEmployedSince(TimeMachine.today().minusYears(5))
        .setNextSalaryDate(TimeMachine.today().plusDays(17))
        .setHousingTenure("Vivienda en alquiler")
        .setExcludedFromASNEF(true)
        .setIncomeSource("unknown")
        .setNetoIncome(amount(1_500))
        .setBlackbox(getBlackbox());

    @Getter
    public AffiliateRegistrationStep1FormV1 affiliateRegistrationStep1FormV1 = new AffiliateRegistrationStep1FormV1()
        .setAmount(amount(500.00))
        .setTerm(30L)
        .setFirstName(this.firstName)
        .setLastName(this.lastName)
        .setDocumentNumber(this.dni)
        .setGender(StringUtils.lowerCase(Gender.MALE.name()))
        .setDateOfBirth(this.dateOfBirth)
        .setStreet("Calle Archiduque Alberto 7")
        .setPostalCode("28050")
        .setCity("San Fernando De Henares")
        .setMobilePhone(this.mobilePhone)
        .setEmail(this.email)
        .setIban(RandomData.randomIban().toString())
        .setAcceptMarketing(true)
        .setTos(1)
        .setMonthlyExpenses(amount(1_000))
        .setMaritalStatus("Divorciado")
        .setNumberOfDependants(1)
        .setEducation("secondary")
        .setWorkSector("medicine")
        .setOccupation("tourism")
        .setEmployedSince(TimeMachine.today().minusYears(5))
        .setNextSalaryDate(TimeMachine.today().plusDays(17))
        .setHousingTenure("Vivienda en alquiler")
        .setExcludedFromASNEF(true)
        .setIncomeSource("unknown")
        .setNetoIncome(amount(1_500));

    private Long applicationId;

    @Setter
    private Long clientId;

    private Long identificationDocumentId;

    public TestClient() {
    }

    public TestClient(Long clientId) {
        this.clientId = clientId;
    }

    @PostConstruct
    public void init() {
        if (isNull(clientId)) {
            return;
        }

        Client client = clientService.get(clientId);
        Optional<ClientBankAccount> bankAccount = bankAccountService.findPrimaryByClientId(clientId);

        setFirstName(client.getFirstName());
        setSecondLastName(client.getSecondLastName());
        setLastName(client.getLastName());

        setEmail(client.getEmail());
        setDateOfBirth(client.getDateOfBirth());
        setDni(client.getDocumentNumber());

        setMobilePhone(client.getPhone());
        bankAccount.map(ClientBankAccount::getAccountNumber).map(Iban::valueOf).ifPresent(this::setIban);
    }

//    public TestClient signUp() {
//        this.clientId = registrationFacade.signUp(buildSignUpForm(), true);
//        List<LoanApplication> loanApplications = loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN));
//        this.applicationId = loanApplications.get(0).getId();
//        return this;
//    }
//
//    public TestClient signUpWithApplication() {
//        this.clientId = registrationFacade.signUp(buildSignUpForm(), true);
//        registrationFacade.saveApplicationData(clientId, applicationForm);
//        List<LoanApplication> loanApplications = loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN));
//        this.applicationId = loanApplications.get(0).getId();
//        return this;
//    }

    public String token() {
        return Optional.ofNullable(clientId)
            .map(id -> webLoginService.login(id, Duration.ofHours(1)))
            .orElseThrow(IllegalStateException::new);
    }

    public SignUpForm buildSignUpForm() {
        this.signUpForm
            .setEmail(this.email)
            .setMobilePhone(this.mobilePhone)
            .setFirstName(this.firstName)
            .setLastName(this.lastName)
            .setSecondLastName(this.secondLastName)
            .setDocumentNumber(this.dni)
            .setCountryCodeOfNationality(this.countryCodeOfNationality)
            .setAmount(this.amount)
            .setTermInDays(this.termInDays)
            .setBlackbox(this.blackbox)
            .setPromoCode(this.promoCode);
        return this.signUpForm;
    }

    public SignUpForm buildAffiliateSignUpForm() {
        this.signUpForm
            .setEmail(this.email)
            .setMobilePhone(this.mobilePhone)
            .setFirstName(this.firstName)
            .setLastName(this.lastName)
            .setSecondLastName(this.secondLastName)
            .setDocumentNumber(this.dni)
            .setAmount(this.amount)
            .setTermInDays(this.termInDays)
            .setBlackbox(this.blackbox)
            .setAffiliate(new AffiliateData().setAffiliateName("Affiliate"));
        return this.signUpForm;
    }


//    public TestClient registerDirectly() {
//        this.clientId = registrationFacade.signUp(buildSignUpForm(), false);
//        saveApplicationForm();
//        addPrimaryBankAccount();
//        return this;
//    }

    public TestClient addPrimaryBankAccount() {
        return addPrimaryBankAccount(iban.toString());
    }

    public TestClient addPrimaryBankAccount(String iban) {
        AddClientBankAccountCommand command = new AddClientBankAccountCommand();
        command.setAccountNumber(iban);
        command.setPrimaryAccount(true);
        command.setNumberOfTransactions(0L);
        command.setAccountOwnerName(fullName());
        command.setBankName("N/A");
        command.setCurrency(AlfaConstants.CURRENCY);
        command.setBalance(amount(0));
        command.setClientId(clientId);
        bankAccountService.addBankAccount(command);
        return this;
    }


    public TestClient deactivatePrimaryBankAccount() {
        bankAccountService.deactivatePrimaryAccount(clientId);
        return this;
    }

    public TestClient activateDiscount(BigDecimal rate) {
        discountService.applyDiscount(new ApplyDiscountCommand()
            .setClientId(clientId)
            .setEffectiveFrom(TimeMachine.today())
            .setEffectiveTo(TimeMachine.today().plusDays(1))
            .setMaxTimesToApply(1)
            .setRateInPercent(rate));
        return this;
    }

    public TestLoan issueLoan(BigDecimal principal, Long term, LocalDate issueDate) {
        return issueLoan(new Inquiry()
            .setPrincipal(principal)
            .setInterestDiscountPercent(amount(0))
            .setSubmittedAt(issueDate.atStartOfDay())
            .setTermInDays(term)
        );
    }

    public TestLoan issueActiveLoan(BigDecimal principal, Long term, LocalDate issueDate) {
        return issueLoan(principal, term, issueDate)
            .exportDisbursements(issueDate)
            .settleDisbursements(issueDate);
    }

    public TestLoan issueLoan(Inquiry inquiry) {
        this.applicationId = underwritingFacade.submitApplication(getClientId(), inquiry);
        inquiry.setApplicationId(getApplicationId());
        this.loanApplicationService.saveCreditLimit(new SaveCreditLimitCommand(this.applicationId, inquiry.getPrincipal()));
        underwritingFacade.prepareOffer(getApplicationId(), inquiry.getSubmittedAt().toLocalDate());
        LoanIssueResult result = underwritingFacade.issueLoan(getApplicationId(), inquiry.getSubmittedAt().toLocalDate());
        return TestFactory.loan(this, result.getLoanId());
    }

    public TestApplication submitApplication(Inquiry inquiry) {
        this.applicationId = underwritingFacade.submitApplication(getClientId(), inquiry);
        return TestFactory.application(this, this.applicationId);
    }

    public TestClient randomEmailAndName(String prefix) {
        long count = clientRepository.count(Entities.client.firstName.startsWith(prefix));
        String randomPart = "" + (count + 1);
        this.email = StringUtils.replace(prefix, " ", "_") + "_" + randomPart + "@mailinator.com";
        this.firstName = prefix + " (" + randomPart + ")";
        return this;
    }

    public TestClient submitApplicationAndStartFirstLoanWorkflow(BigDecimal amount, Long termInDays, LocalDate submitDate) {
        Long applicationId = underwritingFacade.submitApplication(getClientId(), new Inquiry()
            .setPrincipal(amount)
            .setTermInDays(termInDays)
            .setInterestDiscountPercent(amount(0))
            .setSubmittedAt(submitDate.atStartOfDay())
        );
        underwritingFacade.startFirstLoanApplicationWorkflow(applicationId, ImmutableMap.of());
        return this;
    }

    public TestClient submitApplicationAndStartAffiliateWorkflow(BigDecimal amount, Long termInDays, LocalDate submitDate) {
        Long applicationId = underwritingFacade.submitApplication(getClientId(), new Inquiry()
            .setPrincipal(amount)
            .setTermInDays(termInDays)
            .setInterestDiscountPercent(amount(0))
            .setSubmittedAt(submitDate.atStartOfDay())
        );

        boolean repeatedClient = !loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.CLOSED)).isEmpty();
        AddLeadCommand command = new AddLeadCommand();
        command.setClientId(clientId);
        command.setRepeatedClient(repeatedClient);
        command.setApplicationId(applicationId);
        command.setAffiliateName(signUpForm.getAffiliate().getAffiliateName());
        command.setCampaign(signUpForm.getAffiliate().getCampaign());
        command.setAffiliateLeadId(signUpForm.getAffiliate().getAffiliateLeadId());
        command.setSubAffiliateLeadId1(signUpForm.getAffiliate().getSubAffiliateLeadId1());
        command.setSubAffiliateLeadId2(signUpForm.getAffiliate().getSubAffiliateLeadId2());
        command.setSubAffiliateLeadId3(signUpForm.getAffiliate().getSubAffiliateLeadId3());
        affiliateService.addLead(command);

        underwritingFacade.startFirstLoanAffiliatesApplicationWorkflow(applicationId, ImmutableMap.of());
        return this;
    }

    public TestClient submitChangeBankAccountWorkflow() {
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setClientId(this.clientId);
        command.setWorkflowName(ChangeBankAccountWorkflow.WORKFLOW);
        workflowService.startWorkflow(command);
        return this;
    }

    public Long getClientId() {
        return Validate.notNull(clientId, "No client id");
    }

    public Long getApplicationId() {
        return Validate.notNull(applicationId, "No application id");
    }

    public TestApplication toApplication() {
        return TestFactory.application(this, getApplicationId());
    }

    @Transactional
    public TestClient updateClientHashPassword(String password) {
        emailLoginRepository.getOptional(Entities.emailLogin.client.id.eq(clientId)).ifPresent(c -> {
            c.setPassword(password);
            emailLoginRepository.save(c);
        });
        return this;
    }

    @Transactional
    public TestClient setPasswordTemporary() {
        emailLoginRepository.getOptional(Entities.emailLogin.client.id.eq(clientId)).ifPresent(c -> {
            c.setTemporaryPassword(true);
            emailLoginRepository.save(c);
        });
        return this;
    }

    public Client getClient() {
        return clientService.get(this.clientId);
    }

    public ClientAddress getPrimaryAddress() {
        return clientAddressService.getClientPrimaryAddress(getClientId(), AlfaConstants.ADDRESS_TYPE_ACTUAL).orElseThrow(error("No primary address"));
    }

    public PhoneContact getPrimaryPhone() {
        return phoneContactService.findPrimaryPhone(getClientId()).orElseThrow(error("No primary phone"));
    }

    public EmailLogin getEmailLogin() {
        return emailLoginService.findByClientId(getClientId()).orElseThrow(error("No email login"));
    }

    public String fullName() {
        return StringUtils.join(ImmutableList.of(this.firstName, this.lastName, this.secondLastName), ' ');
    }

    static Supplier<? extends RuntimeException> error(String message) {
        return () -> new IllegalStateException(message);
    }

    public long emailCount(String cmsKey) {
        return notificationHelper.countEmails(getClientId(), cmsKey);
    }

    public long smsCount(String cmsKey) {
        return notificationHelper.countSms(getClientId(), cmsKey);
    }

    public TestChangeBankAccountWorkflow toChangeBankAccountWorkflow() {
        List<Workflow> workflows = workflowService.findWorkflows(WorkflowQuery.byClientId(this.clientId, ChangeBankAccountWorkflow.WORKFLOW, WorkflowStatus.ACTIVE));
        Validate.isTrue(workflows.size() == 1, "No single active Change Bank Account workflow found");
        return TestFactory.changeBankAccountWorkflow(this, workflows.get(0).getId());
    }

    public <T extends TestWorkflow<T>> T toWorkflow(String wfName, Class<T> wfClass) {
        List<Workflow> workflows = workflowService.findWorkflows(WorkflowQuery.byClientId(this.clientId, wfName, WorkflowStatus.ACTIVE));
        Validate.isTrue(workflows.size() == 1, "No single active %s workflow found", wfName);
        return TestFactory.workflow(wfClass, this, workflows.get(0).getId());
    }

    public TestClient saveApplicationForm() {
        applicationForm.setDateOfBirth(this.dateOfBirth);
        return this;
    }

    public List<Loan> findOpenLoans() {
        return loanService.findLoans(LoanQuery.openLoans(getClientId()));
    }

    public List<Loan> findPaidLoans() {
        return loanService.findLoans(LoanQuery.paidLoans(getClientId()));
    }

    public List<Loan> findAllLoans() {
        return loanService.findLoans(LoanQuery.allLoans(getClientId()));
    }

    public OfferSettings offerSettings(LocalDate when) {
        return underwritingFacade.clientOfferSettings(getClientId(), when);
    }

    public TestClient retryApplication() {
        applicationId = loanApplicationService.findLatest(byClientId(getClientId())).map(LoanApplication::getId).orElseThrow(() -> new IllegalStateException("No application found"));
        underwritingFacade.retryApplication(getApplicationId());
        return this;
    }

    public TestClient submitLineOfCreditAndStartWorkflow(BigDecimal amount, LocalDateTime localDateTime) {
        this.applicationId = lineOfCreditFacade.apply(this.clientId, amount, localDateTime);
        return this;
    }

    public TestClient cancelActiveApplication() {
        loanApplicationService.findLatest(byClientId(getClientId())).ifPresent(
            application -> loanApplicationService.cancel(application.getId(), "")
        );
        return this;
    }

    @Transactional
    public TestClient acceptMarketing(boolean isAccept) {
        ClientEntity entity = clientRepository.getRequired(clientId);
        entity.setAcceptMarketing(isAccept);
        clientRepository.save(entity);
        return this;
    }

    @Transactional
    public void deleteIdentityDocuments() {
        documentRepository.delete(
            documentRepository.findAll(Entities.identityDocument.client.id.eq(clientId))
        );
        ClientEntity clientEntity = clientRepository.getOne(clientId);
        clientEntity.setDocumentNumber(null);
        clientRepository.save(clientEntity);
    }

    public TestClient softDelete() {
        clientDeleteService.softDelete(clientId);
        return this;
    }

    public TestClient hardDelete() {
        clientDeleteService.hardDelete(clientId);
        return this;
    }

    public TestClient createIdentificationDocument() {
        Client client = getClient();
        Attachment attachment = saveAttachment(client);
        identificationDocumentId = identificationDocumentsService.saveIdentificationDocument(
            new SaveIdentificationDocumentCommand()
                .setClientId(client.getId())
                .setName(client.getFirstName())
                .setSurname1(client.getLastName())
                .setSurname2(client.getSecondLastName())
                .setDocumentType(DocumentType.PASSPORT)
                .setDocumentNumber(client.getDocumentNumber())
                .setGender(Gender.MALE.name())
                .setNationality("Spanish")
                .setFrontFileId(attachment.getFileId())
                .setFrontFileName(attachment.getName())
                .setDateOfBirth(client.getDateOfBirth() != null ? client.getDateOfBirth() : TimeMachine.today().minusYears(25))
                .setExpirationDate(TimeMachine.today().plusDays(30))
        );
        return this;
    }

    public TestClient createExpiredIdentificationDocument() {
        Client client = getClient();
        Attachment attachment = saveAttachment(client);
        identificationDocumentId = identificationDocumentsService.saveIdentificationDocument(
            new SaveIdentificationDocumentCommand()
                .setClientId(client.getId())
                .setName(client.getFirstName())
                .setSurname1(client.getLastName())
                .setSurname2(client.getSecondLastName())
                .setDocumentType(DocumentType.PASSPORT)
                .setDocumentNumber(RandomData.randomDni())
                .setGender(Gender.MALE.name())
                .setNationality("Spanish")
                .setFrontFileId(attachment.getFileId())
                .setFrontFileName(attachment.getName())
                .setDateOfBirth(client.getDateOfBirth() != null ? client.getDateOfBirth() : TimeMachine.today().minusYears(25))
                .setExpirationDate(TimeMachine.today().minusDays(30))
        );
        return this;
    }

    public TestClient createNieWithoutExpirationDate() {
        Client client = getClient();
        Attachment attachment = saveAttachment(client);
        identificationDocumentId = identificationDocumentsService.saveIdentificationDocument(
            new SaveIdentificationDocumentCommand()
                .setClientId(client.getId())
                .setName(client.getFirstName())
                .setSurname1(client.getLastName())
                .setSurname2(client.getSecondLastName())
                .setDocumentType(DocumentType.NIE)
                .setDocumentNumber(RandomData.randomDni())
                .setGender(Gender.MALE.name())
                .setNationality("Spanish")
                .setFrontFileId(attachment.getFileId())
                .setFrontFileName(attachment.getName())
                .setDateOfBirth(client.getDateOfBirth() != null ? client.getDateOfBirth() : TimeMachine.today().minusYears(25))
                .setExpirationDate(null)
        );
        return this;
    }

    public IdentificationDocumentEntity identificationDocument() {
        return identificationDocumentRepository.getRequired(identificationDocumentId);
    }

    public TestClient validateIdentificationDocument() {
        if (identificationDocumentId != null) {
            identificationDocumentsService.validateIdentificationDocument(new ValidateIdentificationDocument().setClientId(clientId).setIdentificationDocumentId(identificationDocumentId));
        } else {
            throw new IllegalStateException("No identification document id");
        }
        return this;
    }

    public TestClient invalidateIdentificationDocument() {
        if (identificationDocumentId != null) {
            identificationDocumentsService.invalidateIdentificationDocument(new InvalidateIdentificationDocument().setIdentificationDocumentId(identificationDocumentId));
        } else {
            throw new IllegalStateException("No identification document id");
        }
        return this;
    }

    public TestClient withCreditLimit(BigDecimal amount) {
        decisionEngine.setResponseForScenario("credit_limit", () ->
            new DecisionResult()
                .setDecisionEngineRequestId(RandomUtils.randomId())
                .setStatus(DecisionRequestStatus.OK)
                .setArrayResult(new ArrayList<>())
                .setRating(String.valueOf(amount))
                .setScore(null)
                .setResponse(null)
                .setVariablesResult(new HashMap<>())
                .setUsedFields(new ArrayList<>())
        );
        return this;
    }

    public Attachment saveAttachment() {
        return saveAttachment(getClient());
    }

    private Attachment saveAttachment(Client client) {
        SaveFileCommand command = new SaveFileCommand();
        command.setOriginalFileName("test-file.pdf");
        command.setDirectory("temporary");
        command.setInputStream(new ByteArrayInputStream("test".getBytes()));
        command.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);

        CloudFile cloudFile = fileStorageService.save(command);

        Long attachmentId = clientAttachmentService.addAttachment(AddAttachmentCommand.builder()
            .fileId(cloudFile.getFileId())
            .clientId(client.getId())
            .attachmentType(AlfaConstants.ATTACHMENT_TYPE_ID_DOCUMENT)
            .name("test-file.pdf")
            .build());
        return clientAttachmentService.get(attachmentId);
    }
}
