package fintech.spain.inglobaly.impl;

import com.global.info.ws.soap.ListadoDomiciliosTelefonos;
import com.global.info.ws.soap.ObtenerDomicilioNIFResponse;
import com.querydsl.core.types.Predicate;
import fintech.spain.inglobaly.InglobalyService;
import fintech.spain.inglobaly.db.Entities;
import fintech.spain.inglobaly.db.InglobalyResponseEntity;
import fintech.spain.inglobaly.db.InglobalyResponseRepository;
import fintech.spain.inglobaly.model.InglobalyQuery;
import fintech.spain.inglobaly.model.InglobalyRequest;
import fintech.spain.inglobaly.model.InglobalyResponse;
import fintech.spain.inglobaly.model.InglobalyStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import static com.querydsl.core.types.ExpressionUtils.allOf;

@Slf4j
@Transactional
@Component
public class InglobalyServiceBean implements InglobalyService {

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @Autowired
    private InglobalyResponseRepository repository;


    @Resource(name = "${spain.inglobaly.provider:" + MockInglobalyProvider.NAME + "}")
    private InglobalyProvider provider;

    @Override
    public InglobalyResponse get(Long id) {
        return repository.getRequired(id).toValueObject();
    }

    @Override
    public InglobalyResponse request(InglobalyRequest request) {
        log.info("Requesting Inglobaly: [{}]", request);
        InglobalyResponseEntity entity = new InglobalyResponseEntity();
        entity.setClientId(request.getClientId());
        entity.setApplicationId(request.getApplicationId());
        entity.setRequestedDocumentNumber(request.getDocumentNumber());

        ListadoDomiciliosTelefonos response;
        try {
            response = provider.request(request.getDocumentNumber());
        } catch (Exception e) {
            log.error("Inglobaly request failed", e);
            entity.setStatus(InglobalyStatus.ERROR);
            entity.setError(e.getMessage());
            return save(entity);        }
        if (response == null) {
            log.info("Person not found in Inglobaly database: [{}]", request);
            entity.setStatus(InglobalyStatus.NOT_FOUND);
            return save(entity);
        }
        log.info("Person found in Inglobaly database: [{}]", request);
        entity.setStatus(InglobalyStatus.FOUND);

        String responseBody = toResponseBody(response);
        entity.setResponseBody(responseBody);
        resolve(() -> response.getPersona().getFechaNacimiento())
            .map(val -> LocalDate.parse(val, DateTimeFormatter.ofPattern(DATE_FORMAT))).ifPresent(entity::setDateOfBirth);
        resolve(() -> response.getPersona().getNombre()).ifPresent(entity::setFirstName);
        resolve(() -> response.getPersona().getApellido1()).ifPresent(entity::setLastName);
        resolve(() -> response.getPersona().getApellido2()).ifPresent(entity::setSecondLastName);
        return save(entity);
    }

    private String toResponseBody(ListadoDomiciliosTelefonos response) {
        ObtenerDomicilioNIFResponse jaxb = new ObtenerDomicilioNIFResponse();
        jaxb.setReturn(response);
        return ObjectSerializer.marshal(ObjectSerializer.wrapInRootElement("http://soap.ws.info.global.com/", "obtenerDomicilioNIFResponse", jaxb));
    }

    private InglobalyResponse save(InglobalyResponseEntity entity) {
        repository.saveAndFlush(entity);
        return entity.toValueObject();
    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<InglobalyResponse> findLatest(InglobalyQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(Entities.response.clientId.eq(query.getClientId()));
        }
        if (query.getDocumentNumber() != null) {
            predicates.add(Entities.response.requestedDocumentNumber.eq(query.getDocumentNumber()));
        }
        if (query.getCreatedAfter() != null) {
            predicates.add(Entities.response.createdAt.after(query.getCreatedAfter()));
        }
        if (!query.getStatus().isEmpty()) {
            predicates.add(Entities.response.status.in(query.getStatus()));
        }
        Page<InglobalyResponseEntity> items = repository.findAll(allOf(predicates), new QPageRequest(0, 1, Entities.response.id.desc()));
        return items.getContent().stream().map(InglobalyResponseEntity::toValueObject).findFirst();
    }
}
