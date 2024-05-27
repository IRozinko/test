package fintech.lending.core.application.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.querydsl.core.types.Predicate;
import fintech.BigDecimalUtils;
import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.lending.core.application.LoanApplication;
import fintech.lending.core.application.LoanApplicationQuery;
import fintech.lending.core.application.LoanApplicationService;
import fintech.lending.core.application.LoanApplicationStatus;
import fintech.lending.core.application.LoanApplicationStatusDetail;
import fintech.lending.core.application.commands.*;
import fintech.lending.core.application.db.LoanApplicationEntity;
import fintech.lending.core.application.db.LoanApplicationRepository;
import fintech.lending.core.application.events.LoanApplicationApprovedEvent;
import fintech.lending.core.application.events.LoanApplicationCancelledEvent;
import fintech.lending.core.application.events.LoanApplicationOfferedEvent;
import fintech.lending.core.application.events.LoanApplicationRejectedEvent;
import fintech.lending.core.application.events.LoanApplicationSubmittedEvent;
import fintech.lending.core.creditlimit.CreditLimit;
import fintech.lending.core.creditlimit.CreditLimitService;
import fintech.lending.core.db.Entities;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;
import static fintech.BigDecimalUtils.amount;
import static fintech.lending.core.application.LoanApplicationStatusDetail.APPROVED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.CANCELLED;
import static fintech.lending.core.application.LoanApplicationStatusDetail.PENDING;
import static fintech.lending.core.application.LoanApplicationStatusDetail.REJECTED;

@Slf4j
@Component
class LoanApplicationServiceBean implements LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final CreditLimitService creditLimitService;

    @Autowired
    public LoanApplicationServiceBean(LoanApplicationRepository loanApplicationRepository, ApplicationEventPublisher eventPublisher, CreditLimitService creditLimitService) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.eventPublisher = eventPublisher;
        this.creditLimitService = creditLimitService;
    }

    @Transactional
    @Override
    public LoanApplication get(Long applicationId) {
        LoanApplicationEntity entity = loanApplicationRepository.getRequired(applicationId);
        return entity.toValueObject();
    }

    @Transactional
    @Override
    public Optional<LoanApplication> findByUuid(String uuid) {
        Validate.notBlank(uuid, "Invalid uuid");
        return loanApplicationRepository.getOptional(Entities.loanApplication.uuid.eq(uuid)).map(LoanApplicationEntity::toValueObject);
    }

    @Transactional
    @Override
    public Long submit(SubmitLoanApplicationCommand command) {
        log.info("Submitting loan application: [{}]", command);
        Validate.isPositive(command.getPrincipal(), "Invalid application requested principal: [%s]", command.getPrincipal());

        Optional<LoanApplicationEntity> existingOpen = loanApplicationRepository.getOptional(
            Entities.loanApplication.clientId.eq(command.getClientId())
                .and(Entities.loanApplication.status.eq(LoanApplicationStatus.OPEN)));
        if (existingOpen.isPresent()) {
            log.info("Found existing open loan application [{}]", existingOpen.get());
            throw new IllegalStateException("Can't submit new application, there is already open one");
        }

        CreditLimit creditLimit = creditLimitService.getClientLimit(command.getClientId(),
            command.getSubmittedAt().toLocalDate())
            .orElse(CreditLimit.zeroLimit(command.getClientId()));

        LoanApplicationEntity application = new LoanApplicationEntity();
        application.setType(command.getType());
        application.setNumber(command.getApplicationNumber());
        application.setOfferedPrincipal(amount(0));

        application.setRequestedPrincipal(command.getPrincipal());
        application.setSubmittedAt(command.getSubmittedAt());
        application.setClientId(command.getClientId());
        application.setProductId(command.getProductId());
        application.open(command.getLoanApplicationStatusDetail() == null ? PENDING : command.getLoanApplicationStatusDetail());
        application.setLoansPaid(command.getLoansPaid());
        application.setIpAddress(command.getIpAddress());
        application.setIpCountry(command.getIpCountry());
        application.setReferer(command.getReferer());
        application.setCreditLimit(creditLimit.getLimit());
        application.setInvoicePaymentDay(command.getInvoiceDay());
        application.setLoanId(command.getLoanId());
        application.setShortApproveCode(command.getShortApproveCode());
        application.setLongApproveCode(command.getLongApproveCode());
        application.setUuid(UUID.randomUUID().toString());
        application.setRequestedPeriodCount(command.getPeriodCount());
        application.setRequestedPeriodUnit(command.getPeriodUnit());
        application.setRequestedInterestDiscountPercent(command.getInterestDiscountPercent());
        application.setSourceType(command.getSourceType());
        application.setSourceName(command.getSourceName());
        application.setDiscountId(command.getDiscountId());
        application.setPromoCodeId(command.getPromoCodeId());
        application.setExtensionStrategyId(command.getExtensionStrategyId());
        application.setFeeStrategyId(command.getFeeStrategyId());
        application.setInterestStrategyId(command.getInterestStrategyId());
        application.setPenaltyStrategyId(command.getPenaltyStrategyId());
        application = loanApplicationRepository.saveAndFlush(application);

        eventPublisher.publishEvent(new LoanApplicationSubmittedEvent(application.toValueObject()));

        return application.getId();
    }

    @Transactional
    @Override
    public void updateInquiry(UpdateInquiryCommand command) {
        log.info("Updating loan application inquiry: [{}]", command);
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getApplicationId());
        assertApplicationIsPending(application);
        Validate.isTrue(BigDecimalUtils.isZero(application.getOfferedPrincipal()), "Can not update loan application inquiry, this is already offered principal: [%s]", application);
        application.setRequestedPrincipal(command.getRequestedPrincipal());
        application.setRequestedPeriodCount(command.getTermInMonth());
    }

    @Transactional
    @Override
    public void attachWorkflow(AttachWorkflowCommand command) {
        log.info("Attaching workflow to loan application: [{}]", command);
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getApplicationId());
        application.setWorkflowId(command.getWorkflowId());
        application.setStatus(LoanApplicationStatus.OPEN);
        application.setStatusDetail(command.getLoanApplicationStatusDetail() != null ? command.getLoanApplicationStatusDetail() : PENDING);
    }

    @Transactional
    @Override
    public void updateStatusDetail(Long applicationId, String statusDetail) {
        LoanApplicationEntity application = loanApplicationRepository.getRequired(applicationId);
        assertApplicationIsPending(application);
        log.info("Updated loan application {} with status detail '{}'", application.getNumber(), statusDetail);
        application.setStatusDetail(statusDetail);
    }

    @Transactional
    @Override
    public void reject(Long applicationId, String reason) {
        LoanApplicationEntity application = loanApplicationRepository.getRequired(applicationId);
        log.info("Rejecting loan application: [{}] [{}]", application, reason);
        assertApplicationIsPending(application);
        application.close(REJECTED, TimeMachine.today(), reason);
        eventPublisher.publishEvent(new LoanApplicationRejectedEvent(application.toValueObject()));
    }

    @Transactional
    @Override
    public void cancel(Long applicationId, String reason) {
        LoanApplicationEntity application = loanApplicationRepository.getRequired(applicationId);
        log.info("Cancelling loan application: [{}] [{}]", application, reason);
        Validate.isTrue(LoanApplicationStatusDetail.isPending(application.getStatusDetail())
            || APPROVED.equals(application.getStatusDetail()), "Can not update application in status [%s]", application);
        application.close(CANCELLED, TimeMachine.today(), reason);
        eventPublisher.publishEvent(new LoanApplicationCancelledEvent(application.toValueObject()));
    }

    @Transactional
    @Override
    public void saveScore(SaveScoreCommand command) {
        log.info("Saving score: [{}]", command);
        Validate.notNull(command.getScore(), "Null score");
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getApplicationId());
        Validate.isTrue(application.getStatus() == LoanApplicationStatus.OPEN, "Can not update score, application is not open: [{}]", application);
        application.setScore(command.getScore());
        application.setScoreSource(command.getScoreSource());
    }

    @Transactional
    @Override
    public void saveCreditLimit(SaveCreditLimitCommand command) {
        log.info("Saving credit limit: [{}]", command);

        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getId());
        Validate.isTrue(application.getStatus() == LoanApplicationStatus.OPEN, "Can not update credit limit, application is not open: [{}]", application);

        application.setCreditLimit(command.getLimit());
    }

    @Transactional
    @Override
    public void updateInterestRate(UpdateLoanApplicationInterestRateCommand command) {
        log.info("Updating loan application interest rate: [{}]", command);
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getApplicationId());
        Validate.isTrue(application.getStatus() == LoanApplicationStatus.OPEN, "Can not update credit limit, application is not open: [{}]", application);

        application.setNominalApr(command.getNominalInterestRate());
        application.setEffectiveApr(command.getEffectiveInterestRate());
    }

    @Transactional
    @Override
    public void saveParams(SaveParamsCommand command) {
        log.info("Saving params: [{}]", command);

        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getId());

        Map<String, String> params = Optional.ofNullable(application.getParams())
            .map(json -> JsonUtils.readValue(json, new TypeReference<Map<String, String>>() {
            }))
            .orElseGet(Maps::newHashMap);
        params.put(command.getKey(), command.getValue());
        application.setParams(JsonUtils.writeValueAsString(params));
    }

    @Transactional
    @Override
    public Map<String, String> getParams(Long applicationId) {
        LoanApplicationEntity application = loanApplicationRepository.getRequired(applicationId);

        return Optional.ofNullable(application.getParams())
            .map(json -> JsonUtils.readValue(json, new TypeReference<Map<String, String>>() {
            }))
            .orElseGet(Maps::newHashMap);
    }

    @Override
    public List<LoanApplication> find(LoanApplicationQuery query) {
        List<LoanApplicationEntity> entities = loanApplicationRepository.findAll(allOf(toPredicates(query)), Entities.loanApplication.id.desc());
        return entities.stream().map(LoanApplicationEntity::toValueObject).collect(Collectors.toList());
    }

    @Override
    public Optional<LoanApplication> findLatest(LoanApplicationQuery query) {
        Page<LoanApplicationEntity> page = loanApplicationRepository.findAll(allOf(toPredicates(query)), new QPageRequest(0, 1, Entities.loanApplication.id.desc()));
        return page.getContent().stream().findFirst().map(LoanApplicationEntity::toValueObject);
    }

    @Override
    public Optional<LoanApplication> findFirst(LoanApplicationQuery query) {
        Page<LoanApplicationEntity> page = loanApplicationRepository.findAll(allOf(toPredicates(query)), new QPageRequest(0, 1, Entities.loanApplication.id.asc()));
        return page.getContent().stream().findFirst().map(LoanApplicationEntity::toValueObject);
    }

    private List<Predicate> toPredicates(LoanApplicationQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(Entities.loanApplication.clientId.eq(query.getClientId()));
        }
        if (query.getApplicationId() != null) {
            predicates.add(Entities.loanApplication.id.eq(query.getApplicationId()));
        }
        if (query.getLongApproveCode() != null) {
            predicates.add(Entities.loanApplication.longApproveCode.eq(query.getLongApproveCode()));
        }
        if (query.getShortApproveCode() != null) {
            predicates.add(Entities.loanApplication.shortApproveCode.eq(query.getShortApproveCode()));
        }
        if (query.getSubmittedDateFrom() != null) {
            predicates.add(Entities.loanApplication.submittedAt.goe(LocalDateTime.of(query.getSubmittedDateFrom(), LocalTime.MIN)));
        }
        if (query.getSubmittedDateTo() != null) {
            predicates.add(Entities.loanApplication.submittedAt.loe(LocalDateTime.of(query.getSubmittedDateFrom(), LocalTime.MAX)));
        }
        if (!query.getStatuses().isEmpty()) {
            predicates.add(Entities.loanApplication.status.in(query.getStatuses()));
        }
        if (!query.getStatusDetails().isEmpty()) {
            predicates.add(Entities.loanApplication.statusDetail.in(query.getStatusDetails()));
        }
        if (!query.getTypes().isEmpty()) {
            predicates.add(Entities.loanApplication.type.in(query.getTypes()));
        }
        return predicates;
    }

    @Transactional
    @Override
    public void updateOffer(LoanApplicationOfferCommand command) {
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getId());
        log.info("Updating loan application [{}] offer: [{}]", application, command);
        assertApplicationIsPending(application);
        application.setOfferedPrincipal(command.getPrincipal());
        application.setOfferedInterest(command.getInterest());
        application.setOfferedInterestDiscountPercent(command.getInterestDiscountPercent());
        application.setOfferedInterestDiscountAmount(command.getInterestDiscountAmount());
        application.setOfferDate(Validate.notNull(command.getOfferDate(), "No offer date"));
        application.setNominalApr(command.getNominalApr());
        application.setEffectiveApr(command.getEffectiveApr());
        application.setOfferedPeriodCount(command.getPeriodCount());
        application.setOfferedPeriodUnit(command.getPeriodUnit());
        application.setDiscountId(command.getDiscountId());

        eventPublisher.publishEvent(new LoanApplicationOfferedEvent(application.toValueObject()));
    }


    @Override
    public void updateStrategies(UpdateLoanAppStrategiesCommand command) {
        final LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getApplicationId());
        Optional.ofNullable(command.getInterestStrategyId()).ifPresent(application::setInterestStrategyId);
        Optional.ofNullable(command.getPenaltyStrategyId()).ifPresent(application::setPenaltyStrategyId);
        Optional.ofNullable(command.getExtensionStrategyId()).ifPresent(application::setExtensionStrategyId);
        Optional.ofNullable(command.getFeeStrategyId()).ifPresent(application::setFeeStrategyId);
    }


    @Override
    public void approveOffer(ApproveOfferCommand command) {
        Validate.notNull(command.getOfferApprovedAt(), "Null offer approved at");
        Validate.notNull(command.getOfferApprovedBy(), "Null offer approved by");
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getId());
        Validate.isTrue(application.getStatus() == LoanApplicationStatus.OPEN, "Can't approve application in invalid status: [%s]", application);
        assertApplicationIsPending(application);
        application.setOfferApprovedAt(command.getOfferApprovedAt());
        application.setOfferApprovedBy(command.getOfferApprovedBy());
        application.setOfferApprovedFromIpAddress(command.getOfferApprovedFromIpAddress());
    }

    @Transactional
    @Override
    public void approve(ApproveLoanApplicationCommand command) {
        Validate.notNull(command.getApproveDate(), "Null approve date");
        LoanApplicationEntity application = loanApplicationRepository.getRequired(command.getId());
        Validate.isTrue(application.getStatus() == LoanApplicationStatus.OPEN, "Can't approve application in invalid status: [%s]", application);
        log.info("Application [{}] approved [{}]", application, command);
        assertApplicationIsPending(application);
        application.close(APPROVED, command.getApproveDate());
        Optional.ofNullable(command.getLoanId()).ifPresent(application::setLoanId);

        eventPublisher.publishEvent(new LoanApplicationApprovedEvent(application.toValueObject()));
    }

    @Transactional
    @Override
    public void addAttribute(Long applicationId, String key, String value) {
        LoanApplicationEntity loanApplicationEntity = loanApplicationRepository.getRequired(applicationId);
        loanApplicationEntity.getAttributes().put(key, value);
    }

    private void assertApplicationIsPending(LoanApplicationEntity application) {
        Validate.isTrue(LoanApplicationStatusDetail.isPending(application.getStatusDetail()), "Can not update application in status [%s]", application);
    }

}
