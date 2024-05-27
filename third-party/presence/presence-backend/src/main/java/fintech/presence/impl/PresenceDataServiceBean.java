package fintech.presence.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.presence.AddOrUpdateOutboundLoadCommand;
import fintech.presence.AddOrUpdateOutboundLoadRecordCommand;
import fintech.presence.OutboundLoad;
import fintech.presence.OutboundLoadQuery;
import fintech.presence.OutboundLoadRecord;
import fintech.presence.OutboundLoadRecordQuery;
import fintech.presence.PhoneRecord;
import fintech.presence.PresenceDataService;
import fintech.presence.db.Entities;
import fintech.presence.db.OutboundLoadEntity;
import fintech.presence.db.OutboundLoadRecordEntity;
import fintech.presence.db.OutboundLoadRecordRepository;
import fintech.presence.db.OutboundLoadRepository;
import fintech.presence.db.PhoneRecordEntity;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Transactional
public class PresenceDataServiceBean implements PresenceDataService {

    private final OutboundLoadRepository outboundLoadRepository;
    private final OutboundLoadRecordRepository outboundLoadRecordRepository;
    private final JPAQueryFactory jpaQueryFactory;

    public PresenceDataServiceBean(OutboundLoadRepository outboundLoadRepository, OutboundLoadRecordRepository outboundLoadRecordRepository, JPAQueryFactory jpaQueryFactory) {
        this.outboundLoadRepository = outboundLoadRepository;
        this.outboundLoadRecordRepository = outboundLoadRecordRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public OutboundLoadRecord getRecord(Long recordId) {
        return outboundLoadRecordRepository.getRequired(recordId).toValueObject();
    }

    @Override
    public OutboundLoad getLoad(Long loadId) {
        return outboundLoadRepository.getRequired(loadId).toValueObject();
    }

    @Override
    public List<OutboundLoadRecord> findRecords(OutboundLoadRecordQuery query) {
        return jpaQueryFactory.select(Entities.outboundLoadRecordEntity)
            .from(Entities.outboundLoadEntity)
            .join(Entities.outboundLoadRecordEntity).on(Entities.outboundLoadRecordEntity.outboundLoad.eq(Entities.outboundLoadEntity))
            .where(ExpressionUtils.allOf(toPredicates(query)))
            .fetch()
            .stream()
            .map(OutboundLoadRecordEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public List<OutboundLoad> findLoads(OutboundLoadQuery query) {
        return outboundLoadRepository.findAll(ExpressionUtils.allOf(toPredicates(query)))
            .stream()
            .map(OutboundLoadEntity::toValueObject)
            .collect(Collectors.toList());
    }

    @Override
    public OutboundLoad addOrUpdateOutboundLoad(AddOrUpdateOutboundLoadCommand command) {
        OutboundLoadEntity entity = outboundLoadRepository.getFirstByServiceIdAndLoadId(command.getServiceId(), command.getLoadId())
            .orElse(new OutboundLoadEntity());

        entity.setLoadId(command.getLoadId());
        entity.setServiceId(command.getServiceId());
        entity.setStatus(command.getStatus());
        entity.setAddedAt(command.getAddedAt());
        entity.setDescription(command.getDescription());

        return outboundLoadRepository.save(entity).toValueObject();
    }

    @Override
    public OutboundLoadRecord addOrUpdateOutboundLoadRecord(AddOrUpdateOutboundLoadRecordCommand command) {
        OutboundLoadEntity entity = outboundLoadRepository.getRequired(command.getOutboundLoadId());
        OutboundLoadRecordEntity record;
        if (command.getOutboundLoadRecordId() != null) {
            record = outboundLoadRecordRepository.getRequired(command.getOutboundLoadRecordId());
        } else {
            record = outboundLoadRecordRepository.getFirstByOutboundLoadAndSourceId(entity, command.getSourceId())
                .orElseGet(() -> {
                    OutboundLoadRecordEntity newRecord = new OutboundLoadRecordEntity();
                    entity.getOutboundLoadRecords().add(newRecord);
                    return newRecord;
                });
        }

        record.setSourceId(command.getSourceId());
        record.setName(command.getName());
        record.setStatus(command.getStatus());
        record.setOutboundLoad(entity);
        record.setQualificationCode(command.getQualificationCode());

        if (command.getPhoneRecords() != null) {
            List<PhoneRecord> currentRecords = record.getPhoneRecords()
                .stream()
                .map(PhoneRecordEntity::toValueObject)
                .collect(Collectors.toList());

            record.getPhoneRecords().addAll(
                command.getPhoneRecords()
                    .stream()
                    .filter(phoneRecord -> !currentRecords.contains(phoneRecord))
                    .map(phoneRecord -> new PhoneRecordEntity()
                        .setDescription(phoneRecord.getDescription())
                        .setNumber(phoneRecord.getNumber())
                        .setOutboundLoadRecord(record))
                    .collect(Collectors.toList()));
        }

        return outboundLoadRecordRepository.save(record).toValueObject();
    }

    @Override
    public Integer getNextSourceId() {
        return outboundLoadRecordRepository.getNextSourceId();
    }

    private List<Predicate> toPredicates(OutboundLoadRecordQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getServiceId() != null) {
            predicates.add(Entities.outboundLoadEntity.serviceId.eq(query.getServiceId()));
        }
        if (query.getLoadId() != null) {
            predicates.add(Entities.outboundLoadEntity.loadId.eq(query.getLoadId()));
        }
        if (query.getSourceId() != null) {
            predicates.add(Entities.outboundLoadRecordEntity.sourceId.eq(query.getSourceId()));
        }
        return predicates;
    }

    private List<Predicate> toPredicates(OutboundLoadQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getServiceId() != null) {
            predicates.add(Entities.outboundLoadEntity.serviceId.eq(query.getServiceId()));
        }
        if (query.getLoadId() != null) {
            predicates.add(Entities.outboundLoadEntity.loadId.eq(query.getLoadId()));
        }
        if (query.getOutboundLoadStatus() != null) {
            predicates.add(Entities.outboundLoadEntity.status.eq(query.getOutboundLoadStatus()));
        }
        return predicates;
    }
}
