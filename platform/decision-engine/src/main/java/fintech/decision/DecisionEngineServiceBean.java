package fintech.decision;

import fintech.decision.db.DecisionEngineRequestEntity;
import fintech.decision.db.DecisionEngineRequestRepository;
import fintech.decision.db.ScoringValueUsageEntity;
import fintech.decision.db.ScoringValueUsageRepository;
import fintech.decision.model.DecisionEngineRequest;
import fintech.decision.model.DecisionRequestStatus;
import fintech.decision.model.DecisionResult;
import fintech.decision.spi.DecisionEngine;
import fintech.decision.spi.MockDecisionEngine;
import fintech.scoring.values.ScoringValuesService;
import fintech.scoring.values.model.ScoringValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Service
@Transactional
public class DecisionEngineServiceBean implements DecisionEngineService {

    @Resource(name = "${spain.decision.engine:" + MockDecisionEngine.NAME + "}")
    private DecisionEngine decisionEngine;

    @Autowired
    private DecisionEngineRequestRepository requestRepository;

    @Autowired
    private ScoringValueUsageRepository usageRepository;

    @Autowired
    private ScoringValuesService scoringValuesService;

    @Override
    public DecisionResult getDecision(DecisionEngineRequest request) {
        request.setValues(scoringValuesService.getValues(request.getScoringModelId()));
        DecisionResult result = decisionEngine.getDecision(request);
        Long engineRequestId = saveDecisionEngineRequest(request, result);
        if (result.getStatus() == DecisionRequestStatus.OK) {
            saveUsedValues(engineRequestId, result.getUsedFields());
            scoringValuesService.saveValues(request.getScoringModelId(), result.getVariablesResult());
        }
        result.setDecisionEngineRequestId(engineRequestId);

        return result;
    }

    @Override
    public DecisionResult getStrategies(DecisionEngineRequest request) {
        DecisionResult result = decisionEngine.getDecision(request);
        Long engineRequestId = saveDecisionEngineRequest(request, result);
        if (result.getStatus() == DecisionRequestStatus.OK) {
            saveUsedValues(engineRequestId, result.getUsedFields());
        }
        result.setDecisionEngineRequestId(engineRequestId);
        return result;
    }

    private void saveUsedValues(long decisionEngineRequestId, List<ScoringValue> usedFields) {
        usageRepository.save(new ScoringValueUsageEntity(decisionEngineRequestId, usedFields.stream().map(ScoringValue::getKey).toArray(String[]::new)));
    }

    private Long saveDecisionEngineRequest(DecisionEngineRequest request, DecisionResult result) {
        DecisionEngineRequestEntity entity = new DecisionEngineRequestEntity()
            .setClientId(request.getClientId())
            .setScenario(request.getScenario())
            .setScoringModelId(request.getScoringModelId())
            .setResult(result);

        return requestRepository.save(entity).getId();
    }

}
