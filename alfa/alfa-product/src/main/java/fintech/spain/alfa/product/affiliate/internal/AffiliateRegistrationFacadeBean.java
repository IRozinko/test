//package fintech.spain.alfa.product.affiliate.internal;
//
//import com.google.common.collect.ImmutableList;
//import com.google.common.collect.ImmutableMap;
//import com.google.common.collect.Maps;
//import fintech.JsonUtils;
//import fintech.TimeMachine;
//import fintech.Validate;
//import fintech.affiliate.AffiliateService;
//import fintech.affiliate.model.AddLeadCommand;
//import fintech.crm.CrmConstants;
//import fintech.crm.address.ClientAddressService;
//import fintech.crm.address.SaveClientAddressCommand;
//import fintech.crm.client.Client;
//import fintech.crm.client.ClientService;
//import fintech.crm.client.CreateClientCommand;
//import fintech.crm.client.Gender;
//import fintech.crm.client.UpdateClientCommand;
//import fintech.crm.client.util.ClientNumberGenerator;
//import fintech.crm.contacts.*;
//import fintech.crm.documents.AddIdentityDocumentCommand;
//import fintech.crm.documents.IdentityDocumentService;
//import fintech.crm.logins.AddEmailLoginCommand;
//import fintech.crm.logins.EmailLoginService;
//import fintech.db.AuditInfoProvider;
//import fintech.iovation.IovationService;
//import fintech.iovation.model.SaveBlackboxCommand;
//import fintech.lending.core.application.*;
//import fintech.lending.core.loan.Loan;
//import fintech.lending.core.loan.LoanQuery;
//import fintech.lending.core.loan.LoanService;
//import fintech.spain.alfa.product.affiliate.*;
//import fintech.spain.alfa.product.lending.Inquiry;
//import fintech.spain.alfa.product.lending.UnderwritingFacade;
//import fintech.spain.alfa.product.registration.RegistrationFacade;
//import fintech.spain.alfa.product.workflow.common.Attributes;
//import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
//import fintech.spain.equifax.EquifaxService;
//import fintech.spain.equifax.model.EquifaxQuery;
//import fintech.spain.equifax.model.EquifaxRequest;
//import fintech.spain.equifax.model.EquifaxResponse;
//import fintech.spain.equifax.model.EquifaxStatus;
//import fintech.spain.experian.ExperianService;
//import fintech.spain.experian.model.CaisListOperacionesResponse;
//import fintech.spain.experian.model.CaisQuery;
//import fintech.spain.experian.model.CaisRequest;
//import fintech.spain.experian.model.CaisResumenResponse;
//import fintech.spain.experian.model.ExperianStatus;
//import fintech.spain.alfa.product.AlfaConstants;
//import fintech.spain.alfa.product.affiliate.*;
//import fintech.webanalytics.WebAnalyticsService;
//import fintech.webanalytics.model.SaveEventCommand;
//import fintech.workflow.Activity;
//import fintech.workflow.ActivityStatus;
//import fintech.workflow.Workflow;
//import fintech.workflow.WorkflowService;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.support.TransactionTemplate;
//
//import javax.annotation.Nullable;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//import static fintech.BigDecimalUtils.amount;
//import static fintech.lending.core.application.LoanApplicationStatusDetail.APPROVED;
//import static fintech.lending.core.application.LoanApplicationStatusDetail.CANCELLED;
//import static fintech.lending.core.application.LoanApplicationStatusDetail.REJECTED;
//import static fintech.spain.alfa.product.AlfaConstants.*;
//
//@Slf4j
//@Component
//class AffiliateRegistrationFacadeBean implements AffiliateRegistrationFacade {
//
//    public static final String REJECTED_BY_EXPERIAN = "Rejected by Experian";
//    public static final String REJECTED_BY_EQUIFAX = "Rejected by Equifax";
//
//    @Autowired
//    private TransactionTemplate transactionTemplate;
//
//    @Autowired
//    private ClientNumberGenerator clientNumberGenerator;
//
//    @Autowired
//    private ClientService clientService;
//
//    @Autowired
//    private ClientAddressService clientAddressService;
//
//    @Autowired
//    private IdentityDocumentService identityDocumentService;
//
//    @Autowired
//    private EmailContactService emailContactService;
//
//    @Autowired
//    private EmailLoginService emailLoginService;
//
//    @Autowired
//    private PhoneContactService phoneContactService;
//
//    @Autowired
//    private UnderwritingFacade underwritingFacade;
//
//    @Autowired
//    private AffiliateService affiliateService;
//
//    @Autowired
//    private WorkflowService workflowService;
//
//    @Autowired
//    private LoanApplicationService loanApplicationService;
//
//    @Autowired
//    private RegistrationFacade registrationFacade;
//
//    @Autowired
//    private AuditInfoProvider auditInfoProvider;
//
//    @Autowired
//    private WebAnalyticsService webAnalyticsService;
//
//    @Autowired
//    private LoanService loanService;
//
//    @Autowired
//    private ExperianService experianService;
//
//    @Autowired
//    private EquifaxService equifaxService;
//
//    @Autowired
//    private IovationService iovationService;
//
//    @Override
//    public AffiliateRegistrationResult step1V1(String affiliateName, AffiliateRegistrationStep1FormV1 formV1) {
//        AffiliateRegistrationStep1Form form = new AffiliateRegistrationStep1Form();
//        form.setAmount(formV1.getAmount());
//        form.setTerm(formV1.getTerm());
//        form.setFirstName(formV1.getFirstName());
//        form.setLastName(formV1.getLastName());
//        form.setDocumentNumber(formV1.getDocumentNumber());
//        form.setGender(formV1.getGender());
//        form.setDateOfBirth(formV1.getDateOfBirth());
//        form.setStreet(formV1.getStreet());
//        form.setPostalCode(formV1.getPostalCode());
//        form.setCity(formV1.getCity());
//        form.setMobilePhone(formV1.getMobilePhone());
//        form.setOtherPhone(formV1.getOtherPhone());
//        form.setEmail(formV1.getEmail());
//        form.setIban(formV1.getIban());
//        form.setTos(formV1.getTos());
//        form.setCirex(formV1.getCirex());
//        form.setCampaign(formV1.getCampaign());
//        form.setLead(formV1.getLead());
//        form.setLead2(formV1.getLead2());
//        form.setAcceptMarketing(formV1.isAcceptMarketing());
//        form.setMaritalStatus(formV1.getMaritalStatus());
//        form.setNumberOfDependants(formV1.getNumberOfDependants());
//        form.setEducation(formV1.getEducation());
//        form.setWorkSector(formV1.getWorkSector());
//        form.setOccupation(formV1.getOccupation());
//        form.setEmployedSince(formV1.getEmployedSince());
//        form.setEmployedSince(formV1.getEmployedSince());
//        form.setNextSalaryDate(formV1.getNextSalaryDate());
//        form.setHousingTenure(formV1.getHousingTenure());
//        form.setIncomeSource(formV1.getIncomeSource());
//        form.setExcludedFromASNEF(formV1.getExcludedFromASNEF());
//        form.setMonthlyExpenses(formV1.getMonthlyExpenses());
//        form.setNetoIncome(formV1.getNetoIncome());
//
//        AffiliateRegistrationResult result = step1Internal(affiliateName, form);
//        if (result.getWorkflowId() != null) {
//            runBeforePhoneVerification(result.getWorkflowId());
//        }
//        return result;
//    }
//
//    @Override
//    public AffiliateRegistrationResult step1(String affiliateName, AffiliateRegistrationStep1Form form) {
//        AffiliateRegistrationResult result;
//        Long existingClientId = getExistingClientId(form.getEmail(), form.getMobilePhone());
//        if (existingClientId == null) {
//            result = step1Internal(affiliateName, form);
//            if (result.getWorkflowId() != null) {
//                runBeforePhoneVerification(result.getWorkflowId());
//                AffiliateApplicationStatus currentStatus = status(result.getApplicationUuid());
//                return result.setApplicationStatus(currentStatus).setApplicationDetails(result.getApplicationDetails());
//            }
//            return result.setExistingClient(false);
//        } else {
//            log.info("Affiliate registration from existing client {}", existingClientId);
//            List<LoanApplication> openApplications = loanApplicationService.find(LoanApplicationQuery.byType(existingClientId, LoanApplicationType.NEW_LOAN, LoanApplicationStatus.OPEN));
//            if (openApplications.isEmpty()) {
//                List<Loan> openLoans = loanService.findLoans(LoanQuery.openLoans(existingClientId));
//                if (openLoans.isEmpty()) {
//                    String dni = form.getDocumentNumber();
//                    AffiliateExperianResult affiliateExperianResult = verifyExperian(existingClientId, dni);
//                    if (!affiliateExperianResult.isVerified()) {
//                        return new AffiliateRegistrationResult()
//                            .setExistingClient(true)
//                            .setApplicationStatus(AffiliateApplicationStatus.DECLINED)
//                            .setApplicationDetails(REJECTED_BY_EXPERIAN);
//                    }
//                    AffiliateEquifaxResult affiliateEquifaxResult = verifyEquifax(existingClientId, dni);
//                    if (!affiliateEquifaxResult.isVerified()) {
//                        return new AffiliateRegistrationResult()
//                            .setExistingClient(true)
//                            .setApplicationStatus(AffiliateApplicationStatus.DECLINED)
//                            .setApplicationDetails(REJECTED_BY_EQUIFAX);
//                    }
//                    AffiliateApplicationInfo affiliateApplicationInfo = new AffiliateApplicationInfo()
//                        .setAffiliateName(affiliateName)
//                        .setForm(form)
//                        .setClientId(existingClientId)
//                        .setEquifaxResponseId(affiliateEquifaxResult.getEquifaxResponseId())
//                        .setExperianCaisResumentResponseId(affiliateExperianResult.getCaisResumentResponseId())
//                        .setExperianCaisOperacionesResponseId(affiliateExperianResult.getCaisOperacionesResponseId());
//
//                    result = transactionTemplate.execute(s -> startApplication(affiliateApplicationInfo));
//                    runBeforePhoneVerification(result.getWorkflowId());
//                    AffiliateApplicationStatus currentStatus = status(result.getApplicationUuid());
//                    return result.setApplicationStatus(currentStatus).setApplicationDetails(result.getApplicationDetails()).setExistingClient(true);
//                } else {
//                    log.info("Affiliate registration from existing client {} failed because of already open loan {}", existingClientId, openLoans);
//                }
//            } else {
//                log.info("Affiliate registration from existing client {} failed because of already open application {}", existingClientId, openApplications);
//            }
//
//            return new AffiliateRegistrationResult()
//                .setClientId(existingClientId)
//                .setExistingClient(true)
//                .setApplicationStatus(AffiliateApplicationStatus.FAILED);
//        }
//    }
//
//    @Override
//    public AffiliateRegistrationResult step2(AffiliateRegistrationStep2Form form) {
//        Optional<LoanApplication> maybeApplication = loanApplicationService.findByUuid(form.getApplicationUuid());
//
//        if (!maybeApplication.isPresent()) {
//            return new AffiliateRegistrationResult().setApplicationUuid(form.getApplicationUuid()).setVerified(false);
//        }
//
//        LoanApplication application = maybeApplication.get();
//
//        Client client = clientService.get(application.getClientId());
//        if (form.getAcceptMarketing() != null) {
//            UpdateClientCommand command = UpdateClientCommand.fromClient(client);
//            command.setAcceptMarketing(form.getAcceptMarketing());
//            clientService.update(command);
//        }
//
//        AffiliateRegistrationResult result = new AffiliateRegistrationResult()
//            .setClientId(application.getClientId())
//            .setWorkflowId(application.getWorkflowId())
//            .setApplicationId(application.getId())
//            .setApplicationUuid(application.getUuid());
//
//        if (!LoanApplicationStatusDetail.isPending(application.getStatusDetail())) {
//            return result;
//        }
//
//        workflowService.suspend(application.getWorkflowId());
//        boolean verified = true;
//        return result.setVerified(verified);
//    }
//
////        try {
////            boolean verified;
////            try {
////                verified = registrationFacade.verifyPhone(application.getClientId(), form.getCode()).isVerified();
////            } catch (Exception ex) {
////                verified = false;
////            }
////
////            if (verified) {
////                workflowService.runBeforeActivity(application.getWorkflowId(), UnderwritingWorkflows.Activities.IOVATION_BLACKBOX_RUN_1);
////            }
////
////            return result.setVerified(verified);
////        } finally {
////            workflowService.resume(application.getWorkflowId());
////        }
////    }
//
//    @Override
//    public String statusV1(String applicationUuid) {
//        return loanApplicationService.findByUuid(applicationUuid)
//            .map(application -> workflowService.getWorkflow(application.getWorkflowId()))
//            .flatMap(workflow -> workflow.getActivities().stream().filter(Activity::isActive).findFirst())
//            .map(Activity::getName)
//            .orElse(null);
//    }
//
//    @Override
//    public AffiliateApplicationStatus status(String applicationUuid) {
//        Optional<LoanApplication> loanApplicationMaybe = loanApplicationService.findByUuid(applicationUuid);
//        if (!loanApplicationMaybe.isPresent()) {
//            return AffiliateApplicationStatus.NOT_FOUND;
//        }
//        LoanApplication loanApplication = loanApplicationMaybe.get();
//        if (loanApplication.getStatus() == LoanApplicationStatus.CLOSED) {
//            switch (loanApplication.getStatusDetail()) {
//                case APPROVED:
//                    return AffiliateApplicationStatus.COMPLETED;
//                case REJECTED:
//                    return AffiliateApplicationStatus.FAILED;
//                case CANCELLED:
//                    return AffiliateApplicationStatus.DECLINED;
//            }
//        }
//
//        Workflow workflow = workflowService.getWorkflow(loanApplication.getWorkflowId());
//        Optional<Activity> documentForm = workflow.findActivity(UnderwritingWorkflows.Activities.DOCUMENT_FORM);
//        ActivityStatus status = documentForm.map(Activity::getStatus).get();
//        if (status.equals(ActivityStatus.WAITING)) {
//            return AffiliateApplicationStatus.PENDING;
//        }
//        if (status.equals(ActivityStatus.ACTIVE) || status.equals(ActivityStatus.COMPLETED)) {
//            return AffiliateApplicationStatus.ACCEPTED;
//        }
//        return AffiliateApplicationStatus.DECLINED;
//    }
//
//    @Override
//    public Set<Long> getAlreadyRegisteredClientIds(List<EmailContact> emailContacts, List<PhoneContact> phoneContacts) {
//        Set<Long> emailClientIds = emailContacts
//            .stream()
//            .map(EmailContact::getClientId)
//            .collect(Collectors.toSet());
//        Set<Long> phoneClientIds = phoneContacts
//            .stream()
//            .map(PhoneContact::getClientId)
//            .collect(Collectors.toSet());
//        return emailClientIds
//            .stream()
//            .filter(phoneClientIds::contains)
//            .collect(Collectors.toSet());
//    }
//
//    @Override
//    public Long getExistingClientId(String email, String mobilePhone) {
//        List<EmailContact> emailContacts = emailContactService.findByEmail(email);
//        if (emailContacts.isEmpty()) {
//            return null;
//        }
//        List<PhoneContact> phoneContacts = phoneContactService.findByLocalPhoneNumber(mobilePhone);
//        if (phoneContacts.isEmpty()) {
//            return null;
//        }
//
//        Set<Long> commonClientIds = getAlreadyRegisteredClientIds(emailContacts, phoneContacts);
//
//        if (commonClientIds.isEmpty()) {
//            return null;
//        }
//
//        Validate.isTrue(commonClientIds.size() == 1, "More than one client found with same info of affiliate form");
//
//        return commonClientIds.stream().findFirst().get();
//    }
//
//    private AffiliateExperianResult verifyExperian(@Nullable Long clientId, String documentNumber) {
//        AffiliateExperianResult result = new AffiliateExperianResult();
//
//        CaisQuery query = new CaisQuery();
//        query.setClientId(clientId);
//        query.setStatus(ImmutableList.of(ExperianStatus.NOT_FOUND));
//        query.setCreatedAfter(TimeMachine.now().minusDays(7 * 4));
//
//        experianService.findLatestResumenResponse(query).ifPresent(response -> result.setCaisResumentResponseId(response.getId()));
//        experianService.findLatestListOperacionesResponse(query).ifPresent(response -> result.setCaisOperacionesResponseId(response.getId()));
//
//        if (result.getCaisOperacionesResponseId() != null && result.getCaisResumentResponseId() != null) {
//            result.setVerified(true);
//            return result;
//        }
//
//        CaisRequest request = new CaisRequest();
//        request.setDocumentNumber(documentNumber);
//
//        if (result.getCaisResumentResponseId() == null) {
//            CaisResumenResponse resumenResponse = experianService.requestResumen(request);
//            result.setCaisResumentResponseId(resumenResponse.getId());
//            if (resumenResponse.getStatus() != ExperianStatus.NOT_FOUND) {
//                result.setVerified(false);
//                return result;
//            }
//        }
//
//        if (result.getCaisOperacionesResponseId() == null) {
//            CaisListOperacionesResponse operacionesResponse = experianService.requestListOperaciones(request);
//            result.setCaisOperacionesResponseId(operacionesResponse.getId());
//            if (operacionesResponse.getStatus() != ExperianStatus.NOT_FOUND) {
//                result.setVerified(false);
//                return result;
//            }
//        }
//        result.setVerified(true);
//        return result;
//    }
//
//    private AffiliateEquifaxResult verifyEquifax(@Nullable Long clientId, String documentNumber) {
//        AffiliateEquifaxResult affiliateEquifaxResult = new AffiliateEquifaxResult();
//
//        EquifaxQuery query = new EquifaxQuery();
//        query.setDocumentNumber(documentNumber);
//        query.setClientId(clientId);
//        query.setStatus(ImmutableList.of(EquifaxStatus.NOT_FOUND));
//        query.setCreatedAfter(TimeMachine.now().minusDays(7 * 4));
//        equifaxService.findLatestResponse(query).ifPresent(response -> {
//            affiliateEquifaxResult.setVerified(true);
//            affiliateEquifaxResult.setEquifaxResponseId(response.getId());
//        });
//
//        if (affiliateEquifaxResult.isVerified()) {
//            return affiliateEquifaxResult;
//        }
//
//        EquifaxRequest request = new EquifaxRequest();
//        request.setDocumentNumber(documentNumber);
//        EquifaxResponse response = equifaxService.request(request);
//
//        affiliateEquifaxResult.setVerified(response.getStatus() == EquifaxStatus.NOT_FOUND);
//        affiliateEquifaxResult.setEquifaxResponseId(response.getId());
//        return affiliateEquifaxResult;
//    }
//
//    private AffiliateRegistrationResult step1Internal(String affiliateName, AffiliateRegistrationStep1Form form) {
//        String dni = form.getDocumentNumber();
//        AffiliateExperianResult affiliateExperianResult = verifyExperian(null, dni);
//        if (!affiliateExperianResult.isVerified()) {
//            return new AffiliateRegistrationResult()
//                .setApplicationStatus(AffiliateApplicationStatus.DECLINED)
//                .setApplicationDetails(REJECTED_BY_EXPERIAN);
//        }
//        AffiliateEquifaxResult affiliateEquifaxResult = verifyEquifax(null, dni);
//        if (!affiliateEquifaxResult.isVerified()) {
//            return new AffiliateRegistrationResult()
//                .setApplicationStatus(AffiliateApplicationStatus.DECLINED)
//                .setApplicationDetails(REJECTED_BY_EQUIFAX);
//        }
//        return transactionTemplate.execute(s -> {
//            Long clientId = client(form);
//            address(clientId, form);
//            identityDocument(clientId, form);
//            emailContact(clientId, form);
//            emailLogin(clientId, form);
//            primaryPhone(clientId, form);
//            if (StringUtils.isNotEmpty(form.getOtherPhone())) {
//                otherPhone(clientId, form);
//            }
//            AffiliateApplicationInfo affiliateApplicationInfo = new AffiliateApplicationInfo()
//                .setAffiliateName(affiliateName)
//                .setForm(form)
//                .setClientId(clientId)
//                .setEquifaxResponseId(affiliateEquifaxResult.getEquifaxResponseId())
//                .setExperianCaisResumentResponseId(affiliateExperianResult.getCaisResumentResponseId())
//                .setExperianCaisOperacionesResponseId(affiliateExperianResult.getCaisOperacionesResponseId());
//            AffiliateRegistrationResult result = startApplication(affiliateApplicationInfo);
//            if (form.getBlackbox() != null) {
//                String ip = auditInfoProvider.getInfo().getIpAddress();
//                SaveBlackboxCommand command = new SaveBlackboxCommand();
//                command.setBlackBox(form.getBlackbox());
//                command.setClientId(clientId);
//                command.setIpAddress(ip);
//                command.setLoanApplicationId(result.getApplicationId());
//                iovationService.saveBlackbox(command);
//            }
//            return result;
//        });
//    }
//
//    private AffiliateRegistrationResult startApplication(AffiliateApplicationInfo affiliateApplicationInfo) {
//        Long clientId = affiliateApplicationInfo.getClientId();
//        AffiliateRegistrationStep1Form form = affiliateApplicationInfo.getForm();
//        String affiliateName = affiliateApplicationInfo.getAffiliateName();
//        boolean repeatedClient = !loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.CLOSED)).isEmpty();
//        Long applicationId = underwritingFacade.submitApplication(
//            clientId,
//            new Inquiry()
//                .setPrincipal(form.getAmount())
//                .setTermInDays(form.getTerm())
//                .setInterestDiscountPercent(amount(0))
//                .setSubmittedAt(TimeMachine.now())
//                .setSourceType(LoanApplicationSourceType.AFFILIATE)
//                .setSourceName(affiliateName)
//        );
//
//        saveAffiliateData(clientId, applicationId, affiliateName, form, repeatedClient);
//        saveAnalyticsData(clientId, applicationId, affiliateName, repeatedClient);
//
//        final Map<String, String> attributes = Maps.newHashMap();
//        attributes.put(Attributes.AFFILIATE_API_LEAD, "true");
//
//        if (affiliateApplicationInfo.getExperianCaisResumentResponseId() != null) {
//            attributes.put(Attributes.EXPERIAN_CAIS_RESUMEN_RESPONSE_ID, affiliateApplicationInfo.getExperianCaisResumentResponseId().toString());
//        }
//        if (affiliateApplicationInfo.getExperianCaisOperacionesResponseId() != null) {
//            attributes.put(Attributes.EXPERIAN_CAIS_OPERACIONES_RESPONSE_ID, affiliateApplicationInfo.getExperianCaisOperacionesResponseId().toString());
//        }
//        if (affiliateApplicationInfo.getEquifaxResponseId() != null) {
//            attributes.put(Attributes.EQUIFAX_RESPONSE_ID, affiliateApplicationInfo.getEquifaxResponseId().toString());
//        }
//
//        Long workflowId = underwritingFacade.startFirstLoanAffiliatesApplicationWorkflow(applicationId, attributes);
//        workflowService.suspend(workflowId);
//
//        LoanApplication loanApplication = loanApplicationService.get(applicationId);
//        return new AffiliateRegistrationResult()
//            .setClientId(clientId)
//            .setWorkflowId(workflowId)
//            .setApplicationId(loanApplication.getId())
//            .setApplicationUuid(loanApplication.getUuid());
//    }
//
//    private void runBeforePhoneVerification(Long workflowId) {
//        try {
//            workflowService.runBeforeActivity(workflowId, UnderwritingWorkflows.Activities.PHONE_VERIFICATION);
//        } finally {
//            workflowService.resume(workflowId);
//        }
//    }
//
//    private Long client(AffiliateRegistrationStep1Form form) {
//        String clientNumber = clientNumberGenerator.newNumber(AlfaConstants.CLIENT_NUMBER_PREFIX, AlfaConstants.CLIENT_NUMBER_LENGTH);
//        Long clientId = clientService.create(new CreateClientCommand(clientNumber));
//        UpdateClientCommand command = new UpdateClientCommand();
//        command.setClientId(clientId);
//        command.setFirstName(form.getFirstName());
//        command.setLastName(form.getLastName());
//        command.setGender(Gender.valueOf(StringUtils.upperCase(form.getGender())));
//        command.setDateOfBirth(form.getDateOfBirth());
//        command.setAcceptTerms(true);
//        command.setAttributes(ImmutableMap.copyOf(attributes(form)));
//        command.setAcceptMarketing(Boolean.TRUE.equals(form.getAcceptMarketing()));
//        command.setExcludedFromASNEF(Boolean.TRUE.equals(form.getExcludedFromASNEF()));
//        clientService.update(command);
//        clientService.addToSegment(clientId, AlfaConstants.CLIENT_SEGMENT_AFFILIATE_API_LEAD);
//        return clientId;
//    }
//
//    private Map<String, String> attributes(AffiliateRegistrationStep1Form form) {
//        Map<String, String> attr = Maps.newHashMap();
//        attr.put("AffiliateForm", JsonUtils.writeValueAsString(form));
//        if (!StringUtils.isBlank(form.getMaritalStatus())) {
//            attr.put(CLIENT_ATTRIBUTE_FAMILY_STATUS, form.getMaritalStatus());
//        }
//        if (form.getNumberOfDependants() != null) {
//            attr.put(CLIENT_ATTRIBUTE_NUMBER_OF_DEPENDANTS, String.valueOf(form.getNumberOfDependants()));
//        }
//        if (!StringUtils.isBlank(form.getEducation())) {
//            attr.put(CLIENT_ATTRIBUTE_EDUCATION, form.getEducation());
//        }
//        if (form.getEmployedSince() != null) {
//            attr.put(CLIENT_ATTRIBUTE_EMPLOYED_SINCE, form.getEmployedSince().toString());
//        }
//        if (form.getNextSalaryDate() != null) {
//            attr.put(CLIENT_ATTRIBUTE_NEXT_SALARY_DATE, form.getNextSalaryDate().toString());
//        }
//        if (!StringUtils.isBlank(form.getWorkSector())) {
//            attr.put(CLIENT_ATTRIBUTE_WORK_SECTOR, form.getWorkSector());
//        }
//        if (form.getMonthlyExpenses() != null) {
//            attr.put(CLIENT_ATTRIBUTE_MONTHLY_EXPENSES, form.getMonthlyExpenses().toString());
//        }
//        if (form.getNetoIncome() != null) {
//            attr.put(CLIENT_ATTRIBUTE_NETO_INCOME, form.getNetoIncome().toString());
//        }
//        if (!StringUtils.isBlank(form.getOccupation())) {
//            attr.put(CLIENT_ATTRIBUTE_OCCUPATION, form.getOccupation());
//        }
//        if (!StringUtils.isBlank(form.getIncomeSource())) {
//            attr.put(CLIENT_ATTRIBUTE_INCOME_SOURCE, form.getIncomeSource());
//        }
//        return attr;
//    }
//
//    private void address(Long clientId, AffiliateRegistrationStep1Form form) {
//        SaveClientAddressCommand command = new SaveClientAddressCommand();
//        command.setClientId(clientId);
//        command.setType(AlfaConstants.ADDRESS_TYPE_ACTUAL);
//        command.setStreet(form.getStreet());
//        command.setCity(form.getCity());
//        command.setPostalCode(form.getPostalCode());
//        command.setHousingTenure(form.getHousingTenure());
//        clientAddressService.addAddress(command);
//    }
//
//    private void identityDocument(Long clientId, AffiliateRegistrationStep1Form form) {
//        AddIdentityDocumentCommand command = new AddIdentityDocumentCommand();
//        command.setClientId(clientId);
//        command.setType(CrmConstants.IDENTITY_DOCUMENT_DNI);
//        command.setNumber(form.getDocumentNumber());
//        identityDocumentService.makeDocumentPrimary(identityDocumentService.addDocument(command));
//    }
//
//    private void emailContact(Long clientId, AffiliateRegistrationStep1Form form) {
//        AddEmailContactCommand command = new AddEmailContactCommand();
//        command.setClientId(clientId);
//        command.setEmail(form.getEmail());
//        emailContactService.makeEmailPrimary(emailContactService.addEmailContact(command));
//    }
//
//    private void emailLogin(Long clientId, AffiliateRegistrationStep1Form form) {
//        emailLoginService.add(new AddEmailLoginCommand(clientId, form.getEmail(), RandomStringUtils.randomAlphanumeric(20), true));
//    }
//
//    private void primaryPhone(Long clientId, AffiliateRegistrationStep1Form form) {
//        AddPhoneCommand command = new AddPhoneCommand()
//            .setClientId(clientId)
//            .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
//            .setLocalNumber(PhoneNumberUtils.normalize(form.getMobilePhone()))
//            .setType(PhoneType.MOBILE)
//            .setSource(PhoneSource.REGISTRATION)
//            .setLegalConsent(true);
//        phoneContactService.makePhonePrimary(phoneContactService.addPhoneContact(command));
//    }
//
//    private void otherPhone(Long clientId, AffiliateRegistrationStep1Form form) {
//        AddPhoneCommand command = new AddPhoneCommand()
//            .setClientId(clientId)
//            .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
//            .setLocalNumber(PhoneNumberUtils.normalize(form.getOtherPhone()))
//            .setType(PhoneType.OTHER)
//            .setSource(PhoneSource.REGISTRATION)
//            .setLegalConsent(true);
//        phoneContactService.addPhoneContact(command);
//    }
//
//    private void saveAffiliateData(Long clientId, Long applicationId, String affiliateName, AffiliateRegistrationStep1Form form, boolean repeatedClient) {
//        if (StringUtils.isBlank(affiliateName)) {
//            log.warn("No affiliate name set in form, application id [{}]", applicationId);
//            return;
//        }
//
//        String affiliateLeadId = form.getLead();
//
//        if (StringUtils.isBlank(affiliateLeadId)) {
//            log.warn("No affiliate lead id set, generating one, application id [{}]", applicationId);
//            affiliateLeadId = "GENERATED-" + applicationId;
//        }
//
//        AddLeadCommand command = new AddLeadCommand();
//        command.setClientId(clientId);
//        command.setApplicationId(applicationId);
//        command.setAffiliateName(affiliateName);
//        command.setCampaign(form.getCampaign());
//        command.setAffiliateLeadId(affiliateLeadId);
//        command.setSubAffiliateLeadId1(form.getLead2());
//        command.setRepeatedClient(repeatedClient);
//        affiliateService.addLead(command);
//    }
//
//    private void saveAnalyticsData(Long clientId, Long applicationId, String affiliateName, boolean repeatedClient) {
//        SaveEventCommand command = new SaveEventCommand();
//        command.setClientId(clientId);
//        command.setApplicationId(applicationId);
//        command.setIpAddress(auditInfoProvider.getInfo().getIpAddress());
//        command.setEventType(repeatedClient ? WEB_ANALYTICS_LOAN_APPLICATION_EVENT : WEB_ANALYTICS_SIGN_UP_EVENT);
//        command.setUtmSource(affiliateName);
//        command.setUtmMedium("affiliation");
//        command.setUtmCampaign("api");
//        webAnalyticsService.saveEvent(command);
//    }
//}
