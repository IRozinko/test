//package fintech.spain.alfa.product.registration.impl;
//
//import com.google.common.base.MoreObjects;
//import com.google.common.collect.ImmutableMap;
//import fintech.TimeMachine;
//import fintech.Validate;
//import fintech.affiliate.AffiliateService;
//import fintech.affiliate.model.AddLeadCommand;
//import fintech.cms.PdfRenderer;
//import fintech.crm.CrmConstants;
//import fintech.crm.address.ClientAddressService;
//import fintech.crm.address.SaveClientAddressCommand;
//import fintech.crm.attachments.AddAttachmentCommand;
//import fintech.crm.attachments.ClientAttachmentService;
//import fintech.crm.client.Client;
//import fintech.crm.client.ClientService;
//import fintech.crm.client.CreateClientCommand;
//import fintech.crm.client.Gender;
//import fintech.crm.client.UpdateClientCommand;
//import fintech.crm.client.util.ClientNumberGenerator;
//import fintech.crm.contacts.*;
//import fintech.crm.country.Country;
//import fintech.crm.country.CountryService;
//import fintech.crm.documents.AddIdentityDocumentCommand;
//import fintech.crm.documents.IdentityDocument;
//import fintech.crm.documents.IdentityDocumentNumberUtils;
//import fintech.crm.documents.IdentityDocumentService;
//import fintech.crm.logins.AddEmailLoginCommand;
//import fintech.crm.logins.EmailLoginService;
//import fintech.db.AuditInfoProvider;
//import fintech.filestorage.CloudFile;
//import fintech.filestorage.FileStorageService;
//import fintech.filestorage.SaveFileCommand;
//import fintech.iovation.IovationService;
//import fintech.iovation.model.SaveBlackboxCommand;
//import fintech.lending.core.application.LoanApplicationQuery;
//import fintech.lending.core.application.LoanApplicationService;
//import fintech.lending.core.application.LoanApplicationSourceType;
//import fintech.lending.core.application.LoanApplicationStatus;
//import fintech.lending.core.promocode.PromoCodeOffer;
//import fintech.lending.core.promocode.PromoCodeService;
//import fintech.settings.SettingsService;
//import fintech.spain.alfa.product.lending.DiscountOffer;
//import fintech.spain.alfa.product.lending.Inquiry;
//import fintech.spain.alfa.product.lending.UnderwritingFacade;
//import fintech.spain.alfa.product.registration.RegistrationFacade;
//import fintech.spain.alfa.product.registration.SendVerificationCodeResult;
//import fintech.spain.alfa.product.registration.VerifyPhoneResult;
//import fintech.spain.alfa.product.settings.AlfaSettings;
//import fintech.spain.alfa.product.AlfaConstants;
//import fintech.spain.alfa.product.cms.CmsSetup;
//import fintech.spain.alfa.product.cms.PhoneVerificationModel;
//import fintech.spain.alfa.product.cms.AlfaCmsModels;
//import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
//import fintech.spain.alfa.product.crm.AddressCatalog;
//import fintech.spain.alfa.product.registration.events.ApplicationFormCompletedEvent;
//import fintech.spain.alfa.product.registration.forms.AddressData;
//import fintech.spain.alfa.product.registration.forms.AffiliateData;
//import fintech.spain.alfa.product.registration.forms.AnalyticsData;
//import fintech.spain.alfa.product.registration.forms.ApplicationForm;
//import fintech.spain.alfa.product.registration.forms.DocumentNumberForm;
//import fintech.spain.alfa.product.registration.forms.SignUpForm;
//import fintech.webanalytics.WebAnalyticsService;
//import fintech.webanalytics.model.SaveEventCommand;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.RandomStringUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationEventPublisher;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.ByteArrayInputStream;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static fintech.DateUtils.toSecondsBetween;
//import static fintech.TimeMachine.now;
//import static fintech.spain.alfa.product.AlfaConstants.WEB_ANALYTICS_SIGN_UP_EVENT;
//import static fintech.spain.alfa.product.cms.CmsSetup.PHONE_VERIFICATION_NOTIFICATION;
//import static fintech.spain.alfa.product.cms.AlfaCmsModels.SCOPE_PHONE_VERIFICATION;
//
//@Slf4j
//@Transactional
//@Component
//class RegistrationFacadeBean implements RegistrationFacade {
//
//    @Autowired
//    private ClientService clientService;
//
//    @Autowired
//    private EmailContactService emailContactService;
//
//    @Autowired
//    private PhoneContactService phoneContactService;
//
//    @Autowired
//    private EmailLoginService emailLoginService;
//
//    @Autowired
//    private ApplicationEventPublisher eventPublisher;
//
//    @Autowired
//    private IdentityDocumentService identityDocumentService;
//
//    @Autowired
//    private ClientAddressService clientAddressService;
//
//    @Autowired
//    private SettingsService settingsService;
//
//    @Autowired
//    private AffiliateService affiliateService;
//
//    @Autowired
//    private ClientNumberGenerator clientNumberGenerator;
//
//    @Autowired
//    private WebAnalyticsService webAnalyticsService;
//
//    @Autowired
//    private AuditInfoProvider auditInfoProvider;
//
//    @Autowired
//    private UnderwritingFacade underwritingFacade;
//
//    @Autowired
//    private AlfaNotificationBuilderFactory notificationFactory;
//
//    @Autowired
//    private AddressCatalog addressCatalog;
//
//    @Autowired
//    private AlfaCmsModels cmsModels;
//
//    @Autowired
//    private PdfRenderer pdfRenderer;
//
//    @Autowired
//    private FileStorageService fileStorageService;
//
//    @Autowired
//    private ClientAttachmentService clientAttachmentService;
//
//    @Autowired
//    private CountryService countryService;
//
//    @Autowired
//    private PromoCodeService promoCodeService;
//
//    @Autowired
//    private LoanApplicationService loanApplicationService;
//
//    @Autowired
//    private IovationService iovationService;
//
//    @Override
//    public Long signUp(SignUpForm form, boolean submitApplication) {
//        Long clientId = createClient(form);
//        Long applicationId = null;
//
//        if (submitApplication) {
//            Inquiry inquiry = buildInquiry(clientId, form.getAmount(), form.getTermInDays(), form.getPromoCode(), getAffiliateName(form));
//            applicationId = underwritingFacade.submitApplication(clientId, inquiry);
//            saveAnalyticsData(clientId, applicationId, form.getAnalytics(), WEB_ANALYTICS_SIGN_UP_EVENT);
//            savePrivacyPolicyPdf(clientId);
//            if (form.isAffiliate()) {
//                saveAffiliateData(clientId, applicationId, form.getAffiliate());
//                underwritingFacade.startFirstLoanAffiliatesApplicationWorkflow(applicationId, Collections.emptyMap());
//            } else {
//                underwritingFacade.startFirstLoanApplicationWorkflow(applicationId, Collections.emptyMap());
//            }
//        }
//
//        {
//            String ip = auditInfoProvider.getInfo().getIpAddress();
//            SaveBlackboxCommand command = new SaveBlackboxCommand();
//            command.setBlackBox(form.getBlackbox());
//            command.setClientId(clientId);
//            command.setIpAddress(ip);
//            command.setLoanApplicationId(applicationId);
//            iovationService.saveBlackbox(command);
//        }
//        return clientId;
//    }
//
//    @Override
//    public void saveDocumentNumber(Long clientId, DocumentNumberForm form) {
//        List<IdentityDocument> documents = identityDocumentService.findPrimaryDocuments(clientId);
//        Validate.isTrue(documents.isEmpty(), "Client already has identity document");
//        addIdentityDocument(clientId, form);
//    }
//
//    @Override
//    public void saveApplicationData(Long clientId, ApplicationForm form) {
//        Client client = clientService.get(clientId);
//        UpdateClientCommand updateCommand = UpdateClientCommand.fromClient(client);
//        updateCommand.setGender(Gender.valueOf(form.getGender()));
//        updateCommand.setDateOfBirth(form.getDateOfBirth());
//
//        updateCommand.getAttributes().putAll(MoreObjects.firstNonNull(form.getAttributes(), ImmutableMap.of()));
//
//        if (!StringUtils.isBlank(form.getNumberOfDependants())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_NUMBER_OF_DEPENDANTS, form.getNumberOfDependants());
//        }
//        if (!StringUtils.isBlank(form.getEmploymentStatus())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_EMPLOYMENT_STATUS, form.getEmploymentStatus());
//        }
//        if (!StringUtils.isBlank(form.getEmploymentDetail())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_EMPLOYMENT_DETAIL, form.getEmploymentDetail());
//        }
//        if (!StringUtils.isBlank(form.getMonthlyIncome())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_MONTHLY_INCOME, form.getMonthlyIncome());
//        }
//        if (!StringUtils.isBlank(form.getFamilyStatus())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_FAMILY_STATUS, form.getFamilyStatus());
//        }
//        if (!StringUtils.isBlank(form.getLoanPurpose())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_LOAN_PURPOSE, form.getLoanPurpose());
//        }
//
//        if (!StringUtils.isBlank(form.getIncomeSource())) {
//            updateCommand.getAttributes().put(AlfaConstants.CLIENT_ATTRIBUTE_INCOME_SOURCE, form.getIncomeSource());
//        }
//        clientService.update(updateCommand);
//
//        saveAddressData(clientId, form.getAddress());
//        eventPublisher.publishEvent(new ApplicationFormCompletedEvent().setClientId(clientId));
//    }
//
////    @Override
////    public SendVerificationCodeResult sendPhoneVerificationCode(Long clientId) {
////
////        AlfaSettings.PhoneVerificationSettings settings = settingsService.getJson(AlfaSettings.PHONE_VERIFICATION, AlfaSettings.PhoneVerificationSettings.class);
////
////        Optional<PhoneContact> primary = phoneContactService.findPrimaryPhone(clientId);
////        Validate.isTrue(primary.isPresent(), "Primary phone not found by client id [%s]", clientId);
////        Long phoneContactId = primary.get().getId();
////
////        Optional<SendVerificationCodeResult> result = qualifiedForNewVerificationCode(false, clientId, phoneContactId, settings.getMaxAttempts());
////        if (result.isPresent()) {
////            return result.get();
////        }
////
////        String newCode = settings.getSmsCodeLength() == 0L ? "0" : RandomStringUtils.randomNumeric(settings.getSmsCodeLength());
////        AddPhoneVerificationCommand command = new AddPhoneVerificationCommand();
////        command.setCode(newCode);
////        command.setPhoneContactId(phoneContactId);
////        phoneContactService.addPhoneVerification(command);
////
////        notificationFactory.fromCustomerService(clientId)
////            .render(PHONE_VERIFICATION_NOTIFICATION, ImmutableMap.of(SCOPE_PHONE_VERIFICATION, new PhoneVerificationModel(newCode)))
////            .send();
////
////        result = qualifiedForNewVerificationCode(true, clientId, phoneContactId, settings.getMaxAttempts());
////        return result.orElseGet(() -> SendVerificationCodeResult.builder()
////            .availableAttempts(getAvailableVerificationCodeAttempts(clientId, phoneContactId))
////            .codeSent(true)
////            .build());
////    }
//
////    @Override
////    public VerifyPhoneResult verifyPhone(Long clientId, String code) {
////        Optional<PhoneContact> primary = phoneContactService.findPrimaryPhone(clientId);
////        Validate.isTrue(primary.isPresent(), "Primary phone not found by client id [%s]", clientId);
////
////        AlfaSettings.PhoneVerificationSettings settings = settingsService.getJson(AlfaSettings.PHONE_VERIFICATION, AlfaSettings.PhoneVerificationSettings.class);
////        LocalDateTime codeCreatedAfter = now().minusMinutes(settings.getSmsCodeExpiresInMinutes());
////
////        Long phoneContactId = primary.get().getId();
////        int maxAttemptsCount = settings.getMaxAttempts().getMaxAttemptsCount();
////        if (phoneContactService.getVerificationAttempts(phoneContactId) == maxAttemptsCount) {
////            return phoneNotVerified(phoneContactId, maxAttemptsCount);
////        }
////
////        VerifyPhoneCommand command = new VerifyPhoneCommand();
////        command.setCode(code);
////        command.setPhoneContactId(phoneContactId);
////        command.setCodeCreatedAfter(codeCreatedAfter);
////        return phoneContactService.verifyPhone(command) ? phoneVerified() : phoneNotVerified(phoneContactId, maxAttemptsCount);
////    }
//
//    @Override
//    public void changePhone(Long clientId, String mobilePhone) {
//        addPrimaryPhone(clientId, mobilePhone);
////        sendPhoneVerificationCode(clientId);
//    }
//
//    private VerifyPhoneResult phoneVerified() {
//        return VerifyPhoneResult.builder()
//            .verified(true)
//            .build();
//    }
//
//    private VerifyPhoneResult phoneNotVerified(Long phoneContactId, int maxAttemptsCount) {
//        return VerifyPhoneResult.builder()
//            .verified(false)
//            .availableAttempts(maxAttemptsCount - phoneContactService.getVerificationAttempts(phoneContactId))
//            .build();
//    }
//
//    private void savePrivacyPolicyPdf(Long clientId) {
//        pdfRenderer.render(CmsSetup.PRIVACY_POLICY_PDF, ImmutableMap.of(AlfaCmsModels.SCOPE_COMPANY, cmsModels.company(), AlfaCmsModels.SCOPE_CLIENT, cmsModels.client(clientId)), AlfaConstants.LOCALE).ifPresent(pdf -> {
//                SaveFileCommand saveFileCommand = new SaveFileCommand();
//                saveFileCommand.setContentType(SaveFileCommand.CONTENT_TYPE_PDF);
//                saveFileCommand.setDirectory(AlfaConstants.FILE_DIRECTORY_AGREEMENTS);
//                saveFileCommand.setInputStream(new ByteArrayInputStream(pdf.getContent()));
//                saveFileCommand.setOriginalFileName(String.format("privacy_policy_%s.pdf", clientService.get(clientId).getNumber()));
//
//                CloudFile cloudFile = fileStorageService.save(saveFileCommand);
//
//                AddAttachmentCommand addAttachmentCommand = new AddAttachmentCommand();
//                addAttachmentCommand.setClientId(clientId);
//                addAttachmentCommand.setFileId(cloudFile.getFileId());
//                addAttachmentCommand.setAttachmentType(AlfaConstants.ATTACHMENT_TYPE_PRIVACY_POLICY);
//                addAttachmentCommand.setName(cloudFile.getOriginalFileName());
//
//                clientAttachmentService.addAttachment(addAttachmentCommand);
//            }
//        );
//    }
//
//    public void saveAnalyticsData(Long clientId, Long applicationId, AnalyticsData data, String eventType) {
//        SaveEventCommand command = new SaveEventCommand();
//        command.setClientId(clientId);
//        command.setApplicationId(applicationId);
//        command.setIpAddress(auditInfoProvider.getInfo().getIpAddress());
//        command.setEventType(eventType);
//        if (data == null) {
//            command.setUtmSource(AlfaConstants.WEB_ANALYTICS_ORGANIC_SOURCE);
//        } else {
//            String utmSource = data.getUtmSource();
//            if (StringUtils.isBlank(utmSource)) {
//                utmSource = AlfaConstants.WEB_ANALYTICS_ORGANIC_SOURCE;
//            }
//            command.setUtmSource(utmSource);
//            command.setUtmMedium(data.getUtmMedium());
//            command.setUtmCampaign(data.getUtmCampaign());
//            command.setUtmTerm(data.getUtmTerm());
//            command.setUtmContent(data.getUtmContent());
//            command.setGclid(data.getGclid());
//        }
//        webAnalyticsService.saveEvent(command);
//    }
//
//    public void saveAffiliateData(Long clientId, Long applicationId, AffiliateData affiliate) {
//        if (affiliate == null ||
//            StringUtils.isBlank(affiliate.getAffiliateName()) ||
//            StringUtils.isBlank(affiliate.getAffiliateLeadId())) {
//            return;
//        }
//        boolean repeatedClient = !loanApplicationService.find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.CLOSED)).isEmpty();
//        AddLeadCommand command = new AddLeadCommand();
//        command.setClientId(clientId);
//        command.setApplicationId(applicationId);
//        command.setAffiliateName(affiliate.getAffiliateName());
//        command.setCampaign(affiliate.getCampaign());
//        command.setAffiliateLeadId(affiliate.getAffiliateLeadId());
//        command.setSubAffiliateLeadId1(affiliate.getSubAffiliateLeadId1());
//        command.setRepeatedClient(repeatedClient);
//        command.setSubAffiliateLeadId2(affiliate.getSubAffiliateLeadId2());
//        command.setSubAffiliateLeadId3(affiliate.getSubAffiliateLeadId3());
//        affiliateService.addLead(command);
//    }
//
//    private void saveAddressData(Long clientId, @NonNull AddressData form) {
//        String city = StringUtils.trim(form.getCity());
//        String postalCode = StringUtils.trim(form.getPostalCode());
//        String province = addressCatalog.findProvince(postalCode, city).orElse("");
//
//        SaveClientAddressCommand command = new SaveClientAddressCommand();
//        command.setType(AlfaConstants.ADDRESS_TYPE_ACTUAL);
//        command.setClientId(clientId);
//        command.setStreet(form.getStreet());
//        command.setHouseNumber(form.getHouseNumber());
//        command.setProvince(province);
//        command.setCity(city);
//        command.setPostalCode(postalCode);
//        command.setHousingTenure(form.getHousingTenure());
//        clientAddressService.addAddress(command);
//    }
//
//    private Long createClient(SignUpForm form) {
//        Validate.isTrue(form.isAcceptTerms(), "Terms must be accepted: [%s]", form);
//        Validate.isTrue(form.isAcceptVerification(), "Verification must be accepted: [%s]", form);
//
//        String clientNumber = clientNumberGenerator.newNumber(AlfaConstants.CLIENT_NUMBER_PREFIX, AlfaConstants.CLIENT_NUMBER_LENGTH);
//        Long clientId = clientService.create(new CreateClientCommand(clientNumber));
//
//        Client client = clientService.get(clientId);
//        UpdateClientCommand updateCommand = UpdateClientCommand.fromClient(client);
//        updateCommand.setFirstName(form.getFirstName());
//        updateCommand.setLastName(form.getLastName());
//        updateCommand.setSecondLastName(form.getSecondLastName());
//        updateCommand.setAcceptVerification(form.isAcceptVerification());
//        updateCommand.setAcceptMarketing(form.isAcceptMarketing());
//        updateCommand.setAcceptTerms(form.isAcceptTerms());
//        updateCommand.getAttributes().putAll(MoreObjects.firstNonNull(form.getAttributes(), ImmutableMap.of()));
//        clientService.update(updateCommand);
//
//        addIdentityDocument(clientId, new DocumentNumberForm(form.getDocumentNumber(), form.getCountryCodeOfNationality()));
//        addEmailContact(clientId, form);
//        addEmailLogin(clientId, form);
//        addPrimaryPhone(clientId, form.getMobilePhone());
//        if (!StringUtils.isBlank(form.getOtherPhone())) {
//            addOtherPhone(clientId, form.getOtherPhone());
//        }
//        return clientId;
//    }
//
//    private void addOtherPhone(Long clientId, String otherPhone) {
//        AddPhoneCommand command = new AddPhoneCommand()
//            .setClientId(clientId)
//            .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
//            .setLocalNumber(PhoneNumberUtils.normalize(otherPhone))
//            .setType(PhoneType.OTHER)
//            .setSource(PhoneSource.REGISTRATION)
//            .setLegalConsent(true);
//        phoneContactService.addPhoneContact(command);
//    }
//
//    private void addIdentityDocument(Long clientId, DocumentNumberForm form) {
//        Validate.isTrue(IdentityDocumentNumberUtils.isValidDniOrNie(form.getDocumentNumber()), "The document number " + form.getDocumentNumber() + " is not valid");
//
//        Country country = countryService.getCountry(form.getCountryCodeOfNationality());
//
//        Validate.isTrue(IdentityDocumentNumberUtils.isValidDni(form.getDocumentNumber()) == country.isHomeCountry(), "The document number is DNI but the nationality is not Spain");
//        Validate.isTrue(IdentityDocumentNumberUtils.isValidNie(form.getDocumentNumber()) == !country.isHomeCountry(), "The document number is NIE but the nationality is Spain");
//
//        AddIdentityDocumentCommand command = new AddIdentityDocumentCommand();
//        command.setClientId(clientId);
//        command.setNumber(form.getDocumentNumber());
//        command.setType(CrmConstants.IDENTITY_DOCUMENT_DNI);
//        command.setCountryCodeOfNationality(form.getCountryCodeOfNationality());
//        Long docId = identityDocumentService.addDocument(command);
//        identityDocumentService.makeDocumentPrimary(docId);
//    }
//
//    private void addEmailContact(Long clientId, SignUpForm form) {
//        AddEmailContactCommand command = new AddEmailContactCommand();
//        command.setClientId(clientId);
//        command.setEmail(form.getEmail());
//        Long emailContactId = emailContactService.addEmailContact(command);
//        emailContactService.makeEmailPrimary(emailContactId);
//    }
//
//    private void addEmailLogin(Long clientId, SignUpForm form) {
//        emailLoginService.add(new AddEmailLoginCommand(clientId, form.getEmail(), form.getPassword(), false));
//    }
//
//    private void addPrimaryPhone(Long clientId, String mobilePhone) {
//        AddPhoneCommand command = new AddPhoneCommand()
//            .setClientId(clientId)
//            .setCountryCode(AlfaConstants.PHONE_COUNTRY_CODE)
//            .setLocalNumber(PhoneNumberUtils.normalize(mobilePhone))
//            .setType(PhoneType.MOBILE)
//            .setSource(PhoneSource.REGISTRATION)
//            .setLegalConsent(true);
//        Long phoneContactId = phoneContactService.addPhoneContact(command);
//        phoneContactService.makePhonePrimary(phoneContactId);
//    }
//
//    @Override
//    public Inquiry buildInquiry(Long clientId, BigDecimal amount, Long termInDays, String promoCode, String affiliateName) {
//        Inquiry inquiry = new Inquiry()
//            .setPrincipal(amount)
//            .setTermInDays(termInDays)
//            .setSubmittedAt(TimeMachine.now());
//        if (affiliateName != null) {
//            inquiry.setSourceName(affiliateName);
//            inquiry.setSourceType(LoanApplicationSourceType.AFFILIATE);
//        }
//        Optional<PromoCodeOffer> maybePromoCodeOffer = promoCodeService.getPromoCodeOffer(promoCode, clientId, affiliateName);
//        if (maybePromoCodeOffer.isPresent()) {
//            PromoCodeOffer promoCodeOffer = maybePromoCodeOffer.get();
//            inquiry.setInterestDiscountPercent(promoCodeOffer.getDiscountInPercent());
//            inquiry.setPromoCodeId(promoCodeOffer.getPromoCodeId());
//        } else {
//            DiscountOffer discountOffer = underwritingFacade.getDiscountOffer(clientId, TimeMachine.today());
//            inquiry.setInterestDiscountPercent(discountOffer.getRateInPercent());
//            inquiry.setDiscountId(discountOffer.getDiscountId());
//        }
//        return inquiry;
//    }
//
//    private String getAffiliateName(SignUpForm form) {
//        return form.isAffiliate() ? form.getAffiliate().getAffiliateName() : null;
//    }
//
////    private Optional<SendVerificationCodeResult> qualifiedForNewVerificationCode(boolean codeSent, Long clientId, Long phoneContactId, AlfaSettings.PhoneVerificationSettings.MaxAttempts maxAttempts) {
////        return phoneContactService.findLatestVerificationCode(phoneContactId).map(code -> {
////            LocalDateTime createdAt = code.getCreatedAt();
////            int quarantine = maxAttempts.getMaxAttemptsCountExpiresInMinutes();
////            boolean quarantinePassed = createdAt.isAfter(now().minusMinutes(quarantine));
////            return verificationCodeAttemptsReached(clientId, phoneContactId) && quarantinePassed ?
////                SendVerificationCodeResult.builder()
////                    .nextAttemptInSeconds(toSecondsBetween(now(), createdAt.plusMinutes(quarantine)))
////                    .codeSent(codeSent)
////                    .build()
////                : null;
////        });
////    }
//
////    private boolean verificationCodeAttemptsReached(Long clientId, Long phoneContactId) {
////        AlfaSettings.PhoneVerificationSettings.MaxAttempts maxAttempts = settingsService.getJson(AlfaSettings.PHONE_VERIFICATION, AlfaSettings.PhoneVerificationSettings.class).getMaxAttempts();
////        return phoneContactService.countSentVerificationCodes(clientId, phoneContactId) % maxAttempts.getMaxAttemptsCount() == 0;
////    }
//
////    private int getAvailableVerificationCodeAttempts(Long clientId, Long phoneContactId) {
////        int sentVerificationCodes = phoneContactService.countSentVerificationCodes(clientId, phoneContactId);
////        AlfaSettings.PhoneVerificationSettings.MaxAttempts maxAttempts = settingsService.getJson(AlfaSettings.PHONE_VERIFICATION, AlfaSettings.PhoneVerificationSettings.class).getMaxAttempts();
////        return maxAttempts.getMaxAttemptsCount() - (sentVerificationCodes % maxAttempts.getMaxAttemptsCount());
////    }
//}
