package fintech.spain.alfa.web.services;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import fintech.BigDecimalUtils;
import fintech.FileHashId;
import fintech.TimeMachine;
import fintech.crm.address.ClientAddressService;
import fintech.crm.attachments.AddAttachmentCommand;
import fintech.crm.attachments.Attachment;
import fintech.crm.attachments.AttachmentConstants;
import fintech.crm.attachments.AttachmentStatus;
import fintech.crm.attachments.AttachmentSubType;
import fintech.crm.attachments.ClientAttachmentService;
import fintech.crm.client.ClientService;
import fintech.crm.client.model.ChangeAcceptMarketingCommand;
import fintech.crm.logins.ChangePasswordCommand;
import fintech.crm.logins.EmailLogin;
import fintech.crm.logins.EmailLoginService;
import fintech.dc.DcService;
import fintech.dc.DcSettingsService;
import fintech.dc.model.DcSettings;
import fintech.dc.model.Debt;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.LoanStatusDetail;
import fintech.lending.core.loan.ScheduleService;
import fintech.spain.alfa.web.models.*;
import fintech.spain.alfa.web.models.convertor.InstallmentInfoConverter;
import fintech.spain.alfa.web.models.convertor.PersonalDetailsResponseConverter;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.spain.alfa.product.lending.Inquiry;
import fintech.spain.alfa.product.lending.Offer;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.lending.impl.AprCalculator;
import fintech.spain.alfa.product.registration.RegistrationFacade;
import fintech.spain.alfa.product.registration.events.DocumentsUploadedEvent;
import fintech.spain.alfa.product.registration.forms.AffiliateData;
import fintech.spain.alfa.product.utils.SpainAddressUtils;
import fintech.spain.alfa.product.workflow.common.Attributes;
import fintech.spain.alfa.web.models.*;
import fintech.strategy.model.ExtensionOffer;
import fintech.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.BigDecimalUtils.loe;
import static fintech.spain.alfa.product.AlfaConstants.WEB_ANALYTICS_LOAN_APPLICATION_EVENT;
import static java.util.Comparator.comparing;

@Transactional
@Component
public class WebProfileService {

    @Autowired
    private LoanService loanService;

    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private ClientAttachmentService clientAttachmentService;

    @Autowired
    private UnderwritingFacade underwritingFacade;

    @Autowired
    private EmailLoginService emailLoginService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DcService dcService;

    @Autowired
    private DcSettingsService dcSettingsService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private WorkflowService workflowService;

//    @Autowired
//    private RegistrationFacade registrationFacade;

    private final PersonalDetailsResponseConverter clientConverter = new PersonalDetailsResponseConverter();

//    public Offer prepareOffer(Long clientId, PrepareOfferRequest request) {
//        Inquiry inquiry = registrationFacade.buildInquiry(clientId, request.getAmount(), request.getTermInDays(), request.getPromoCode(), null);
//        return underwritingFacade.makeOffer(inquiry);
//    }

    public boolean changeAcceptMarketing(Long clientId, boolean acceptMarketing, String source) {
        ChangeAcceptMarketingCommand command = new ChangeAcceptMarketingCommand(clientId, acceptMarketing, source);
        clientService.updateAcceptMarketing(command);
        return acceptMarketing;
    }

//    public void submitLoanApplication(Long clientId, SubmitLoanApplicationRequest request) {
//        Inquiry inquiry = registrationFacade.buildInquiry(clientId, request.getAmount(), request.getTermInDays(), request.getPromoCode(), getAffiliateName(request));
//        Long applicationId = underwritingFacade.submitApplication(clientId, inquiry);
//
//        if (request.getAnalytics() != null) {
//            registrationFacade.saveAnalyticsData(clientId, applicationId, request.getAnalytics(), WEB_ANALYTICS_LOAN_APPLICATION_EVENT);
//        }
//        if (request.isAffiliate()) {
//            registrationFacade.saveAffiliateData(clientId, applicationId, request.getAffiliate());
//            underwritingFacade.startFirstLoanAffiliatesApplicationWorkflow(applicationId, ImmutableMap.of());
//        } else {
//            underwritingFacade.startFirstLoanApplicationWorkflow(applicationId, ImmutableMap.of());
//        }
//    }

    private String getAffiliateName(SubmitLoanApplicationRequest request) {
        return Optional.ofNullable(request.getAffiliate()).map(AffiliateData::getAffiliateName).orElse(null);
    }

    public LoansResponse getLoans(Long clientId) {
        List<Loan> loans = loanService.findLoans(LoanQuery.nonVoidedLoans(clientId));
        List<LoanData> loansData = loans.stream().map(this::toLoanData).collect(Collectors.toList());
        return new LoansResponse().setLoans(loansData);
    }

    private LoanData toLoanData(Loan loan) {
        LoanData data = new LoanData();
        data.setStatus(loan.getStatus());
        data.setStatusDetail(loan.getStatusDetail());
        data.setNumber(loan.getNumber());
        data.setIssueDate(loan.getIssueDate());
        data.setCloseDate(loan.getCloseDate());
        data.setMaturityDate(loan.getMaturityDate());
        data.setOverdueDays(loan.getOverdueDays());
        data.setBrokenDate(loan.getBrokenDate());
        data.setPrincipalDisbursed(amount(loan.getPrincipalDisbursed()));
        data.setInterestDue(amount(loan.getInterestDue()));
        data.setInterestApplied(amount(loan.getInterestApplied()));

        data.setTotalDue(amount(loan.getTotalDue()));
        data.setTotalPaid(amount(loan.getTotalPaid()));
        data.setTotalOutstanding(amount(loan.getTotalOutstanding()));
        data.setPenaltyDue(amount(loan.getPenaltyDue()));
        data.setFeeDue(amount(loan.getFeeDue()));

        data.setPrincipalOutstanding(amount(loan.getPrincipalOutstanding()));
        data.setPenaltyOutstanding(amount(loan.getPenaltyOutstanding()));
        data.setInterestOutstanding(amount(loan.getInterestOutstanding()));

        if (loan.isOpen(LoanStatusDetail.ACTIVE)) {
            List<LoanData.ExtensionData> extensionData = findExtensionOptions(loan);
            if (!extensionData.isEmpty()) {
                data.setExtensionStatus(ExtensionStatus.AVAILABLE);
            } else {
                data.setExtensionStatus(ExtensionStatus.LIMIT_REACHED);
            }
            data.setExtensionOptions(extensionData);
        }

        Optional<Debt> debt = dcService.findByLoanId(loan.getId());
        debt.ifPresent(d -> data.setDebtPortfolio(d.getPortfolio().toUpperCase()));
        debt.ifPresent(d -> data.setDebtStatus(d.getStatus().toUpperCase()));

        clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byLoan(loan.getId(), AttachmentConstants.ATTACHMENT_TYPE_LOAN_AGREEMENT)).stream().findFirst()
            .map(this::toAttachmentData).ifPresent(data::setLoanAgreementAttachment);
        clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byLoan(loan.getId(), AlfaConstants.ATTACHMENT_TYPE_RESCHEDULING_TOC)).stream().findFirst()
            .map(this::toAttachmentData).ifPresent(data::setReschedulingAgreementAttachment);
        clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byLoan(loan.getId(), AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION)).stream().findFirst()
            .map(this::toAttachmentData).ifPresent(data::setStandardInformationAttachment);
        data.setInstallments(scheduleService.findInstallments(InstallmentQuery.allLoanInstallments(loan.getId())).stream().map(InstallmentInfoConverter.INSTANCE::convert).collect(Collectors.toList()));

        return data;
    }

    private List<LoanData.ExtensionData> findExtensionOptions(Loan loan) {
        DcSettings.ExtensionSettings extensionSettings = dcSettingsService.getSettings().getExtensionSettings();
        Long extensionDays = loan.getExtendedByDays();
        List<ExtensionOffer> offers = extensionService.listOffersForLoan(loan.getId(), TimeMachine.today());
        return offers.stream()
            .filter(extensionOffer -> extensionDays + extensionOffer.getPeriodCount() < extensionSettings.getMaxPeriodDays())
            .map(offer -> {
                    BigDecimal discountPercent = null;
                    BigDecimal discountPrice = null;
                    if (!BigDecimalUtils.isZero(offer.getDiscountPercent())) {
                        discountPercent = offer.getDiscountPercent();
                        discountPrice = offer.getPriceWithDiscount();
                    }
                    return new LoanData.ExtensionData()
                        .setExtendByDays(offer.getPeriodCount())
                        .setPrice(amount(offer.getPrice()))
                        .setExtendedMaturityDate(loan.getMaturityDate().plusDays(offer.getPeriodCount()))
                        .setDiscountPct(discountPercent)
                        .setDiscountPrice(discountPrice);
                }
            ).collect(Collectors.toList());
    }

    private AttachmentData toAttachmentData(Attachment attachment) {
        return new AttachmentData()
            .setFileId(FileHashId.encodeFileId(attachment.getClientId(), attachment.getFileId()))
            .setName(attachment.getName());
    }

    public void changePassword(Long clientId, ChangePasswordRequest params) {
        EmailLogin emailLogin = emailLoginService.findByClientId(clientId).orElseThrow(() -> new IllegalArgumentException("Email login not found"));
        ChangePasswordCommand changePasswordCommand = new ChangePasswordCommand();
        changePasswordCommand.setClientId(clientId);
        changePasswordCommand.setEmail(emailLogin.getEmail());
        changePasswordCommand.setNewPassword(params.getNewPassword());
        changePasswordCommand.setCurrentPassword(params.getCurrentPassword());
        emailLoginService.changePassword(changePasswordCommand);
    }

    public PersonalDetailsResponse getClient(Long clientId) {
        PersonalDetailsResponse response = clientConverter.convert(clientService.get(clientId));
        clientAddressService.getClientPrimaryAddress(clientId, AlfaConstants.ADDRESS_TYPE_ACTUAL).ifPresent(address -> {
            response.setAddress(SpainAddressUtils.fullAddress(address));
        });
        return response;
    }

    public void uploadDocument(Long clientId, MultipartFile multiPart, AttachmentSubType attachmentSubType) throws IOException {
        SaveFileCommand saveFileCommand = new SaveFileCommand();
        saveFileCommand.setInputStream(multiPart.getInputStream());
        saveFileCommand.setOriginalFileName(multiPart.getOriginalFilename());
        saveFileCommand.setContentType(multiPart.getContentType());
        saveFileCommand.setDirectory("registration");
        CloudFile file = fileStorageService.save(saveFileCommand);

        AddAttachmentCommand addAttachment = new AddAttachmentCommand();
        addAttachment.setClientId(clientId);
        addAttachment.setFileId(file.getFileId());
        addAttachment.setStatus(AttachmentStatus.OK);
        addAttachment.setAttachmentType(AlfaConstants.ATTACHMENT_TYPE_CLIENT_UPLOAD);
        addAttachment.setAttachmentSubType(attachmentSubType);
        addAttachment.setName(file.getOriginalFileName());
        clientAttachmentService.addAttachment(addAttachment);
    }

    public List<AttachmentInfo> findAttachmentInfos(Long clientId) {
        List<Attachment> attachments = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(clientId, AlfaConstants.ATTACHMENT_TYPE_CLIENT_UPLOAD));
        return attachments.stream().map(AttachmentInfo::new).collect(Collectors.toList());
    }

    public void uploadedDocuments(Long clientId) {
        eventPublisher.publishEvent(new DocumentsUploadedEvent().setClientId(clientId));
    }

    public LoanApplicationData getLoanApplication(Long clientId) {
        LoanApplication loanApplication = loanApplicationService.findLatest(LoanApplicationQuery.byClientId(clientId, LoanApplicationStatus.OPEN))
            .orElseThrow(() -> new IllegalStateException(String.format("Unable to find open loan application for client [%s]", clientId)));

        BigDecimal aprPercent = AprCalculator.calculate(loanApplication.getOfferedPrincipal(), loanApplication.getOfferedInterest(), loanApplication.getOfferedPeriodCount())
            .setScale(0, BigDecimal.ROUND_HALF_UP);

        BigDecimal monthlyInterestRatePercent = loanApplication.getOfferedInterest()
            .multiply(amount(100))
            .multiply(amount(30))
            .divide(loanApplication.getOfferedPrincipal(), 8, BigDecimal.ROUND_HALF_UP)
            .divide(amount(loanApplication.getOfferedPeriodCount()), 8, BigDecimal.ROUND_HALF_UP)
            .setScale(2, BigDecimal.ROUND_HALF_UP);

        Attachment attachment = clientAttachmentService.findAttachments(ClientAttachmentService.AttachmentQuery.byClient(loanApplication.getClientId(), AttachmentConstants.ATTACHMENT_TYPE_STANDARD_INFORMATION)).stream()
            .max(comparing(Attachment::getId))
            .orElseThrow(() -> new IllegalStateException(String.format("Unable to find standard information pdf for client [%s]", clientId)));

        String standardInformationPdfFileHashId = FileHashId.encodeFileId(loanApplication.getClientId(), attachment.getFileId());

        List<UpsellOfferData> upsellOffers = Optional.ofNullable(loanApplication.getWorkflowId())
            .map(workflowService::getWorkflow)
            .filter(workflow -> workflow.hasAttribute(Attributes.UPSELL_AVAILABLE))
            .map(workflow -> calculateUpsellOffers(loanApplication))
            .orElse(Lists.newArrayList());

        return new LoanApplicationData()
            .setId(loanApplication.getId())
            .setPrincipal(loanApplication.getOfferedPrincipal())
            .setInterest(loanApplication.getOfferedInterest())
            .setTotal(loanApplication.getOfferedPrincipal().add(loanApplication.getOfferedInterest()))
            .setTermInDays(loanApplication.getOfferedPeriodCount())
            .setStartDate(TimeMachine.today())
            .setMaturityDate(TimeMachine.today().plusDays(loanApplication.getOfferedPeriodCount()))
            .setAprPercent(aprPercent)
            .setNominalApr(monthlyInterestRatePercent.multiply(amount(12)))
            .setMonthlyInterestRatePercent(monthlyInterestRatePercent)
            .setStandardInformationPdfFileHashId(standardInformationPdfFileHashId)
            .setUpsellOffers(upsellOffers);
    }

    private List<UpsellOfferData> calculateUpsellOffers(LoanApplication loanApplication) {
        List<UpsellOfferData> upsellOffers = Lists.newArrayList();

        BigDecimal start = loanApplication.getRequestedPrincipal().add(amount(100)).setScale(-2, RoundingMode.DOWN).setScale(2, RoundingMode.UNNECESSARY);
        BigDecimal end = loanApplication.getCreditLimit();

        do {
            Offer offer = underwritingFacade.makeOffer(new Inquiry()
                .setInterestStrategyId(loanApplication.getInterestStrategyId())
                .setPrincipal(start)
                .setTermInDays(loanApplication.getRequestedPeriodCount())
                .setInterestDiscountPercent(loanApplication.getOfferedInterestDiscountPercent())
                .setSubmittedAt(loanApplication.getSubmittedAt())
                .setDiscountId(loanApplication.getDiscountId()))
                .setPromoCodeId(loanApplication.getPromoCodeId());

            upsellOffers.add(new UpsellOfferData()
                .setPrincipal(offer.getPrincipal())
                .setInterest(offer.getInterest())
                .setTotal(offer.getTotal())
                .setAprPercent(offer.getAprPercent())
                .setMonthlyInterestRatePercent(offer.getMonthlyInterestRatePercent())
                .setNominalApr(offer.getMonthlyInterestRatePercent().multiply(amount(12)))
            );
            start = start.add(amount(100));
        } while (loe(start, end));

        return upsellOffers;
    }
}
