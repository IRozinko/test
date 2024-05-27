package fintech.spain.experian;

import fintech.spain.experian.model.*;

import java.util.List;
import java.util.Optional;

public interface ExperianService {


    Optional<CaisResumenResponse> findLatestResumenResponse(CaisQuery query);

    CaisResumenResponse requestResumen(CaisRequest caisRequest);

    Optional<CaisListOperacionesResponse> findLatestListOperacionesResponse(CaisQuery query);

    CaisListOperacionesResponse requestListOperaciones(CaisRequest caisRequest);

    CaisResumenResponse getResumenResponse(Long id);

    CaisListOperacionesResponse getListOperacionesResponse(Long id);

    List<CaisDebt> findDebtsByOperacionesResponse(Long operacionesResponseId);
}
