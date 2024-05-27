package fintech.spain.experian.scoring;

import fintech.ScoringProperties;
import fintech.scoring.values.spi.ScoringValuesProvider;
import fintech.spain.experian.ExperianService;
import fintech.spain.experian.model.CaisQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExperianScoringValuesProvider implements ScoringValuesProvider {

    private static final String EXPERIAN_PREFIX = "experian_resumen";

    private static final String TOTAL_NUMBER_OF_UNPAID_OPERATIONS = "total_number_of_unpaid_operations";
    private static final String TOTAL_NUMBER_OF_UNPAID_QUOTAS = "total_number_of_unpaid_quotas";
    private static final String WORST_PAYMENT_SITUATION_CODE = "worst_payment_situation_code";
    private static final String WORST_PAYMENT_SITUATION_DESCRIPTION = "worst_payment_situation_description";
    private static final String WORST_HISTORICAL_PAYMENT_SITUATION_CODE = "worst_historical_payment_situation_code";
    private static final String WORST_HISTORICAL_PAYMENT_SITUATION_DESCRIPTION = "worst_historical_payment_situation_description";
    private static final String TOTAL_AMOUNT_UNPAID = "total_amount_unpaid";
    private static final String MAX_UNPAID_AMOUNT = "max_unpaid_amount";

    private final ExperianService experianService;

    @Override
    public Properties provide(long clientId) {
        return experianService.findLatestResumenResponse(CaisQuery.byClientIdOkOrNotFound(clientId))
            .map(resp -> {
                ScoringProperties properties = new ScoringProperties(EXPERIAN_PREFIX);
                properties.put(TOTAL_NUMBER_OF_UNPAID_OPERATIONS, resp.getNumeroTotalOperacionesImpagadas());
                properties.put(TOTAL_NUMBER_OF_UNPAID_QUOTAS, resp.getNumeroTotalCuotasImpagadas());
                properties.put(WORST_PAYMENT_SITUATION_CODE, resp.getPeorSituacionPagoCodigo());
                properties.put(WORST_PAYMENT_SITUATION_DESCRIPTION, resp.getPeorSituacionPago());
                properties.put(WORST_HISTORICAL_PAYMENT_SITUATION_CODE, resp.getPeorSituacionPagoHistoricaCodigo());
                properties.put(WORST_HISTORICAL_PAYMENT_SITUATION_DESCRIPTION, resp.getPeorSituacionPagoHistorica());
                properties.put(TOTAL_AMOUNT_UNPAID, resp.getImporteTotalImpagado());
                properties.put(MAX_UNPAID_AMOUNT, resp.getMaximoImporteImpagado());
                return properties;
            }).orElse(new ScoringProperties());
    }

}
