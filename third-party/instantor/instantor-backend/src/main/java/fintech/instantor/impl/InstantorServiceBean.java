package fintech.instantor.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.JsonUtils;
import fintech.PredicateBuilder;
import fintech.Validate;
import fintech.instantor.InstantorService;
import fintech.instantor.db.Entities;
import fintech.instantor.db.InstantorResponseEntity;
import fintech.instantor.db.InstantorResponseRepository;
import fintech.instantor.db.InstantorTransactionEntity;
import fintech.instantor.db.InstantorTransactionRepository;
import fintech.instantor.events.InstantorResponseFailed;
import fintech.instantor.events.InstantorResponseProcessed;
import fintech.instantor.json.common.InstantorCommonResponse;
import fintech.instantor.model.*;
import fintech.instantor.parser.InstantorParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static fintech.instantor.db.Entities.response;

@Slf4j
@Service
@Transactional
public class InstantorServiceBean implements InstantorService {

    private final InstantorResponseRepository responseRepository;

    private final InstantorTransactionRepository transactionRepository;

    private final ApplicationEventPublisher eventPublisher;

    private final JPAQueryFactory queryFactory;

    private InstantorParser instantorParser;

    public InstantorServiceBean(InstantorResponseRepository responseRepository, InstantorTransactionRepository transactionRepository, ApplicationEventPublisher eventPublisher, JPAQueryFactory queryFactory) {
        this.responseRepository = responseRepository;
        this.transactionRepository = transactionRepository;
        this.eventPublisher = eventPublisher;
        this.queryFactory = queryFactory;
    }

    @Override
    public Long saveResponse(SaveInstantorResponseCommand command) {
        log.info("Saving instantor request: [{}]", command);
        InstantorResponseEntity entity = instantorParser().parseResponse(command);
        if (entity.getStatus() == InstantorResponseStatus.OK) {
            queryFactory.update(response).set(response.latest, false).where(response.clientId.eq(entity.getClientId()).and(response.latest.isTrue())).execute();
        }
        return responseRepository.saveAndFlush(entity).getId();
    }

    @Override
    public void processResponse(Long responseId) {
        log.info("Processing Instantor response [{}]", responseId);
        InstantorResponseEntity entity = responseRepository.getRequired(responseId);
        Validate.isTrue(entity.getProcessingStatus() == InstantorProcessingStatus.PENDING, "Invalid processing status [%s] of instantor response [%s]", entity.getProcessingStatus(), responseId);
        if (entity.getStatus() == InstantorResponseStatus.OK) {
            InstantorCommonResponse request = JsonUtils.readValue(entity.getPayloadJson(), InstantorCommonResponse.class);
            log.info("Instantor response [{}] processed", responseId);
            eventPublisher.publishEvent(new InstantorResponseProcessed(entity.getId(), entity.getClientId(), request));
        } else {
            log.info("Failed to process Instantor response [{}]", responseId);
            if (entity.getClientId() != null) {
                eventPublisher.publishEvent(new InstantorResponseFailed(entity.getId(), entity.getClientId()));
            }
        }
        entity.setProcessingStatus(InstantorProcessingStatus.PROCESSED);
    }

    @Override
    public void processingFailed(Long responseId) {
        InstantorResponseEntity entity = responseRepository.getRequired(responseId);
        entity.setProcessingStatus(InstantorProcessingStatus.PROCESSING_ERROR);
    }

    @Override
    public Optional<InstantorResponse> findLatest(InstantorResponseQuery query) {
        return responseRepository.findFirst(toPredicate(query).allOf(), response.id.desc())
            .map(InstantorResponseEntity::toValueObject);
    }


    @Override
    public InstantorResponse getResponse(Long responseId) {
        InstantorResponseEntity response = responseRepository.getRequired(responseId);
        return response.toValueObject();
    }

    @Override
    public void saveManualTransactionCategory(Long transactionId, String category) {
        InstantorTransactionEntity entity = transactionRepository.getRequired(transactionId);
        entity.setCategory(category);
    }

    @Override
    public void saveNordigenTransactionCategory(Long transactionId, String category) {
        InstantorTransactionEntity entity = transactionRepository.getRequired(transactionId);
        entity.setNordigenCategory(category);
    }

    @Override
    public List<InstantorTransaction> findTransactions(InstantorTransactionQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getResponseId() != null) {
            predicates.add(Entities.transaction.response.id.eq(query.getResponseId()));
        }
        if (query.getClientId() != null) {
            predicates.add(Entities.transaction.clientId.eq(query.getClientId()));
        }
        if (query.getDateFrom() != null) {
            predicates.add(Entities.transaction.date.goe(query.getDateFrom()));
        }
        if (query.getDateTo() != null) {
            predicates.add(Entities.transaction.date.loe(query.getDateTo()));
        }
        if (query.getAccountNumber() != null) {
            predicates.add(Entities.transaction.accountNumber.eq(query.getAccountNumber()));
        }
        return transactionRepository.findAll(ExpressionUtils.allOf(predicates)).stream()
            .map(InstantorTransactionEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public void updateAccountData(Long responseId, String bankAccountNumber) {
        InstantorResponseEntity response = responseRepository.getRequired(responseId);
        updateVerificationName(response, bankAccountNumber);
        updateAccountAttributes(response, bankAccountNumber);
        responseRepository.saveAndFlush(response);
    }

    private PredicateBuilder toPredicate(InstantorResponseQuery query) {
        return new PredicateBuilder()
            .addIfPresent(query.getClientId(), response.clientId::eq)
            .addIfPresent(query.getCreatedAfter(), response.createdAt::after)
            .addIfPresent(query.getProcessingStatus(), response.processingStatus::in)
            .addIfPresent(query.getResponseStatus(), response.status::eq)
            .addIfPresent(query.getAccountNumber(), response.accountNumbers::contains);
    }

    private void updateVerificationName(InstantorResponseEntity response, String bankAccountNumber) {
        String newName = instantorParser.getNameForVerification(response, bankAccountNumber);
        if (newName != null) {
            response.setNameForVerification(newName);
        }
    }

    private void updateAccountAttributes(InstantorResponseEntity response, String bankAccountNumber) {
        Map<String, String> attributes = instantorParser.parseAccountAttributes(response, bankAccountNumber);
        response.getAttributes().putAll(attributes);
    }

    @Override
    public String getJsonPayload(Long responseId) {
        return responseRepository.getRequired(responseId).getPayloadJson();
    }

    private InstantorParser instantorParser() {
        Validate.isTrue(instantorParser != null, "No instantor parser set!!!");
        return instantorParser;
    }

    @Override
    public void setInstantorParser(InstantorParser instantorParser) {
        this.instantorParser = instantorParser;
    }
}
