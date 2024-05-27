package fintech.spain.alfa.product.lending.impl;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import fintech.BigDecimalUtils;
import fintech.DateUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.cms.Pdf;
import fintech.cms.PdfRenderer;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import fintech.db.AuditInfoProvider;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.geoip.GeoIpService;
import fintech.instantor.InstantorService;
import fintech.instantor.model.InstantorResponseQuery;
import fintech.instantor.model.InstantorResponseStatus;
import fintech.lending.core.PeriodUnit;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.application.LoanApplicationType;
import fintech.lending.core.application.commands.ApproveOfferCommand;
import fintech.lending.core.application.commands.AttachWorkflowCommand;
import fintech.lending.core.application.commands.LoanApplicationOfferCommand;
import fintech.lending.core.application.commands.SubmitLoanApplicationCommand;
import fintech.lending.core.application.events.LoanApplicationRetriedEvent;
import fintech.lending.core.application.impl.LoanApplicationNumberProvider;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.lending.core.discount.DiscountService;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.IssueLoanCommand;
import fintech.lending.core.product.ProductService;
import fintech.lending.core.promocode.PromoCodeOffer;
import fintech.lending.core.promocode.PromoCodeService;
import fintech.lending.payday.settings.PaydayOfferSettings;
import fintech.lending.payday.settings.PaydayProductSettings;
import fintech.nordigen.NordigenService;
import fintech.nordigen.model.NordigenQuery;
import fintech.payments.DisbursementService;
import fintech.payments.commands.AddDisbursementCommand;
import fintech.payments.model.Institution;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.filestorage.FileStorageCommandFactory;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.equifax.EquifaxService;
import fintech.spain.equifax.model.EquifaxQuery;
import fintech.spain.equifax.model.EquifaxStatus;
import fintech.spain.experian.ExperianService;
import fintech.spain.experian.model.CaisQuery;
import fintech.spain.experian.model.ExperianStatus;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.cms.CmsSetup;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.AlfaNotificationBuilderFactory;
import fintech.spain.alfa.product.lending.DiscountOffer;
import fintech.spain.alfa.product.lending.Inquiry;
import fintech.spain.alfa.product.lending.LoanIssueResult;
import fintech.spain.alfa.product.lending.Offer;
import fintech.spain.alfa.product.lending.OfferSettings;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.lending.events.OfferApprovedEvent;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.product.workflow.undewrtiting.UnderwritingWorkflows;
import fintech.spain.alfa.product.workflow.upsell.UpsellWorkflow;
import fintech.spain.alfa.strategy.StrategyType;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.spi.InterestStrategy;
import fintech.workflow.StartWorkflowCommand;
import fintech.workflow.Workflow;
import fintech.workflow.WorkflowService;
import fintech.workflow.WorkflowStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.min;
import static fintech.BigDecimalUtils.roundDecimals;
import static fintech.DateUtils.date;

@Slf4j
@Component
@Transactional
public class UnderwritingFacadeBean implements UnderwritingFacade {

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private InstitutionFinder institutionFinder;

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private WorkflowService workflowService;

    @Autowired
    private AuditInfoProvider auditInfoProvider;

    @Autowired
    private GeoIpService geoIpService;

    @Autowired
    private LoanApplicationNumberProvider loanApplicationNumberProvider;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private AlfaNotificationBuilderFactory notificationFactory;

    @Autowired
    private AlfaCmsModels cmsModels;

    @Autowired
    private ClientAttachmentService attachmentService;

    @Autowired
    private PdfRenderer pdfRenderer;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EquifaxService equifaxService;

    @Autowired
    private ExperianService experianService;

    @Autowired
    private InstantorService instantorService;

    @Autowired
    private NordigenService nordigenService;

    @Autowired
    private DiscountService discountService;

    @Autowired
    private PromoCodeService promoCodeService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private CreditLimitService creditLimitService;

    @Autowired
    private CalculationStrategyService strategyService;

    @Override
    public OfferSettings publicOfferSettings() {
        PaydayProductSettings settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class);
        PaydayOfferSettings publicOfferSettings = settings.getPublicOfferSettings();

        OfferSettings offerSettings = new OfferSettings();
        offerSettings.setInterestDiscountPercent(BigDecimal.ZERO);
        offerSettings.setMinAmount(publicOfferSettings.getMinAmount());
        offerSettings.setMaxAmount(publicOfferSettings.getMaxAmount());
        offerSettings.setAmountStep(publicOfferSettings.getAmountStep());
        offerSettings.setDefaultAmount(min(publicOfferSettings.getDefaultAmount(), publicOfferSettings.getMaxAmount()));
        offerSettings.setMinTerm(publicOfferSettings.getMinTerm());
        offerSettings.setMaxTerm(publicOfferSettings.getMaxTerm());
        offerSettings.setTermStep(publicOfferSettings.getTermStep());
        offerSettings.setDefaultTerm(publicOfferSettings.getDefaultTerm());
        return offerSettings;
    }

    @Override
    public OfferSettings clientOfferSettings(Long clientId, LocalDate when) {
        PaydayProductSettings settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class);
        PaydayOfferSettings clientOfferSettings = settings.getClientOfferSettings();

        BigDecimal discount = getDiscountInPercent(clientId);
        BigDecimal creditLimit = roundDecimals(creditLimitService.getClientLimit(clientId, when)
            .orElse(CreditLimit.zeroLimit(clientId)).getLimit());
        BigDecimal maxAmount = clientOfferSettings.getMaxAmount();
        if (clientOfferSettings.isUseCreditLimitAsMaxAmount() && !BigDecimalUtils.eq(creditLimit, BigDecimal.ZERO)) {
            maxAmount = creditLimit;
        }

        BigDecimal defaultAmount = clientOfferSettings.isSetSliderToMaxAmount() ? maxAmount : min(clientOfferSettings.getDefaultAmount(), maxAmount); //do not exceed max amount
        OfferSettings offerSettings = new OfferSettings();
        offerSettings.setInterestDiscountPercent(discount);
        offerSettings.setMinAmount(clientOfferSettings.getMinAmount());
        offerSettings.setMaxAmount(maxAmount);
        offerSettings.setAmountStep(clientOfferSettings.getAmountStep());
        offerSettings.setDefaultAmount(defaultAmount);
        offerSettings.setMinTerm(clientOfferSettings.getMinTerm());
        offerSettings.setMaxTerm(clientOfferSettings.getMaxTerm());
        offerSettings.setTermStep(clientOfferSettings.getTermStep());
        offerSettings.setDefaultTerm(clientOfferSettings.getDefaultTerm());
        return offerSettings;
    }

    @Override
    public DiscountOffer getDiscountOffer(Long clientId, LocalDate today) {
        BigDecimal discountRateInPercent = getDiscountInPercent(clientId);

        return discountService.findDiscount(clientId, TimeMachine.today())
            .filter(discount -> BigDecimalUtils.goe(discount.getRateInPercent(), discountRateInPercent))
            .map(discount -> new DiscountOffer().setDiscountId(discount.getId()).setRateInPercent(discount.getRateInPercent()))
            .orElseGet(() -> new DiscountOffer().setRateInPercent(discountRateInPercent));
    }

    private BigDecimal getDiscountInPercent(Long clientId) {
        List<Loan> paidLoans = loanService.findLoans(LoanQuery.paidLoans(clientId));

        AlfaSettings.DiscountSettings discountSettings = settingsService.getJson(AlfaSettings.DISCOUNT_SETTINGS, AlfaSettings.DiscountSettings.class);
        Long dpdThreshold = discountSettings.getDpdThreshold();

        LocalDate lastOverdueLoanIssueDate = paidLoans.stream().filter(l -> l.getOverdueDays() > dpdThreshold).map(Loan::getIssueDate).max(LocalDate::compareTo).orElse(date("1900-01-01"));
        List<Loan> recentPaidLoansOnTimeSequence = paidLoans.stream().filter(l -> DateUtils.gt(l.getIssueDate(), lastOverdueLoanIssueDate)).collect(Collectors.toList());
        BigDecimal totalRepaidPrincipal = recentPaidLoansOnTimeSequence.stream().map(Loan::getPrincipalPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        return discountSettings.findDiscount(totalRepaidPrincipal);
    }

    @Override
    public Offer makeOffer(Inquiry inquiry) {
        BigDecimal principal = inquiry.getPrincipal();
        Validate.isNotZero(amount(principal), "Inquiry principal should be greater than 0, value: [%s]", principal);
        Validate.isNotZero(amount(inquiry.getTermInDays()), "Inquiry termInDays should be greater than 0, value: [%s]", inquiry.getTermInDays());
        BigDecimal fullInterest = calculateInterest(inquiry);
        BigDecimal interestDiscount = calculateInterestDiscount(fullInterest, inquiry.getInterestDiscountPercent());
        BigDecimal interest = fullInterest.subtract(interestDiscount).setScale(0, BigDecimal.ROUND_DOWN).setScale(2, RoundingMode.UNNECESSARY);
        BigDecimal monthlyInterestRate = calculateMonthlyInterestRate(interest, inquiry.getPrincipal(), inquiry.getTermInDays());
        BigDecimal apr = AprCalculator.calculate(principal, interest, inquiry.getTermInDays())
            .setScale(0, BigDecimal.ROUND_HALF_UP);

        LocalDate maturityDate = inquiry.getSubmittedAt().toLocalDate().plusDays(inquiry.getTermInDays());

        return new Offer()
            .setPrincipal(principal)
            .setInterest(interest)
            .setInterestDiscountRatePercent(inquiry.getInterestDiscountPercent())
            .setInterestDiscountAmount(interestDiscount)
            .setTotal(principal.add(interest))
            .setMonthlyInterestRatePercent(monthlyInterestRate)
            .setTermInDays(inquiry.getTermInDays())
            .setOfferDate(inquiry.getSubmittedAt().toLocalDate())
            .setMaturityDate(maturityDate)
            .setNominalApr(monthlyInterestRate.multiply(amount(12)))
            .setAprPercent(apr)
            .setDiscountId(inquiry.getDiscountId())
            .setPromoCodeId(inquiry.getPromoCodeId());
    }

    @Override
    public Long submitApplication(Long clientId, Inquiry inquiry) {
        Client client = clientService.get(clientId);
        cancelPendingApplications(client.getId(), AlfaConstants.REJECT_REASON_SUBMITTED_NEW_APPLICATION);

        AuditInfoProvider.AuditInfo auditInfo = auditInfoProvider.getInfo();
        String referer = auditInfo.getReferer();
        String ipAddress = auditInfo.getIpAddress();
        String ipCountry = geoIpService.getCountryCode(ipAddress).orElse(AlfaConstants.UNKNOWN_IP_COUNTRY);

        String longApproveCode = RandomStringUtils.randomAlphanumeric(12);
        String shortApproveCode = StringUtils.upperCase(AlfaConstants.SMS_APPROVE_CODE);

        long loansPaid = loanService.findLoans(LoanQuery.paidLoans(clientId)).size();

        SubmitLoanApplicationCommand command = new SubmitLoanApplicationCommand();
        command.setType(LoanApplicationType.NEW_LOAN);
        command.setApplicationNumber(generateLoanNumber(client.getNumber()));
        command.setPrincipal(inquiry.getPrincipal());
        command.setPeriodCount(inquiry.getTermInDays());
        command.setPeriodUnit(PeriodUnit.DAY);
        command.setSubmittedAt(inquiry.getSubmittedAt());
        command.setClientId(client.getId());
        command.setInvoiceDay(0);
        command.setProductId(AlfaConstants.PRODUCT_ID);
        command.setIpAddress(ipAddress);
        command.setIpCountry(ipCountry);
        command.setReferer(referer);
        command.setLongApproveCode(longApproveCode);
        command.setShortApproveCode(shortApproveCode);
        command.setInterestDiscountPercent(inquiry.getInterestDiscountPercent());
        command.setSourceType(inquiry.getSourceType());
        command.setSourceName(inquiry.getSourceName());
        command.setDiscountId(inquiry.getDiscountId());
        command.setPromoCodeId(inquiry.getPromoCodeId());
        command.setLoansPaid(loansPaid);

        command.setInterestStrategyId(strategyService.getDefaultStrategyId(StrategyType.INTEREST.getType()).orElse(null));
        command.setPenaltyStrategyId(strategyService.getDefaultStrategyId(StrategyType.PENALTY.getType()).orElse(null));
        command.setFeeStrategyId(strategyService.getDefaultStrategyId(StrategyType.FEE.getType()).orElse(null));
        command.setExtensionStrategyId(strategyService.getDefaultStrategyId(StrategyType.EXTENSION.getType()).orElse(null));

        return loanApplicationService.submit(command);
    }

    @Override
    public Long startFirstLoanAffiliatesApplicationWorkflow(Long applicationId, Map<String, String> attributes) {
        return startApplicationWorkflow(applicationId, UnderwritingWorkflows.FIRST_LOAN_AFFILIATE, attributes);
    }

    @Override
    public Long startFirstLoanApplicationWorkflow(Long applicationId, Map<String, String> attributes) {
        return startApplicationWorkflow(applicationId, UnderwritingWorkflows.FIRST_LOAN, attributes);
    }

    @Override
    public Long startUpsellWorkflow(Long clientId, Long loanId) {
        Client client = clientService.get(clientId);
        Loan loan = loanService.getLoan(loanId);
        LoanApplication loanApplication = loanApplicationService.get(loan.getApplicationId());

        cancelPendingApplications(client.getId(), AlfaConstants.REJECT_REASON_SUBMITTED_NEW_APPLICATION);

        String ipAddress = auditInfoProvider.getInfo().getIpAddress();
        String ipCountry = geoIpService.getCountryCode(ipAddress).orElse(AlfaConstants.UNKNOWN_IP_COUNTRY);

        String shortApproveCode = StringUtils.upperCase(AlfaConstants.SMS_APPROVE_CODE);
        String longApproveCode = RandomStringUtils.randomAlphanumeric(12);

        long loansPaid = loanService.findLoans(LoanQuery.paidLoans(clientId)).size();

        SubmitLoanApplicationCommand submitLoanApplication = new SubmitLoanApplicationCommand();
        submitLoanApplication.setType(LoanApplicationType.UPSELL);
        submitLoanApplication.setClientId(client.getId());
        submitLoanApplication.setApplicationNumber(generateLoanNumber(client.getNumber()));
        submitLoanApplication.setIpAddress(ipAddress);
        submitLoanApplication.setIpCountry(ipCountry);
        submitLoanApplication.setProductId(AlfaConstants.PRODUCT_ID);
        submitLoanApplication.setPrincipal(loan.getCreditLimit().subtract(loan.getPrincipalDisbursed()));
        submitLoanApplication.setPeriodCount(ChronoUnit.DAYS.between(TimeMachine.today(), loan.getMaturityDate()));
        submitLoanApplication.setPeriodUnit(PeriodUnit.DAY);
        submitLoanApplication.setSubmittedAt(TimeMachine.now());
        submitLoanApplication.setInvoiceDay(loan.getInvoicePaymentDay());
        submitLoanApplication.setLoanId(loanId);
        submitLoanApplication.setShortApproveCode(shortApproveCode);
        submitLoanApplication.setLongApproveCode(longApproveCode);
        submitLoanApplication.setSourceName(loanApplication.getSourceName());
        submitLoanApplication.setSourceType(loanApplication.getSourceType());
        submitLoanApplication.setInterestDiscountPercent(loanApplication.getOfferedInterestDiscountPercent());
        submitLoanApplication.setDiscountId(loanApplication.getDiscountId());
        submitLoanApplication.setPromoCodeId(loanApplication.getPromoCodeId());
        submitLoanApplication.setLoansPaid(loansPaid);
        Long applicationId = loanApplicationService.submit(submitLoanApplication);

        StartWorkflowCommand startWorkflow = new StartWorkflowCommand();
        startWorkflow.setWorkflowName(UpsellWorkflow.WORKFLOW);
        startWorkflow.setClientId(client.getId());
        startWorkflow.setApplicationId(applicationId);
        startWorkflow.setLoanId(loanId);

        Long workflowId = workflowService.startWorkflow(startWorkflow);

        AttachWorkflowCommand attachWorkflow = new AttachWorkflowCommand();
        attachWorkflow.setApplicationId(applicationId);
        attachWorkflow.setWorkflowId(workflowId);

        loanApplicationService.attachWorkflow(attachWorkflow);

        return workflowId;
    }

//    private Map<String, String> resolveAttributes(LoanApplication application) {
//        Map<String, String> attributes = new HashMap<>();
//        Client client = clientService.get(application.getClientId());
//        {
//            EquifaxQuery query = new EquifaxQuery();
//            query.setClientId(client.getId());
//            query.setDocumentNumber(client.getDocumentNumber());
//            query.setStatus(ImmutableList.of(EquifaxStatus.FOUND, EquifaxStatus.NOT_FOUND));
//            query.setCreatedAfter(TimeMachine.now().minusDays(7 * 4));
//            equifaxService.findLatestResponse(query).ifPresent(response -> attributes.put(Attributes.EQUIFAX_RESPONSE_ID, response.getId().toString()));
//        }
//        {
//            CaisQuery query = new CaisQuery();
//            query.setClientId(client.getId());
//            query.setDocumentNumber(client.getDocumentNumber());
//            query.setStatus(ImmutableList.of(ExperianStatus.FOUND, ExperianStatus.NOT_FOUND));
//            query.setCreatedAfter(TimeMachine.now().minusDays(7 * 4));
//            experianService.findLatestResumenResponse(query).ifPresent(response -> attributes.put(Attributes.EXPERIAN_CAIS_RESUMEN_RESPONSE_ID, response.getId().toString()));
//            experianService.findLatestListOperacionesResponse(query).ifPresent(response -> attributes.put(Attributes.EXPERIAN_CAIS_OPERACIONES_RESPONSE_ID, response.getId().toString()));
//        }
//        {
//            boolean latestLoanApplicationWasRejected = loanApplicationService.findLatest(LoanApplicationQuery.byClientId(client.getId(), LoanApplicationStatus.CLOSED))
//                .filter(a -> LoanApplicationStatusDetail.REJECTED.equals(a.getStatusDetail()))
//                .isPresent();
//
//            if (!latestLoanApplicationWasRejected) {
//                InstantorResponseQuery query = new InstantorResponseQuery()
//                    .setClientId(client.getId())
//                    .setAccountNumber(client.getAccountNumber())
//                    .setResponseStatus(InstantorResponseStatus.OK)
//                    .setCreatedAfter(TimeMachine.now().minusDays(45));
//
//                instantorService.findLatest(query).ifPresent(instantor -> {
//                    attributes.put(Attributes.INSTANTOR_RESPONSE_ID, instantor.getId().toString());
//                    nordigenService.findLatest(NordigenQuery.byInstantorResponseIdOk(instantor.getId())).ifPresent(nordigen -> {
//                        attributes.put(Attributes.NORDIGEN_RESPONSE_ID, nordigen.getId().toString());
//                        Long wealthinessId = wealthinessService.create(nordigen.getId());
//                        attributes.put(Attributes.WEALTHINESS_ID, wealthinessId.toString());
//                    });
//                });
//            }
//        }
//        {
//            int paidLoanCount = loanService.findLoans(LoanQuery.paidLoans(client.getId())).size();
//            if (paidLoanCount > 0) {
//                attributes.put(Attributes.REPEATED_LOAN, String.valueOf(true));
//            }
//        }
//        return attributes;
//    }

    private String generateLoanNumber(String clientNumber) {
        return loanApplicationNumberProvider.newNumber(clientNumber, "-", 3);
    }

    private void cancelPendingApplications(Long clientId, String reason) {
        List<LoanApplication> pendingApplications = loanApplicationService
            .find(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN));
        for (LoanApplication pending : pendingApplications) {
            loanApplicationService.cancel(pending.getId(), reason);
            if (pending.getWorkflowId() != null) {
                workflowService.terminateWorkflow(pending.getWorkflowId(), reason);
            }
        }
    }

    @Override
    public void prepareOffer(Long applicationId, LocalDate offerDate) {
        LoanApplication application = loanApplicationService.get(applicationId);
        prepareOffer(application, offerDate, application.getRequestedPrincipal());
    }

    @Override
    public void prepareOffer(Long applicationId, LocalDate offerDate, BigDecimal principalRequested) {
        LoanApplication application = loanApplicationService.get(applicationId);
        prepareOffer(application, offerDate, principalRequested);
    }

    private void prepareOffer(LoanApplication application, LocalDate offerDate, BigDecimal principalRequested) {
        Validate.notNull(application.getCreditLimit(), "Credit limit not set for [%s]", application);
        BigDecimal offeredPrincipal = min(application.getCreditLimit(), principalRequested);
        Validate.isPositive(offeredPrincipal, "Offered principal not positive: [%s], application id: [%s]", offeredPrincipal, application.getId());

        Inquiry inquiry = new Inquiry()
            .setInterestStrategyId(application.getInterestStrategyId())
            .setPrincipal(offeredPrincipal)
            .setTermInDays(application.getRequestedPeriodCount())
            .setSubmittedAt(offerDate.atStartOfDay())
            .setPromoCodeId(application.getPromoCodeId())
            .setApplicationId(application.getId());

        if (application.getPromoCodeId() != null) {
            PromoCodeOffer promoCodeOffer = promoCodeService.getRequired(application.getPromoCodeId());
            inquiry.setInterestDiscountPercent(promoCodeOffer.getDiscountInPercent());
            inquiry.setPromoCodeId(promoCodeOffer.getPromoCodeId());
        } else {
            DiscountOffer discountOffer = Optional.ofNullable(application.getDiscountId())
                .map(discountId -> discountService.get(discountId))
                .map(d -> new DiscountOffer().setDiscountId(d.getId()).setRateInPercent(d.getRateInPercent()))
                .orElseGet(() -> getDiscountOffer(application.getClientId(), offerDate));
            inquiry.setInterestDiscountPercent(discountOffer.getRateInPercent());
            inquiry.setDiscountId(discountOffer.getDiscountId());
        }

        Offer offer = makeOffer(inquiry);

        LoanApplicationOfferCommand command = new LoanApplicationOfferCommand();
        command.setId(application.getId());
        command.setPrincipal(offer.getPrincipal());
        command.setInterest(offer.getInterest());
        command.setInterestDiscountPercent(offer.getInterestDiscountRatePercent());
        command.setInterestDiscountAmount(offer.getInterestDiscountAmount());
        command.setNominalApr(offer.getMonthlyInterestRatePercent());
        command.setEffectiveApr(offer.getAprPercent());
        command.setOfferDate(offerDate);
        command.setPeriodCount(offer.getTermInDays());
        command.setPeriodUnit(PeriodUnit.DAY);
        command.setDiscountId(offer.getDiscountId());
        loanApplicationService.updateOffer(command);
    }

    @Override
    public void sendLoanOfferSms(Long applicationId) {
        sendLoanOfferSmsInternal(applicationId, CmsSetup.LOAN_OFFER_SMS);
    }

    @Override
    public void sendLoanOfferEmail(Long applicationId, Long agreementAttachmentId, Long standardInformationAttachmentId) {
        sendLoanOfferEmailInternal(applicationId, agreementAttachmentId, standardInformationAttachmentId, CmsSetup.LOAN_OFFER_EMAIL);
    }

    @Override
    public void sendLoanOfferSmsUpsell(Long applicationId) {
        sendLoanOfferSmsInternal(applicationId, CmsSetup.LOAN_OFFER_SMS_UPSELL);
    }

    @Override
    public void sendLoanOfferEmailUpsell(Long applicationId, Long agreementAttachmentId, Long standardInformationAttachmentId) {
        sendLoanOfferEmailInternal(applicationId, agreementAttachmentId, standardInformationAttachmentId, CmsSetup.LOAN_OFFER_EMAIL_UPSELL);
    }

    private Long startApplicationWorkflow(Long applicationId, String wfName, Map<String, String> attributes) {
        LoanApplication application = loanApplicationService.get(applicationId);

        Map<String, String> finalAttributes = new HashMap<>();
        finalAttributes.putAll(attributes);

        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setWorkflowName(wfName);
        command.setClientId(application.getClientId());
        command.setApplicationId(application.getId());
        command.setAttributes(finalAttributes);

        Long workflowId = workflowService.startWorkflow(command);

        AttachWorkflowCommand attachWorkflowCommand = new AttachWorkflowCommand();
        attachWorkflowCommand.setWorkflowId(workflowId);
        attachWorkflowCommand.setApplicationId(applicationId);
        loanApplicationService.attachWorkflow(attachWorkflowCommand);

        return workflowId;
    }

    private void sendLoanOfferSmsInternal(Long applicationId, String cmsKey) {
        log.info("Sending offer SMS [{}]", applicationId);
        LoanApplication application = loanApplicationService.get(applicationId);
        Validate.isTrue(application.getType() == LoanApplicationType.NEW_LOAN, "Only new loan application type supported");
        notificationFactory.fromCustomerService(application.getClientId())
            .loanApplicationId(applicationId)
            .render(cmsKey, cmsModels.applicationContext(applicationId))
            .send();
    }

    private void sendLoanOfferEmailInternal(Long applicationId, Long agreementAttachmentId, Long standardInformationAttachmentId, String cmsKey) {
        LoanApplication application = loanApplicationService.get(applicationId);
        Validate.isTrue(application.getType() == LoanApplicationType.NEW_LOAN, "Only new loan application type supported");

        Attachment agreementAttachment = attachmentService.get(agreementAttachmentId);
        Attachment standardInformationAttachment = attachmentService.get(standardInformationAttachmentId);

        notificationFactory.fromCustomerService(application.getClientId())
            .emailAttachmentFileIds(ImmutableList.of(agreementAttachment.getFileId(), standardInformationAttachment.getFileId()))
            .loanApplicationId(applicationId)
            .render(cmsKey, cmsModels.applicationContext(applicationId))
            .send();
    }

    @Override
    public LoanApplication approveApplicationWithLongCode(String longCode, String ipAddress) {
        LoanApplication application = loanApplicationService.findLatest(LoanApplicationQuery.byLongApproveCode(longCode, LoanApplicationStatus.OPEN))
            .filter(app -> app.getOfferApprovedAt() == null)
            .orElse(null);

        if (application != null) {
            eventPublisher.publishEvent(new OfferApprovedEvent(application.getClientId()));
            loanApplicationService.approveOffer(new ApproveOfferCommand()
                .setId(application.getId())
                .setOfferApprovedAt(TimeMachine.now())
                .setOfferApprovedBy("email")
                .setOfferApprovedFromIpAddress(ipAddress));
        }
        return application;
    }

    @Override
    public void approveApplicationWithSms(Long clientId, String shortCode) {
        Optional<LoanApplication> application = loanApplicationService.findLatest(LoanApplicationQuery.byShortApproveCode(clientId, shortCode, LoanApplicationStatus.OPEN));
        if (application.isPresent()) {
            loanApplicationService.approveOffer(new ApproveOfferCommand().setId(application.get().getId()).setOfferApprovedAt(TimeMachine.now()).setOfferApprovedBy("sms"));
            eventPublisher.publishEvent(new OfferApprovedEvent(application.get().getClientId()));
        }
    }

    @Override
    public void webApproveApplication(Long clientId, Long applicationId, String ipAddress) {
        Optional<LoanApplication> application = loanApplicationService.findLatest(new LoanApplicationQuery().setClientId(clientId).setApplicationId(applicationId));
        if (application.isPresent()) {
            loanApplicationService.approveOffer(new ApproveOfferCommand().setId(applicationId).setOfferApprovedAt(TimeMachine.now()).setOfferApprovedBy("web").setOfferApprovedFromIpAddress(ipAddress));
            eventPublisher.publishEvent(new OfferApprovedEvent(application.get().getClientId()));
        }
    }

    @Override
    public void webApproveUpsellOffer(Long clientId, Long applicationId, BigDecimal principal, String ipAddress, String abSource) {
        Optional<LoanApplication> maybeApplication = loanApplicationService.findLatest(new LoanApplicationQuery().setClientId(clientId).setApplicationId(applicationId));
        if (maybeApplication.isPresent()) {
            LoanApplication application = maybeApplication.get();

            Offer offer = makeOffer(new Inquiry()
                .setApplicationId(application.getId())
                .setInterestStrategyId(application.getInterestStrategyId())
                .setPrincipal(principal)
                .setTermInDays(application.getRequestedPeriodCount())
                .setInterestDiscountPercent(application.getOfferedInterestDiscountPercent())
                .setSubmittedAt(application.getSubmittedAt())
                .setDiscountId(application.getDiscountId()))
                .setPromoCodeId(application.getPromoCodeId());

            LoanApplicationOfferCommand command = new LoanApplicationOfferCommand();
            command.setId(applicationId);
            command.setPrincipal(offer.getPrincipal());
            command.setInterest(offer.getInterest());
            command.setInterestDiscountPercent(offer.getInterestDiscountRatePercent());
            command.setInterestDiscountAmount(offer.getInterestDiscountAmount());
            command.setNominalApr(offer.getMonthlyInterestRatePercent());
            command.setEffectiveApr(offer.getAprPercent());
            command.setOfferDate(TimeMachine.today());
            command.setPeriodCount(offer.getTermInDays());
            command.setPeriodUnit(PeriodUnit.DAY);
            command.setDiscountId(offer.getDiscountId());
//            command.setPromoCodeId(offer.getPromoCodeId());
            loanApplicationService.updateOffer(command);

            Long agreementFileId = generateLoanAgreement(applicationId);
            workflowService.setAttribute(application.getWorkflowId(), Attributes.AGREEMENT_ATTACHMENT_ID, agreementFileId.toString());

            Long standardInformationFileId = generateStandardInformation(applicationId);
            workflowService.setAttribute(application.getWorkflowId(), Attributes.STANDARD_INFORMATON_ATTACHMENT_ID, standardInformationFileId.toString());

            //temporary solution while complete AB test module is under consideration
            if (abSource != null) {
                workflowService.setAttribute(application.getWorkflowId(), UPSELL_AB_TEST_WORKFLOW_ATTRIBUTE, abSource);
            }
            webApproveApplication(clientId, applicationId, ipAddress);
        }
    }

    @Override
    public Long generateLoanAgreement(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);
        Map<String, Object> context = cmsModels.agreementContext(applicationId);
        Optional<Pdf> pdfMaybe = pdfRenderer.render(CmsSetup.LOAN_AGREEMENT_PDF, context, AlfaConstants.LOCALE);
        Validate.isTrue(pdfMaybe.isPresent(), "Agreement PDF not generated for application: [%s]", applicationId);
        Pdf pdf = pdfMaybe.get();
        CloudFile pdfFile = savePdfToLoanAgreementDir(pdf);
        return saveLoanAgreementAttachment(application, pdfFile);
    }

    @Override
    public Long generateStandardInformation(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);
        Map<String, Object> context = cmsModels.standardInformationContext(applicationId);
        Optional<Pdf> pdfMaybe = pdfRenderer.render(CmsSetup.STANDARD_INFORMATION_PDF, context, AlfaConstants.LOCALE);
        Validate.isTrue(pdfMaybe.isPresent(), "Standard information PDF not generated for application: [%s]", applicationId);
        Pdf pdf = pdfMaybe.get();
        CloudFile pdfFile = savePdfToLoanAgreementDir(pdf);
        return saveStandardInformationAttachment(application, pdfFile);
    }

    @Override
    public Long generateUpsellAgreement(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);
        ImmutableMap<String, Object> context = ImmutableMap.of(
            AlfaCmsModels.SCOPE_COMPANY, cmsModels.company(),
            AlfaCmsModels.SCOPE_CLIENT, cmsModels.client(application.getClientId()),
            AlfaCmsModels.SCOPE_UPSELL, cmsModels.upsellModel(application.getId())
        );
        Optional<Pdf> pdfMaybe = pdfRenderer.render(CmsSetup.UPSELL_AGREEMENT_PDF, context, AlfaConstants.LOCALE);
        Validate.isTrue(pdfMaybe.isPresent(), "Upsell agreement PDF not generated for application: [%s]", applicationId);
        Pdf pdf = pdfMaybe.get();
        CloudFile pdfFile = savePdfToLoanAgreementDir(pdf);
        return saveUpsellAgreementAttachment(application, pdfFile);
    }

    @Override
    public LoanIssueResult issueLoan(Long applicationId, LocalDate issueDate) {
        LoanApplication application = loanApplicationService.get(applicationId);
        log.info("Issuing loan on [{}] from application [{}]", issueDate, application);

        long openLoans = loanService.findLoans(LoanQuery.openLoans(application.getClientId())).size();
        Validate.isTrue(openLoans == 0, "Can not issue loan, client [%s] has already open loan", application.getClientId());

        Validate.isPositive(application.getOfferedPrincipal(), "Offered principal must be > 0, application: [%s]", application);

        IssueLoanCommand loanCommand = IssueLoanCommand.builder()
            .loanApplicationId(application.getId())
            .loanNumber(application.getNumber())
            .issueDate(issueDate)
            .build();
        Long loanId = loanService.issueLoan(loanCommand);
        Loan loan = loanService.getLoan(loanId);

        Client client = clientService.get(application.getClientId());
        if (!client.isAcceptMarketing()) {
            clientService.updateAcceptMarketing(new ChangeAcceptMarketingCommand(client.getId(), true));
        }
        Long disbursementId = addDisbursement(application, loan, issueDate);
        return new LoanIssueResult().setLoanId(loanId).setDisbursementId(disbursementId);
    }

    @Override
    public Long retryApplication(Long applicationId) {
        log.info("Retrying application: [{}]", applicationId);
        LoanApplication application = loanApplicationService.get(applicationId);
        Validate.notNull(application.getWorkflowId(), "Application has no workflow attached: [%s]", application);
        Validate.isTrue(!hasOpenLoanApplication(application), "Client already has an active loan application");
        Validate.isTrue(loanService.findLoans(LoanQuery.openLoans(application.getClientId())).isEmpty(), "Client already has an active loan");
        terminateCurrentWorkflow(application);
        return restartWorkflow(application);
    }

    private boolean hasOpenLoanApplication(LoanApplication application) {
        return loanApplicationService.find(LoanApplicationQuery.byClientId(application.getClientId(), LoanApplicationStatus.OPEN)).stream().anyMatch(a -> !a.getId().equals(application.getId()));
    }

    private Long restartWorkflow(LoanApplication application) {
        Workflow rootWorkflow = workflowService.getRootWorkflow(application.getWorkflowId());
        StartWorkflowCommand command = new StartWorkflowCommand();
        command.setAttributes(rootWorkflow.getAttributes());
        command.setClientId(rootWorkflow.getClientId());
        command.setWorkflowName(rootWorkflow.getName());
        command.setApplicationId(rootWorkflow.getApplicationId());
        command.setLoanId(rootWorkflow.getLoanId());
        Long newWorkflowId = workflowService.startWorkflow(command);
        attachWorkflow(application.getId(), newWorkflowId);
        eventPublisher.publishEvent(new LoanApplicationRetriedEvent(application));
        log.info("Started new workflow [{}] for retrying application [{}]", newWorkflowId, application.getId());
        return newWorkflowId;
    }

    private void terminateCurrentWorkflow(LoanApplication application) {
        Workflow existingWorkflow = workflowService.getWorkflow(application.getWorkflowId());
        if (existingWorkflow.getStatus() != WorkflowStatus.TERMINATED && existingWorkflow.getStatus() != WorkflowStatus.EXPIRED) {
            workflowService.terminateWorkflow(existingWorkflow.getId(), "RetryApplication");
        }
    }

    private void attachWorkflow(Long applicationId, Long workflowId) {
        if (applicationId != null) {
            AttachWorkflowCommand command = new AttachWorkflowCommand();
            command.setWorkflowId(workflowId);
            command.setApplicationId(applicationId);
            loanApplicationService.attachWorkflow(command);
        }
    }

    private Long addDisbursement(LoanApplication application, Loan loan, LocalDate valueDate) {
        AddDisbursementCommand addDisbursement = new AddDisbursementCommand();
        addDisbursement.setLoanId(loan.getId());
        addDisbursement.setClientId(loan.getClientId());
        addDisbursement.setAmount(application.getOfferedPrincipal());
        addDisbursement.setValueDate(valueDate);

        Institution institution = institutionFinder.findDisbursementInstitution(loan.getClientId());
        addDisbursement.setInstitutionId(institution.getId());
        addDisbursement.setInstitutionAccountId(institution.getPrimaryAccount().getId());

        addDisbursement.setReference(generateDisbursementReference());
        addDisbursement.setApplicationId(application.getId());
        return disbursementService.add(addDisbursement);
    }

    private String generateDisbursementReference() {
        return disbursementService.generateReference(AlfaConstants.DISBURSEMENT_REFERENCE_PREFIX,
            AlfaConstants.DISBURSEMENT_REFERENCE_ENDING, AlfaConstants.DISBURSEMENT_REFERENCE_LENGTH);
    }

    private CloudFile savePdfToLoanAgreementDir(Pdf pdf) {
        return fileStorageService.save(FileStorageCommandFactory.fromPdf(AlfaConstants.FILE_DIRECTORY_AGREEMENTS, pdf));
    }

    private Long saveLoanAgreementAttachment(LoanApplication application, CloudFile pdfFile) {
        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setLoanId(application.getLoanId());
        addAttachment.setClientId(application.getClientId());
        addAttachment.setApplicationId(application.getId());
        addAttachment.setFileId(pdfFile.getFileId());
        addAttachment.setStatus(AttachmentStatus.WAITING_APPROVAL);
        addAttachment.setAttachmentType(AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT);
        addAttachment.setName(pdfFile.getOriginalFileName());
        return attachmentService.addAttachment(addAttachment);
    }

    private Long saveStandardInformationAttachment(LoanApplication application, CloudFile pdfFile) {
        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setLoanId(application.getLoanId());
        addAttachment.setClientId(application.getClientId());
        addAttachment.setApplicationId(application.getId());
        addAttachment.setFileId(pdfFile.getFileId());
        addAttachment.setStatus(AttachmentStatus.OK);
        addAttachment.setAttachmentType(AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION);
        addAttachment.setName(pdfFile.getOriginalFileName());
        return attachmentService.addAttachment(addAttachment);
    }

    private Long saveUpsellAgreementAttachment(LoanApplication application, CloudFile pdfFile) {
        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setLoanId(application.getLoanId());
        addAttachment.setClientId(application.getClientId());
        addAttachment.setApplicationId(application.getId());
        addAttachment.setFileId(pdfFile.getFileId());
        addAttachment.setStatus(AttachmentStatus.OK);
        addAttachment.setAttachmentType(AlfaConstants.ATTACHMENT_TYPE_UPSELL_AGREEMENT);
        addAttachment.setName(pdfFile.getOriginalFileName());
        return attachmentService.addAttachment(addAttachment);
    }

    private BigDecimal calculateInterest(Inquiry inquiry) {
        InterestStrategy interestStrategy = Optional.ofNullable(inquiry.getInterestStrategyId())
            .flatMap(id -> strategyService.getInterestStrategyById(id))
            .orElseGet(() -> strategyService.getDefaultStrategyId(StrategyType.INTEREST.getType())
                .flatMap(id -> strategyService.getInterestStrategyById(id))
                .orElseThrow(() -> new IllegalStateException("Can't calculate interest for offer")));
        return interestStrategy.calculateInterest(inquiry.getPrincipal(), inquiry.getTermInDays(), Optional.ofNullable(inquiry.getApplicationId()));
    }

    private BigDecimal calculateInterestDiscount(BigDecimal interest, BigDecimal discount) {
       return interest.multiply(discount.divide(amount(100), 8, BigDecimal.ROUND_HALF_UP))
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private BigDecimal calculateMonthlyInterestRate(BigDecimal interest, BigDecimal principal, Long term) {
        return interest
            .multiply(amount(100))
            .multiply(amount(30))
            .divide(principal, 8, BigDecimal.ROUND_HALF_UP)
            .divide(amount(term), 8, BigDecimal.ROUND_HALF_UP)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
