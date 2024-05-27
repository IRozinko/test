package fintech.bo.components.dc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public enum DebtSubStatus {

    PRESENTADA_DEMANDA,PENDIENTE_INCOAR,
    REQUERIMIENTO_PREVIO, REQUERIMIENTO_DEL_JUZGADO,
    ADMISION, REQUERIMIENTO,
    INADMISION,
    NEGATIVO,
    ILOCALIZADO,
    INCOMPETENCIA_TERRITORIAL, INHIBICION,
    REQUERIMIENTO_POSITIVO, REQUERIMIENTO_DE_PAGO,
    FIN_MONITORIO,
    OPOSICION,
    VISTA,
    DESESTIMATORIA,
    SENTENCIA, SENTENCIA_ESTIMATORIA, SENTENCIA_ESTIMATORIA_TOTAL,
    SENTENCIA_ESTIMATORIA_PARCIAL,
    EJECUCION, DESPACHO_DE_EJECUCION,
    ARCHIVO , DESISTIMIENTO,
    ARCHIVO_HOMOLOGACION,
    ARCHIVO_PAGO;

    public static Set<String> subStatuses = ImmutableSet.of(PRESENTADA_DEMANDA.name(),
        PENDIENTE_INCOAR.name(),
        REQUERIMIENTO_PREVIO.name(),
        REQUERIMIENTO_DEL_JUZGADO.name(),
        ADMISION.name(),
        REQUERIMIENTO.name(),
        INADMISION.name(),
        NEGATIVO.name(),
        ILOCALIZADO.name(),
        INCOMPETENCIA_TERRITORIAL.name(),
        INHIBICION.name(),
        REQUERIMIENTO_POSITIVO.name(),
        REQUERIMIENTO_DE_PAGO.name(),
        FIN_MONITORIO.name(),
        OPOSICION.name(),
        VISTA.name(),
        DESESTIMATORIA.name(),
        SENTENCIA.name(),
        SENTENCIA_ESTIMATORIA.name(),
        SENTENCIA_ESTIMATORIA_TOTAL.name(),
        SENTENCIA_ESTIMATORIA_PARCIAL.name(),
        EJECUCION.name(),
        DESPACHO_DE_EJECUCION.name(),
        ARCHIVO.name(),
        DESISTIMIENTO.name(),
        ARCHIVO_HOMOLOGACION.name(),
        ARCHIVO_PAGO.name()
    );
    public static List<String> subStatusesList = ImmutableList.of(PRESENTADA_DEMANDA.name(),
        PENDIENTE_INCOAR.name(),
        REQUERIMIENTO_PREVIO.name(),
        REQUERIMIENTO_DEL_JUZGADO.name(),
        ADMISION.name(),
        REQUERIMIENTO.name(),
        INADMISION.name(),
        NEGATIVO.name(),
        ILOCALIZADO.name(),
        INCOMPETENCIA_TERRITORIAL.name(),
        INHIBICION.name(),
        REQUERIMIENTO_POSITIVO.name(),
        REQUERIMIENTO_DE_PAGO.name(),
        FIN_MONITORIO.name(),
        OPOSICION.name(),
        VISTA.name(),
        DESESTIMATORIA.name(),
        SENTENCIA.name(),
        SENTENCIA_ESTIMATORIA.name(),
        SENTENCIA_ESTIMATORIA_TOTAL.name(),
        SENTENCIA_ESTIMATORIA_PARCIAL.name(),
        EJECUCION.name(),
        DESPACHO_DE_EJECUCION.name(),
        ARCHIVO.name(),
        DESISTIMIENTO.name(),
        ARCHIVO_HOMOLOGACION.name(),
        ARCHIVO_PAGO.name()
    );
    }
