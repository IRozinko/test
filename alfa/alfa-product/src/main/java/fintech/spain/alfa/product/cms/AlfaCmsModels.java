package fintech.spain.alfa.product.cms;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.cms.CmsModels;
import fintech.crm.address.ClientAddressService;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.crm.contacts.EmailContactService;
import fintech.crm.contacts.PhoneContactService;
import fintech.dc.DcService;
import fintech.dc.model.Debt;
import fintech.instantor.InstantorService;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.loan.Contract;
import fintech.lending.core.loan.Installment;
import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.lending.core.product.ProductService;
import fintech.lending.payday.settings.PaydayOfferSettings;
import fintech.lending.payday.settings.PaydayProductSettings;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.model.Disbursement;
import fintech.payments.model.Institution;
import fintech.settings.SettingsService;
import fintech.spain.alfa.product.AlfaConstants;
import fintech.spain.alfa.product.extension.ExtensionService;
import fintech.spain.alfa.product.lending.DiscountOffer;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.payments.PaymentsSetup;
import fintech.spain.alfa.product.settings.AlfaSettings;
import fintech.spain.platform.web.SpecialLinkType;
import fintech.spain.platform.web.model.SpecialLink;
import fintech.spain.platform.web.model.command.BuildLinkCommand;
import fintech.spain.platform.web.spi.SpecialLinkService;
import fintech.spain.alfa.product.utils.SpainAddressUtils;
import fintech.spain.alfa.product.web.WebLoginService;
import fintech.spain.alfa.strategy.CalculationStrategyCmsItemKey;
import fintech.spain.alfa.strategy.extension.ExtensionStrategyProperties;
import fintech.spain.alfa.strategy.interest.MonthlyInterestStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DailyPenaltyStrategyProperties;
import fintech.spain.alfa.strategy.penalty.DpdPenaltyStrategyProperties;
import fintech.strategy.CalculationStrategyService;
import fintech.strategy.db.CalculationStrategyEntity;
import fintech.strategy.db.CalculationStrategyRepository;
import fintech.strategy.model.ExtensionOffer;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.iban4j.Iban;
import org.iban4j.IbanFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.PojoUtils.npeSafe;
import static fintech.TimeMachine.now;
import static fintech.TimeMachine.today;
import static fintech.lending.core.PeriodUnit.DAY;
import static fintech.spain.platform.web.model.command.SpecialLinkQuery.byClientId;
import static fintech.spain.alfa.product.cms.CmsSetup.testClientModel;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

@Slf4j
@Component
public class AlfaCmsModels implements CmsModels {

    public static final String SCOPE_CLIENT = "client";
    public static final String SCOPE_COMPANY = "company";
    public static final String SCOPE_LOAN = "loan";
    public static final String SCOPE_DEBT = "debt";
    public static final String SCOPE_APPLICATION = "application";
    public static final String SCOPE_CONTACT_ME = "contactMe";
    public static final String SCOPE_STANDARD_INFORMATION = "standardInformation";
    public static final String SCOPE_RESET_PASSWORD = "resetPassword";
    public static final String SCOPE_PHONE_VERIFICATION = "phoneVerification";
    public static final String SCOPE_CLIENT_INCOMING_PAYMENT = "clientIncomingPayment";
    public static final String SCOPE_CLIENT_REPAYMENT = "clientRepayment";
    public static final String SCOPE_SCHEDULE = "schedule";
    public static final String SCOPE_UPSELL = "upsell";
    public static final String SCOPE_SPECIAL_LINK = "specialLink";
    public static final String SCOPE_AUTO_LOGIN = "autoLogin";
    public static final String SCOPE_CALCULATION_STRATEGY = "calculationStrategy";
    public static final String SCOPE_DISBURSEMENT = "disbursement";
    public static final String SCOPE_INSTANTOR_REPORT = "instantor";


    @Autowired
    private EmailContactService emailContactService;

    @Autowired
    private PhoneContactService phoneContactService;

    @Autowired
    private ClientBankAccountService clientBankAccountService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private ClientAddressService clientAddressService;

    @Autowired
    private LoanApplicationService loanApplicationService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private SettingsService settingsService;

    @Autowired
    private DcService dcService;

    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private SpecialLinkService specialLinkService;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private WebLoginService webLoginService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CalculationStrategyService calculationStrategyService;

    @Autowired
    private CalculationStrategyRepository calculationStrategyRepository;

    @Autowired
    private LoanServicingFacade loanServicingFacade;

    private final UnderwritingFacade underwritingFacade;

    @Value("${alfa.web.baseUrl:http://localhost:4200}")
    private String webBaseUrl = "http://localhost:4200";

    @Value("${alfa.api.baseUrl:http://localhost:8080}")
    private String apiBaseUrl = "http://localhost:8080";

    @Autowired
    private DisbursementService disbursementService;

    @Autowired
    private InstantorService instantorService;

    public AlfaCmsModels(@Lazy UnderwritingFacade underwritingFacade) {
        this.underwritingFacade = underwritingFacade;
    }

    public CompanyModel company() {
        AlfaSettings.CompanyContactDetails companyContactDetails = settingsService.getJson(
            AlfaSettings.COMPANY_CONTACT_DETAILS, AlfaSettings.CompanyContactDetails.class);

        CompanyModel model = new CompanyModel();
        model.setWebBaseUrl(webBaseUrl);
        model.setApiBaseUrl(apiBaseUrl);
        model.setName(companyContactDetails.getName());
        model.setNumber(companyContactDetails.getNumber());
        model.setAddressLine1(companyContactDetails.getAddressLine1());
        model.setAddressLine2(companyContactDetails.getAddressLine2());
        model.setPhone(companyContactDetails.getPhone());
        model.setEmail(companyContactDetails.getEmail());
        model.setWebSite(companyContactDetails.getWebSite());
        model.setIncomingSmsNumber(companyContactDetails.getIncomingSmsNumber());
        model.setDefaultLocale(AlfaConstants.LOCALE);
        model.setCurrency(AlfaConstants.CURRENCY);
        model.setBankAccounts(institutionService.getAllInstitutions()
            .stream()
            .filter(institution -> !institution.isDisabled())
            .filter(institution -> equalsIgnoreCase(institution.getInstitutionType(), PaymentsSetup.TYPE_BANK))
            .filter(institution -> institution.findPrimaryAccount().isPresent())
            .map(institution -> new BankAccountModel(
                institution.getName(),
                Iban.valueOf(institution.getPrimaryAccount().getAccountNumber()).toFormattedString()))
            .collect(Collectors.toList())
        );

        return model;
    }

    public ClientModel client(Long clientId) {
        Validate.notNull(clientId);
        ClientModel model = new ClientModel();
        Client client = clientService.get(clientId);
        model.setNumber(client.getNumber());
        model.setFirstName(client.getFirstName());
        model.setLastName(client.getLastName());
        model.setSecondLastName(trimToEmpty(client.getSecondLastName()));
        model.setFullName(client.getFirstName() + " " + client.getLastName() + " " + trimToEmpty(client.getSecondLastName()));
        model.setDocumentNumber(client.getDocumentNumber());
        emailContactService.findPrimaryEmail(clientId).ifPresent(email -> model.setEmail(email.getEmail()));
        phoneContactService.findPrimaryPhone(clientId).ifPresent(phone -> model.setPhoneNumber(phone.getPhoneNumber()));
        clientBankAccountService.findPrimaryByClientId(clientId).ifPresent(account -> {
            model.setIban(account.getAccountNumber());
            try {
                model.setIbanFormatted(Iban.valueOf(account.getAccountNumber()).toFormattedString());
            } catch (IbanFormatException e) {
                log.warn("Invalid IBAN account [{}] for client [{}]", account.getAccountNumber(), clientId);
                model.setIbanFormatted(account.getAccountNumber());
            }
        });

        clientAddressService.getClientPrimaryAddress(clientId, AlfaConstants.ADDRESS_TYPE_ACTUAL).ifPresent(address -> {
            model.setAddressLine1(SpainAddressUtils.addressLine1(address));
            model.setAddressLine2(SpainAddressUtils.addressLine2(address));
        });
        model.setRegisteredAt(client.getCreatedAt());
        loanApplicationService.findFirst(LoanApplicationQuery.byClientId(client.getId())).ifPresent(loanApplication -> {
            model.setRegistrationIpAddress(loanApplication.getIpAddress());
            model.setCreditLimit(loanApplication.getCreditLimit());
        });
        DiscountOffer discountOffer = underwritingFacade.getDiscountOffer(client.getId(), TimeMachine.today());
        model.setLoyaltyDiscount(discountOffer.getRateInPercent());
        model.setAcceptMarketing(client.isAcceptMarketing());
        return model;
    }

    public LoanModel loan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        List<ExtensionOffer> extensions = extensionService.listOffersForLoan(loanId, today());

        Optional<LoanPrepayment> loanPrepayment = Optional.of(loan)
            .filter(Loan::isLoanBeforeEndOfTerm)
            .map(l -> loanServicingFacade.calculatePrepayment(l.getId(), today()));

        LoanModel model = new LoanModel()
            .setNumber(loan.getNumber())
            .setIssueDate(loan.getIssueDate())
            .setMaturityDate(loan.getMaturityDate())
            .setPrincipal(loan.getPrincipalDisbursed())
            .setTotalDue(loan.getPrincipalWrittenOff().add(loan.getInterestWrittenOff()).add(loan.getPenaltyWrittenOff()))
            .setTotalOutstanding(loan.getTotalOutstanding())
            .setFeeOutstanding(loan.getFeeOutstanding())
            .setExtensions(extensions)
            .setInterestDue(loan.getInterestWrittenOff())
            .setPenaltyDue(loan.getPenaltyWrittenOff())
            .setPrincipalDue(loan.getPrincipalWrittenOff())
            .setPaymentDueDate(loan.getPaymentDueDate())
            .setExtensions(extensions);

        loanPrepayment.ifPresent(pr -> {
            model.setPrePaymentInterestDue(pr.getInterestToPay());
            model.setPrePaymentTotalDue(pr.getTotalToPay());
        });

        return model;
    }

    @Override
    public Map<String, Object> testClientContext() {
        Map<String, Object> context = new HashMap<>();
        context.put(AlfaCmsModels.SCOPE_CLIENT, testClientModel());
        context.put(AlfaCmsModels.SCOPE_COMPANY, company());
        return context;
    }

    public ApplicationModel application(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);
        ApplicationModel model = new ApplicationModel();
        model.setOfferedPrincipal(application.getOfferedPrincipal());
        model.setShortApproveCode(application.getShortApproveCode());
        model.setLongApproveCode(application.getLongApproveCode());
        model.setCreditLimit(application.getCreditLimit());
        model.setNominalApr(application.getNominalApr().multiply(amount(12)));
        model.setEffectiveApr(application.getEffectiveApr());
        model.setNumber(application.getNumber());
        model.setDate(application.getSubmittedAt().toLocalDate());
        model.setOfferedPeriodCount(application.getOfferedPeriodCount());
        model.setOfferedInterest(application.getOfferedInterest());
        model.setOfferedTotal(application.getOfferedPrincipal().add(application.getOfferedInterest()));

        if (application.getOfferDate() != null) {
            model.setOfferDate(application.getOfferDate());
            model.setOfferMaturityDate(application.getOfferDate().plus(application.getOfferedPeriodCount(), DAY.toTemporalUnit()));
            // Only for LOC
            model.setFirstInvoiceDueDate(model.getOfferDate().plusMonths(1).withDayOfMonth(5));
        }
        return model;
    }

    public DebtModel debt(Long debtId) {
        Debt debt = dcService.get(debtId);
        //TODO
        SpecialLink link = specialLinkService.findLink(byClientId(debt.getClientId(), SpecialLinkType.ADD_PAYMENT))
            .orElseGet(() -> buildDebtSpecialLink(debt.getClientId()));

        DebtModel model = new DebtModel();
        model.setTotalDue(debt.getTotalDue());
        model.setSpecialLink(link);
        return model;
    }
    private SpecialLink buildDebtSpecialLink(long clientId) {
        AlfaSettings.NotificationSettings settings = settingsService.getJson(AlfaSettings.NOTIFICATION_SETTINGS, AlfaSettings.NotificationSettings.class);
        LocalDateTime expiresAt = now().plusDays(ofNullable(settings.getPaymentSpecialLinkExpiresInDays()).orElse(5));
        BuildLinkCommand command = new BuildLinkCommand()
            .setClientId(clientId)
            .setAutoLoginRequired(true)
            .setType(SpecialLinkType.ADD_PAYMENT)
            .setReusable(true)
            .setExpiresAt(expiresAt);
        return specialLinkService.buildLink(command);
    }

    public Map<String, Object> clientContext(Long clientId) {
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_CLIENT, client(clientId));
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_AUTO_LOGIN, autoLogin(clientId));
        return context;
    }

    public Map<String, Object> loanContext(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_CLIENT, client(loan.getClientId()));
        context.put(SCOPE_APPLICATION, application(loan.getApplicationId()));
        context.put(SCOPE_LOAN, loan(loan.getId()));
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_SCHEDULE, schedule(scheduleService.getCurrentContract(loanId).getId()));
        context.put(SCOPE_CALCULATION_STRATEGY, calculationStrategyByLoan(loanId));
        return context;
    }

    public Map<String, Object> applicationContext(Long applicationId) {
        LoanApplication application = loanApplicationService.get(applicationId);
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_CLIENT, client(application.getClientId()));
        context.put(SCOPE_APPLICATION, application(applicationId));
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_CALCULATION_STRATEGY, calculationStrategyByApplication(applicationId));
        return context;
    }

    public Map<String, Object> specialLink(Long applicationId, SpecialLinkType type) {
        Long clientId = loanApplicationService.get(applicationId).getClientId();
        Map<String, Object> context = applicationContext(applicationId);

        SpecialLink link = specialLinkService.findRequiredLink(byClientId(clientId, type));

        context.put(SCOPE_SPECIAL_LINK, link);
        return context;
    }

    public String autoLogin(Long clientId) {
        String token = webLoginService.login(clientId, Duration.ofHours(2));
        return webBaseUrl + "/auto-login?token=" + token;
    }

    public Map<String, Object> debtContext(Long debtId) {
        Debt debt = dcService.get(debtId);

        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_CLIENT, client(debt.getClientId()));
        context.put(SCOPE_LOAN, loan(debt.getLoanId()));
        context.put(SCOPE_DEBT, debt(debt.getId()));
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_SCHEDULE, schedule(scheduleService.getCurrentContract(debt.getLoanId()).getId()));
        context.put(SCOPE_CALCULATION_STRATEGY, calculationStrategyByLoan(debt.getLoanId()));
        return context;
    }

    public Map<String, Object> agreementContext(Long applicationId) {
        return applicationContext(applicationId);
    }

    public Map<String, Object> standardInformationContext(Long applicationId) {
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_STANDARD_INFORMATION, standardInformation());
        context.put(SCOPE_APPLICATION, application(applicationId));
        context.put(SCOPE_CALCULATION_STRATEGY, calculationStrategyByApplication(applicationId));
        return context;
    }

    public Map<String, Object> clientIncomingPaymentContext(Long clientId, ClientIncomingPaymentModel model) {
        ClientModel clientModel = client(clientId);
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_CLIENT, clientModel);
        context.put(SCOPE_CLIENT_INCOMING_PAYMENT, model);
        return context;
    }

    public Map<String, Object> clientRepaymentContext(Long loanId, ClientRepaymentModel model) {
        Loan loan = loanService.getLoan(loanId);
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_CLIENT, client(loan.getClientId()));
        context.put(SCOPE_COMPANY, company());
        context.put(SCOPE_LOAN, loan(loan.getId()));
        context.put(SCOPE_CLIENT_REPAYMENT, model);
        context.put(SCOPE_CALCULATION_STRATEGY, calculationStrategyByLoan(loanId));
        return context;
    }

    public ScheduleModel schedule(Long contractId) {
        Contract contract = scheduleService.getContract(contractId);
        List<Installment> installments = scheduleService.findInstallments(new InstallmentQuery().setContractId(contractId));
        List<ScheduleModel.InstallmentModel> installmentModels = installments.stream().map(this::mapInstallment).collect(Collectors.toList());
        BigDecimal totalScheduled = installments.stream().map(Installment::getTotalScheduled).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalDue = installments.stream().map(Installment::getTotalDue).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPaid = installments.stream().map(Installment::getTotalPaid).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal feeScheduled = installments.stream().map(Installment::getFeeScheduled).reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ScheduleModel()
            .setInstallments(installmentModels)
            .setStartDate(contract.getContractDate())
            .setTotalScheduled(totalScheduled)
            .setTotalDue(totalDue)
            .setTotalPaid(totalPaid)
            .setFeeScheduled(feeScheduled);
    }

    public UpsellModel upsellModel(Long applicationId) {
        Loan loan = loanService.getLoan(loanApplicationService.get(applicationId).getLoanId());

        ApplicationModel upsellModel = application(applicationId);
        ApplicationModel loanModel = application(loan.getApplicationId());

        return new UpsellModel()
            .setLoan(loanModel)
            .setUpsell(upsellModel)
            .setLoanNumber(loan.getNumber())
            .setTotalPrincipal(upsellModel.getOfferedPrincipal().add(loanModel.getOfferedPrincipal()))
            .setTotalInterest(upsellModel.getOfferedInterest().add(loanModel.getOfferedInterest()))
            .setGrandTotal(upsellModel.getOfferedTotal().add(loanModel.getOfferedTotal()))
            .setAverageNominalApr(upsellModel.getNominalApr().add(loanModel.getNominalApr()).divide(amount(2), BigDecimal.ROUND_HALF_UP))
            .setAverageEffectiveApr(upsellModel.getEffectiveApr().add(loanModel.getEffectiveApr()).divide(amount(2), BigDecimal.ROUND_HALF_UP));
    }

    public Map<String, Object> contactMe(ContactMeModel model) {
        return ImmutableMap.of(
            AlfaCmsModels.SCOPE_CONTACT_ME, model
        );
    }

    public CalculationStrategyModel calculationStrategyByLoan(Long loanId) {
        Loan loan = loanService.getLoan(loanId);
        return calculationStrategy(loan.getPenaltyStrategyId(), loan.getExtensionStrategyId(), loan.getInterestStrategyId());
    }

    public CalculationStrategyModel calculationStrategyByApplication(Long loanApplicationId) {
        LoanApplication application = loanApplicationService.get(loanApplicationId);
        return calculationStrategy(application.getPenaltyStrategyId(), application.getExtensionStrategyId(), application.getInterestStrategyId());
    }

    private CalculationStrategyModel calculationStrategy(Long penaltyStrategyId, Long extensionStrategyId, Long interestStrategyId) {
        CalculationStrategyModel model = new CalculationStrategyModel();
        if (penaltyStrategyId != null) {
            String key = strategyKey(penaltyStrategyId);
            model.setPenaltyStrategy(key);
            if (key.equals(CalculationStrategyModel.PENALTY_STRATEGY_A)) {
                model.setPenaltyStrategyAProperties((DailyPenaltyStrategyProperties) calculationStrategyService.getStrategyProperties(penaltyStrategyId));
            } else if (key.equals(CalculationStrategyModel.PENALTY_STRATEGY_AV)) {
                model.setPenaltyStrategyAVProperties((DpdPenaltyStrategyProperties) calculationStrategyService.getStrategyProperties(penaltyStrategyId));
            }
        }
        if (extensionStrategyId != null) {
            String key = strategyKey(extensionStrategyId);
            model.setExtensionStrategy(key);
            model.setExtensionStrategyDProperties((ExtensionStrategyProperties) calculationStrategyService.getStrategyProperties(extensionStrategyId));
        }
        if (interestStrategyId != null) {
            String key = strategyKey(interestStrategyId);
            model.setInterestStrategy(key);
            model.setInterestStrategyXProperties((MonthlyInterestStrategyProperties) calculationStrategyService.getStrategyProperties(interestStrategyId));
        }
        return model;
    }

    private String strategyKey(Long strategyId) {
        if (strategyId == null) {
            return null;
        }
        CalculationStrategyEntity entity = calculationStrategyRepository.getRequired(strategyId);
        return new CalculationStrategyCmsItemKey(entity.getStrategyType(), entity.getCalculationType()).get();
    }

    private StandardInformationModel standardInformation() {
        PaydayProductSettings settings = productService.getSettings(AlfaConstants.PRODUCT_ID, PaydayProductSettings.class);
        PaydayOfferSettings offerSettings = settings.getPublicOfferSettings();
        StandardInformationModel model = new StandardInformationModel();
        model.setMaxProductAmount(offerSettings.getMaxAmount());
        model.setMaxProductPeriodCount(offerSettings.getMaxTerm());
        return model;
    }

    private BigDecimal toPerDayPercentage(BigDecimal value) {
        return value.divide(new BigDecimal(30), 3, BigDecimal.ROUND_HALF_UP);
    }

    public ResetPasswordModel resetPassword(String tokenPath) {
        ResetPasswordModel model = new ResetPasswordModel();
        model.setUrl(this.webBaseUrl + tokenPath);
        return model;
    }

    @Override
    public String getWebBaseUrl() {
        return webBaseUrl;
    }

    @Override
    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    private ScheduleModel.InstallmentModel mapInstallment(Installment source) {
        return new ScheduleModel.InstallmentModel()
            .setInstallmentSequence(source.getInstallmentSequence())
            .setInstallmentNumber(source.getInstallmentNumber())
            .setPeriodFrom(source.getPeriodFrom())
            .setPeriodTo(source.getPeriodTo())
            .setDueDate(source.getDueDate())
            .setCloseDate(source.getCloseDate())
            .setGenerateInvoiceOnDate(source.getGenerateInvoiceOnDate())
            .setStatus(source.getStatus())
            .setStatusDetail(source.getStatusDetail())
            .setTotalScheduled(source.getTotalScheduled())
            .setTotalPaid(source.getTotalPaid())
            .setTotalDue(source.getTotalDue())
            .setPrincipalScheduled(source.getPrincipalScheduled())
            .setInterestScheduled(source.getInterestScheduled())
            .setPenaltyScheduled(source.getPenaltyScheduled())
            .setFeeScheduled(source.getFeeScheduled());
    }

    private static Optional<String> findInJson(DocumentContext json, String jsonPath) {
        Object result = json.read(jsonPath);
        if (result == null) {
            return Optional.empty();
        }
        if (result instanceof JSONArray) {
            JSONArray results = (JSONArray) result;
            if (!results.isEmpty()) {
                return Optional.of(results.get(0).toString());
            }
        } else {
            return Optional.of(String.valueOf(result));
        }
        log.info("Couldn't find `{}` in Json", jsonPath);
        return Optional.empty();
    }

    public InstantorModel instantor(Long clientId, Long instantorResponseId) {
        String jsonPayload = instantorService.getJsonPayload(instantorResponseId);
        Configuration configuration = Configuration.defaultConfiguration();
        configuration = configuration.addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL, Option.SUPPRESS_EXCEPTIONS);

        DocumentContext payload = JsonPath.using(configuration).parse(jsonPayload);

        InstantorModel model = new InstantorModel();
        model.setRequestId(payload.read("instantorRequestId"));
        findInJson(payload, "processFinishedTime")
            .ifPresent(reportTime -> model.setDateTime(ZonedDateTime.parse(reportTime).toLocalDateTime()));
        model.setResult(payload.read("processStatus"));

        List<String> addresses = npeSafe(() -> {
            List<String> data = Lists.newArrayList();
            int addressLength = payload.read("$.userDetails.address.length()");
            for (int i = 0; i < addressLength; i++) {
                String address = payload.read("$.userDetails.address[" + i + "]");
                data.add(address);
            }
            return data;
        }).orElse(Lists.newArrayList());

        List<String> phones = npeSafe(() -> {
            List<String> data = Lists.newArrayList();
            int phoneLength = payload.read("$.userDetails.phone.length()");
            for (int i = 0; i < phoneLength; i++) {
                String phone = payload.read("$.userDetails.phone[" + i + "]");
                data.add(phone);
            }
            return data;
        }).orElse(Lists.newArrayList());

        List<String> emails = npeSafe(() -> {
            List<String> data = Lists.newArrayList();
            int emailLength = payload.read("$.userDetails.email.length()");
            for (int i = 0; i < emailLength; i++) {
                String email = payload.read("$.userDetails.email[" + i + "]");
                data.add(email);
            }
            return data;
        }).orElse(Lists.newArrayList());

        InstantorModel.InstantorData instantorData = new InstantorModel.InstantorData()
            .setPhoneNumber(phones)
            .setEmail(emails)
            .setAddress(addresses);

        findInJson(payload, "bankInfo.name")
            .ifPresent(instantorData::setBankName);
        findInJson(payload, "userDetails.name")
            .ifPresent(instantorData::setClientName);
        findInJson(payload, "userDetails.personalIdentifier.value")
            .ifPresent(instantorData::setPersonalCode);
        findInJson(payload, "processFinishedTime")
            .ifPresent(reportTime -> instantorData.setReportTime(ZonedDateTime.parse(reportTime).toLocalDateTime()));

        instantorData.setPeriodFrom(null);
        instantorData.setPeriodTo(null);


        List<InstantorModel.Account> accounts = npeSafe(() -> {
            List<InstantorModel.Account> data = Lists.newArrayList();
            int accountsLength = payload.read("$.accountList.length()");
            for (int i = 0; i < accountsLength; i++) {
                InstantorModel.Account acc = new InstantorModel.Account()
                    .setNumber(payload.read("$.accountList[" + i + "].number"))
                    .setIban(payload.read("$.accountList[" + i + "].iban"))
                    .setHolder(payload.read("$.accountList[" + i + "].holderName"));
                findInJson(payload, "$.accountList[" + i + "].balance")
                    .ifPresent(balance -> acc.setBalance(new BigDecimal(balance)));
                Object transactions = payload.read("$.accountList[" + i + "].transactionList");
                if (transactions instanceof JSONArray) {
                    acc.setTransactionCount(((JSONArray) transactions).size());
                } else {
                    acc.setTransactionCount(0);
                }
                data.add(acc);
            }
            return data;
        }).orElse(Lists.newArrayList());
        instantorData.getAccounts().addAll(accounts);

        model.setInstantor(instantorData);

        Client client = clientService.get(clientId);
        InstantorModel.ClientData clientData = new InstantorModel.ClientData()
            .setClientName(client.getFullName())
            .setPersonalCode(client.getDocumentNumber())
            .setPhoneNumber(client.getPhone())
            .setEmail(client.getEmail());

        clientAddressService.getClientPrimaryAddress(clientId, AlfaConstants.ADDRESS_TYPE_ACTUAL)
            .ifPresent(address -> clientData.setAddress(String.join(" ",
                address.getStreet(),
                address.getHouseNumber(),
                address.getHouseLetter(),
                address.getPostalCode(),
                address.getCity())));

        model.setClient(clientData);
        return model;
    }

    public DisbursementModel disbursement(Long disbursementId) {
        Disbursement disbursement = disbursementService.getDisbursement(disbursementId);
        Loan loan = loanService.getLoan(disbursement.getLoanId());
        Client client = clientService.get(disbursement.getClientId());
        Institution institution = institutionService.getInstitution(disbursement.getInstitutionId());
        DisbursementModel model = new DisbursementModel();
        model.setReference(disbursement.getReference());
        model.setStatus(disbursement.getStatusDetail().name());
        model.setDisbursementId(disbursement.getId());
        model.setLoanNumber(loan.getNumber());
        model.setPaymentOrigin(institution.getName());
        model.setRecipient(client.getFullName());
        model.setIban(client.getAccountNumber());
        model.setAmount(disbursement.getAmount());
        model.setCreatedAt(disbursement.getExportedAt());
        model.setUpdatedAt(disbursement.getExportedAt());
        return model;
    }

    public Map<String, Object> disbursementContext(Long disbursementId) {
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_DISBURSEMENT, disbursement(disbursementId));
        return context;
    }

    public Map<String, Object> instantorContext(Long clientId, Long instantorResponseId) {
        Map<String, Object> context = new HashMap<>();
        context.put(SCOPE_INSTANTOR_REPORT, instantor(clientId, instantorResponseId));
        return context;
    }
}
