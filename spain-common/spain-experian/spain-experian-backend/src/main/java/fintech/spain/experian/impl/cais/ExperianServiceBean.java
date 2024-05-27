package fintech.spain.experian.impl.cais;

import cais.servicios.experian.ErrorServicio;
import cais.servicios.experian.GenerarInforme;
import cais.servicios.experian.Informe;
import cais.servicios.experian.PeticionInforme;
import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.querydsl.core.types.Predicate;
import fintech.PojoUtils;
import fintech.spain.experian.ExperianService;
import fintech.spain.experian.db.*;
import fintech.spain.experian.impl.ObjectSerializer;
import fintech.spain.experian.model.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import v2.cais.servicios.experian.*;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.querydsl.core.types.ExpressionUtils.allOf;
import static fintech.BigDecimalUtils.amount;
import static fintech.spain.experian.db.Entities.listOperciones;
import static fintech.spain.experian.db.Entities.resumen;

@Slf4j
@Transactional
@Component
public class ExperianServiceBean implements ExperianService {


    public static final String ERROR_CODE_NOT_FOUND = "4030";
    private static final String ERROR_CODE_OK = "0000";

    @Resource(name = "${spain.experian.cais.provider:" + MockExperianCaisProvider.NAME + "}")
    private ExperianCaisProvider provider;

    @Autowired
    private ExperianCaisRequestBuilder requestBuilder;

    @Autowired
    private ObjectSerializer objectSerializer;

    @Autowired
    private CaisResumenRepository resumenRepository;

    @Autowired
    private CaisListOperacionesRepository listOperacionesRepository;

    @Autowired
    private CaisDebtRepository caisDebtRepository;


    @Override
    public Optional<CaisResumenResponse> findLatestResumenResponse(CaisQuery query) {

        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(resumen.clientId.eq(query.getClientId()));
        }
        if (query.getDocumentNumber() != null) {
            predicates.add(resumen.documentNumber.eq(query.getDocumentNumber()));
        }
        if (query.getCreatedAfter() != null) {
            predicates.add(resumen.createdAt.after(query.getCreatedAfter()));
        }
        if (!query.getStatus().isEmpty()) {
            predicates.add(resumen.status.in(query.getStatus()));
        }

        Page<CaisResumenEntity> items = resumenRepository.findAll(allOf(predicates), new QPageRequest(0, 1, resumen.id.desc()));
        return items.getContent().stream().map(CaisResumenEntity::toValueObject).findFirst();
    }

    @Override
    public CaisResumenResponse requestResumen(CaisRequest caisRequest) {
        Preconditions.checkNotNull(caisRequest.getDocumentNumber(), "Null Document Number");

        PeticionInforme request = requestBuilder.prepareRequest(caisRequest, ExperianCaisConfiguration.RESUMEN);
        String rawRequest = toRawRequestString(request);

        CaisResumenEntity entity = new CaisResumenEntity();
        entity.setRequestBody(rawRequest);
        entity.setClientId(caisRequest.getClientId());
        entity.setApplicationId(caisRequest.getApplicationId());
        entity.setDocumentNumber(caisRequest.getDocumentNumber());

        Informe report;
        try {
            report = provider.request(request);
        } catch (Exception e) {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError(e.getMessage());
            entity = resumenRepository.saveAndFlush(entity);
            return entity.toValueObject();
        }

        try {
            String rawResponse = objectSerializer.marshal(report.getContent());
            entity.setResponseBody(rawResponse);
        } catch (Exception e) {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError(e.getMessage());
            entity = resumenRepository.saveAndFlush(entity);
            return entity.toValueObject();
        }

        Optional<ErrorServicio> maybeError = getObject(report, ErrorServicio.class);
        Optional<InformeResumen> maybeResumen = getObject(report, InformeResumen.class);
        if (maybeError.isPresent()) {
            ErrorServicio error = maybeError.get();
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError(String.format("%s: %s", error.getCodigo(), error.getDescripcion()));
        } else if (maybeResumen.isPresent()) {
            InformeResumen resumen = maybeResumen.get();
            TBloqueResumen resumenBlock = resumen.getResumen();
            TError error = resumen.getError();
            if (error != null && !ERROR_CODE_OK.equals(error.getCodigo())) {
                entity.setStatus(ERROR_CODE_NOT_FOUND.equals(error.getCodigo()) ? ExperianStatus.NOT_FOUND : ExperianStatus.ERROR);
                entity.setError(String.format("%s: %s", error.getCodigo(), error.getDescripcion()));
            } else if (resumenBlock == null) {
                entity.setStatus(ExperianStatus.ERROR);
                entity.setError("No resumen block found");
            } else {
                entity.setStatus(ExperianStatus.FOUND);
                entity.setImporteTotalImpagado(resumenBlock.getImporteTotalImpagado());
                entity.setMaximoImporteImpagado(resumenBlock.getMaximoImporteImpagado());
                entity.setNumeroTotalCuotasImpagadas(resumenBlock.getNumeroTotalCuotasImpagadas());
                entity.setNumeroTotalOperacionesImpagadas(resumenBlock.getNumeroTotalOperacionesImpagadas());

                String provinciaCodigo = resolve(() -> resumenBlock.getDireccion().getProvincia().getCodigo()).orElse(null);
                entity.setProvinciaCodigo(provinciaCodigo);

                if (resumenBlock.getPeorSituacionPago() != null) {
                    entity.setPeorSituacionPago(resumenBlock.getPeorSituacionPago().getDescripcion());
                    entity.setPeorSituacionPagoCodigo(resumenBlock.getPeorSituacionPago().getCodigo());
                }
                if (resumenBlock.getPeorSituacionPagoHistorica() != null) {
                    entity.setPeorSituacionPagoHistoricaCodigo(resumenBlock.getPeorSituacionPagoHistorica().getCodigo());
                    entity.setPeorSituacionPagoHistorica(resumenBlock.getPeorSituacionPagoHistorica().getDescripcion());
                }
            }
        } else {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError("No required objects found in response");
        }
        entity = resumenRepository.saveAndFlush(entity);
        return entity.toValueObject();
    }

    @Override
    public Optional<CaisListOperacionesResponse> findLatestListOperacionesResponse(CaisQuery query) {
        List<Predicate> predicates = new ArrayList<>();
        if (query.getClientId() != null) {
            predicates.add(listOperciones.clientId.eq(query.getClientId()));
        }
        if (query.getDocumentNumber() != null) {
            predicates.add(listOperciones.documentNumber.eq(query.getDocumentNumber()));
        }
        if (query.getCreatedAfter() != null) {
            predicates.add(listOperciones.createdAt.after(query.getCreatedAfter()));
        }
        if (!query.getStatus().isEmpty()) {
            predicates.add(listOperciones.status.in(query.getStatus()));
        }

        Page<CaisListOperacionesEntity> items = listOperacionesRepository.findAll(allOf(predicates), new QPageRequest(0, 1, listOperciones.id.desc()));
        return items.getContent().stream().map(CaisListOperacionesEntity::toValueObject).findFirst();
    }

    @Override
    public CaisListOperacionesResponse requestListOperaciones(CaisRequest caisRequest) {
        Preconditions.checkNotNull(caisRequest.getDocumentNumber(), "Null Document Number");

        PeticionInforme request = requestBuilder.prepareRequest(caisRequest, ExperianCaisConfiguration.LISTA_OPERACIONES);
        String rawRequest = toRawRequestString(request);

        CaisListOperacionesEntity entity = new CaisListOperacionesEntity();
        entity.setRequestBody(rawRequest);
        entity.setClientId(caisRequest.getClientId());
        entity.setApplicationId(caisRequest.getApplicationId());
        entity.setDocumentNumber(caisRequest.getDocumentNumber());

        Informe report;
        try {
            report = provider.request(request);
        } catch (Exception e) {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError(e.getMessage());
            entity = listOperacionesRepository.saveAndFlush(entity);
            return entity.toValueObject();
        }

        try {
            String rawResponse = objectSerializer.marshal(report.getContent());
            entity.setResponseBody(rawResponse);
        } catch (Exception e) {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError(e.getMessage());
            entity = listOperacionesRepository.saveAndFlush(entity);
            return entity.toValueObject();
        }

        Optional<ErrorServicio> maybeError = getObject(report, ErrorServicio.class);
        Optional<InformeListaOperaciones> maybeOperaciones = getObject(report, InformeListaOperaciones.class);
        if (maybeError.isPresent()) {
            ErrorServicio error = maybeError.get();
            entity.setStatus(ERROR_CODE_NOT_FOUND.equals(error.getCodigo()) ? ExperianStatus.NOT_FOUND : ExperianStatus.ERROR);
            entity.setError(String.format("%s: %s", error.getCodigo(), error.getDescripcion()));
        } else if (maybeOperaciones.isPresent()) {
            InformeListaOperaciones operaciones = maybeOperaciones.get();

            TError error = operaciones.getError();
            if (error != null && !ERROR_CODE_OK.equals(error.getCodigo())) {
                entity.setStatus(ERROR_CODE_NOT_FOUND.equals(error.getCodigo()) ? ExperianStatus.NOT_FOUND : ExperianStatus.ERROR);
                entity.setError(String.format("%s: %s", error.getCodigo(), error.getDescripcion()));
            } else {
                entity.setStatus(ExperianStatus.FOUND);
                entity.setNumeroRegistrosDevueltos(operaciones.getNumeroRegistrosDevueltos());
            }
        } else {
            entity.setStatus(ExperianStatus.ERROR);
            entity.setError("No required objects found in response");
        }
        entity = listOperacionesRepository.saveAndFlush(entity);

        if (maybeOperaciones.isPresent()) {
            List<TBloqueOperacion> debts = PojoUtils.npeSafe(() -> maybeOperaciones.get().getRegistros().getTBloqueOperacion()).orElse(ImmutableList.of());
            for (TBloqueOperacion debt : debts) {
                saveDebt(entity, debt);
            }
        }

        return entity.toValueObject();
    }

    private void saveDebt(CaisListOperacionesEntity owner, TBloqueOperacion debt) {
        CaisDebtEntity entity = new CaisDebtEntity();
        entity.setApplicationId(owner.getApplicationId());
        entity.setClientId(owner.getClientId());
        entity.setOperaciones(owner);
        entity.setIdOperacion(debt.getIdOperacion());
        entity.setTipoProductoFinanciadoCodigo(PojoUtils.npeSafe(() -> debt.getTipoProductoFinanciado().getCodigo()).orElse(""));
        entity.setTipoProductoFinanciadoDescription(PojoUtils.npeSafe(() -> debt.getTipoProductoFinanciado().getDescripcion()).orElse(""));
        entity.setSituacionPagoCodigo(PojoUtils.npeSafe(() -> debt.getSituacionPago().getCodigo()).orElse(""));
        entity.setSituacionPagoDescription(PojoUtils.npeSafe(() -> debt.getSituacionPago().getDescripcion()).orElse(""));
        entity.setSaldoImpagado(MoreObjects.firstNonNull(debt.getSaldoImpagado(), amount(0)));
        entity.setTipoIntervinienteCodigo(PojoUtils.npeSafe(() -> debt.getTipoInterviniente().getCodigo()).orElse(""));
        entity.setTipoIntervinienteDescription(PojoUtils.npeSafe(() -> debt.getTipoInterviniente().getDescripcion()).orElse(""));
        entity.setNumeroCuotasImpagadas(MoreObjects.firstNonNull(debt.getNumeroCuotasImpagadas(), 0));
        entity.setImporteCuota(MoreObjects.firstNonNull(debt.getImporteCuota(), amount(0)));
        entity.setFrecuenciaPagoCodigo(PojoUtils.npeSafe(() -> debt.getFrecuenciaPago().getCodigo()).orElse(""));
        entity.setFrecuenciaPagoDescription(PojoUtils.npeSafe(() -> debt.getFrecuenciaPago().getDescripcion()).orElse(""));
        if (debt.getFechaInicio() != null && !StringUtils.isBlank(debt.getFechaInicio().getDD())) {
            entity.setFechaInicio(LocalDate.of(
                Integer.parseInt(debt.getFechaInicio().getAAAA()),
                Integer.parseInt(debt.getFechaInicio().getMM()),
                Integer.parseInt(debt.getFechaInicio().getDD())));
        }
        if (debt.getFechaFin() != null && !StringUtils.isBlank(debt.getFechaFin().getDD())) {
            entity.setFechaFin(LocalDate.of(
                Integer.parseInt(debt.getFechaFin().getAAAA()),
                Integer.parseInt(debt.getFechaFin().getMM()),
                Integer.parseInt(debt.getFechaFin().getDD())));
        }
        entity.setInformante(debt.getInformante());
        caisDebtRepository.save(entity);
    }

    @Override
    public CaisResumenResponse getResumenResponse(Long id) {
        return resumenRepository.getRequired(id).toValueObject();
    }

    @Override
    public CaisListOperacionesResponse getListOperacionesResponse(Long id) {
        return listOperacionesRepository.getRequired(id).toValueObject();
    }

    @Override
    public List<CaisDebt> findDebtsByOperacionesResponse(Long operacionesResponseId) {
        return caisDebtRepository.findAll(Entities.debt.operaciones.id.eq(operacionesResponseId), Entities.debt.id.asc()).stream().map(CaisDebtEntity::toValueObject).collect(Collectors.toList());
    }

    private <T> Optional<T> getObject(Informe report, Class<T> clazz) {
        return report.getContent()
            .stream()
            .map(JAXBElement::getValue)
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .findFirst();
    }

    private String toRawRequestString(PeticionInforme request) {
        GenerarInforme root = new GenerarInforme();
        root.setPeticion(request);
        return objectSerializer.marshal(ImmutableList.of(root));
    }

    public static <T> Optional<T> resolve(Supplier<T> resolver) {
        try {
            T result = resolver.get();
            return Optional.ofNullable(result);
        } catch (NullPointerException e) {
            return Optional.empty();
        }
    }
}
