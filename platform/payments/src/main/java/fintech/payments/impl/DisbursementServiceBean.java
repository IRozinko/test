package fintech.payments.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.payments.DisbursementService;
import fintech.payments.InstitutionService;
import fintech.payments.commands.AddDisbursementCommand;
import fintech.payments.db.DisbursementEntity;
import fintech.payments.db.DisbursementRepository;
import fintech.payments.events.DisbursementCancelledEvent;
import fintech.payments.events.DisbursementErrorOccurredEvent;
import fintech.payments.events.DisbursementExportErrorOccurredEvent;
import fintech.payments.events.DisbursementExportedEvent;
import fintech.payments.events.DisbursementPendingEvent;
import fintech.payments.events.DisbursementSettledEvent;
import fintech.payments.events.DisbursementVoidedEvent;
import fintech.payments.model.Disbursement;
import fintech.payments.model.DisbursementExportResult;
import fintech.payments.model.DisbursementStatusDetail;
import fintech.transactions.TransactionService;
import fintech.transactions.TransactionType;
import lombok.AllArgsConstructor;
import fintech.payments.model.Institution;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static fintech.payments.db.Entities.disbursement;
import static fintech.payments.model.DisbursementStatusDetail.ERROR;
import static fintech.payments.model.DisbursementStatusDetail.PENDING;
import static org.apache.commons.lang3.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Component
@Transactional
@AllArgsConstructor
class DisbursementServiceBean implements DisbursementService {

    private final DisbursementRepository repository;
    private final InstitutionService institutionService;
    private final TransactionService transactionService;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Long add(@Valid AddDisbursementCommand command) {
        log.info("Saving disbursement: [{}]", command);

        Institution institution = institutionService.getInstitution(command.getInstitutionId());
        DisbursementEntity entity = new DisbursementEntity();
        entity.setDisbursementType(command.getDisbursementType());
        entity.setClientId(command.getClientId());
        entity.setLoanId(command.getLoanId());
        entity.setApplicationId(command.getApplicationId());
        entity.setInstitutionId(command.getInstitutionId());
        entity.setInstitutionAccountId(command.getInstitutionAccountId());
        entity.setAmount(command.getAmount());
        entity.setValueDate(command.getValueDate());
        entity.setReference(command.getReference());
        entity.setApiExport(institution.isApiExport());

        entity = repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementPendingEvent(entity.toValueObject()));
        return entity.getId();
    }

    @Override
    public void exported(long disbursementId, LocalDateTime when, DisbursementExportResult exportResult) {
        log.info("Disbursement [{}] exported, result [{}]", disbursementId, exportResult);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.isTrue(PENDING.equals(entity.getStatusDetail()) ||
            ERROR.equals(entity.getStatusDetail()), "Can export only PENDING or ERROR disbursement: %s", entity);
        entity.setExportedCloudFileId(exportResult.getFileId());
        entity.setExportedFileName(exportResult.getOriginalFileName());
        entity.open(DisbursementStatusDetail.EXPORTED);
        entity.setExportedAt(when);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementExportedEvent(entity.toValueObject()));
    }

    @Override
    public void exportError(long disbursementId, String error) {
        log.info("Disbursement export error occurred [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.validState(DisbursementStatusDetail.EXPORTED.equals(entity.getStatusDetail()), "Can mark as error occurred only EXPORTED disbursement: %s", entity);
        entity.close(DisbursementStatusDetail.EXPORT_ERROR);
        entity.setError(error);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementExportErrorOccurredEvent(entity.toValueObject()));
    }

    @Override
    public void error(long disbursementId, String error) {
        log.info("Disbursement error occurred [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.validState(DisbursementStatusDetail.EXPORTED.equals(entity.getStatusDetail()), "Can mark as error occurred only EXPORTED disbursement: %s", entity);
        entity.close(ERROR);
        entity.setError(error);
        transactionService.voidDisbursementTransaction(disbursementId, TransactionType.DISBURSEMENT);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementErrorOccurredEvent(entity.toValueObject()));
    }

    @Override
    public void settled(long disbursementId) {
        log.info("Disbursement settled [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.validState(DisbursementStatusDetail.EXPORTED.equals(entity.getStatusDetail()), "Can settle only EXPORTED disbursement: %s", entity);
        entity.close(DisbursementStatusDetail.SETTLED);
        entity.setSettledAt(TimeMachine.now());
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementSettledEvent(entity.toValueObject()));
    }

    @Override
    public void revertSettled(long disbursementId) {
        log.info("Reverting disbursement settlement [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.validState(DisbursementStatusDetail.SETTLED.equals(entity.getStatusDetail()), "Can revert SETTLED disbursement: %s", entity);
        entity.open(DisbursementStatusDetail.EXPORTED);
        entity.setSettledAt(null);
        repository.saveAndFlush(entity);
    }

    @Override
    public void cancel(long disbursementId, String reason) {
        log.info("Cancelling disbursement [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.isTrue(PENDING == entity.getStatusDetail(), "Can cancel only PENDING disbursement: %s", entity);
        entity.close(DisbursementStatusDetail.CANCELLED);
        entity.setError(reason);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementCancelledEvent(entity.toValueObject()));
    }

    @Override
    public void voidDisbursement(long disbursementId, String reason) {
        log.info("Voiding disbursement [{}]", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.isTrue(PENDING == entity.getStatusDetail() ||
            ERROR == entity.getStatusDetail(), "Can cancel only PENDING or ERROR disbursement: %s", entity);
        entity.close(DisbursementStatusDetail.VOIDED);
        entity.setError(reason);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementVoidedEvent(entity.toValueObject()));
    }

    @Override
    public void pending(long disbursementId) {
        log.info("Marking disbursement [{}] as pending", disbursementId);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.isTrue(DisbursementStatusDetail.INVALID == entity.getStatusDetail(), "Can mark pending only INVALID disbursement: %s", entity);
        entity.open(PENDING);
        repository.saveAndFlush(entity);
        eventPublisher.publishEvent(new DisbursementPendingEvent(entity.toValueObject()));
    }

    @Override
    public void invalid(long disbursementId, String error) {
        log.info("Setting disbursement [{}] status to invalid, error [{}]", disbursementId, error);
        DisbursementEntity entity = repository.getRequired(disbursementId);
        Validate.isTrue(PENDING == entity.getStatusDetail() ||
                DisbursementStatusDetail.INVALID == entity.getStatusDetail() ||
                DisbursementStatusDetail.EXPORT_ERROR == entity.getStatusDetail(),
            "Can mark disbursement invalid only PENDING or with error status detail: %s", entity);
        entity.close(DisbursementStatusDetail.INVALID);
        entity.setError(error);
        eventPublisher.publishEvent(new DisbursementErrorOccurredEvent(entity.toValueObject()));
    }

    @Override
    public Disbursement getDisbursement(long disbursementId) {
        return repository.getRequired(disbursementId).toValueObject();
    }

    @Override
    public List<Disbursement> findDisbursements(DisbursementQuery query) {
        List<Predicate> predicates = getPredicates(query);
        return repository.findAll(ExpressionUtils.allOf(predicates)).stream().map(DisbursementEntity::toValueObject).collect(Collectors.toList());
    }

    private List<Predicate> getPredicates(DisbursementQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getInstitutionId() != null) {
            predicates.add(disbursement.institutionId.eq(query.getInstitutionId()));
        }
        if (query.getInstitutionAccountId() != null) {
            predicates.add(disbursement.institutionAccountId.eq(query.getInstitutionAccountId()));
        }
        if (query.getStatusDetail() != null) {
            predicates.add(disbursement.statusDetail.eq(query.getStatusDetail()));
        }
        if (query.getLoanId() != null) {
            predicates.add(disbursement.loanId.eq(query.getLoanId()));
        }
        if (query.getClientId() != null) {
            predicates.add(disbursement.clientId.eq(query.getClientId()));
        }
        if (isNotBlank(query.getReference())) {
            predicates.add(disbursement.reference.eq(query.getReference()));
        }
        if (isNotBlank(query.getFileName())) {
            predicates.add(disbursement.exportedFileName.contains(query.getFileName()));
        }
        if (query.getAmount() != null) {
            predicates.add(disbursement.amount.eq(query.getAmount()));
        }
        return predicates;
    }

    @Override
    public Optional<Disbursement> getOptional(DisbursementQuery query) {
        return repository.getOptional(ExpressionUtils.allOf(getPredicates(query))).map(DisbursementEntity::toValueObject);
    }

    @Override
    public String generateReference(String prefix, String suffix, int length) {
        return IntStream.range(1, 100)
            .mapToObj(i -> prefix + randomNumeric(length).toLowerCase() + suffix)
            .filter(ref -> !repository.exists(disbursement.reference.eq(ref)))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("Failed to generate unique disbursement reference"));
    }
}
