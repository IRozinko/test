package fintech.scoring.values.impl;

import fintech.scoring.values.ScoringValuesService;
import fintech.scoring.values.db.ScoringModelEntity;
import fintech.scoring.values.db.ScoringModelRepository;
import fintech.scoring.values.db.ScoringValueData;
import fintech.scoring.values.db.ScoringValueSource;
import fintech.scoring.values.model.ScoringModel;
import fintech.scoring.values.model.ScoringValue;
import fintech.scoring.values.spi.ScoringValuesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class ScoringValuesServiceBean implements ScoringValuesService {

    private final List<ScoringValuesProvider> variablesProviders;
    private final ScoringModelRepository modelRepository;

    public ScoringValuesServiceBean(@Autowired(required = false) List<ScoringValuesProvider> variablesProviders,
                                    ScoringModelRepository modelRepository) {
        this.variablesProviders = variablesProviders;
        this.modelRepository = modelRepository;
    }

    @Override
    public ScoringModel collectValues(long clientId) {
        ScoringModelEntity model = new ScoringModelEntity(clientId);
        model.setValues(collectDataValues(clientId));
        return modelRepository.save(model).toValue();
    }

    @Override
    public List<ScoringValue> getValues(long scoringModelId) {
        return toScoringValues(modelRepository.getRequired(scoringModelId).getValues());
    }

    @Override
    public Map<String, Object> getValuesAsMap(long scoringModelId) {
        return modelRepository.getRequired(scoringModelId).getValues()
            .stream()
            .collect(Collectors.toMap(ScoringValueData::getKey, ScoringValueData::getValAsObject));
    }

    @Override
    public ScoringModel saveValues(long scoringModelId, Map<String, Object> values) {
        ScoringModelEntity model = modelRepository.getRequired(scoringModelId);
        List<ScoringValueData> valuesEntities = toValueEntities(values, ScoringValueSource.OUTER);
        model.addValues(valuesEntities);
        return modelRepository.save(model).toValue();
    }

    private List<ScoringValueData> collectDataValues(long clientId) {
        return Optional.ofNullable(variablesProviders).orElse(Collections.emptyList())
            .stream()
            .map(provider -> provider.provide(clientId))
            .flatMap(p -> p.entrySet().stream())
            .map(e -> toScoringValueData(e, ScoringValueSource.INNER))
            .collect(Collectors.toList());
    }

    private List<ScoringValueData> toValueEntities(Map<?, Object> values,
                                                   ScoringValueSource source) {
        return values.entrySet().stream()
            .map(e -> toScoringValueData(e, source))
            .collect(Collectors.toList());
    }

    private ScoringValueData toScoringValueData(Map.Entry<?, Object> entry,
                                                ScoringValueSource source) {
        return new ScoringValueData(source, entry.getKey().toString(), entry.getValue());
    }

    private List<ScoringValue> toScoringValues(List<ScoringValueData> entities) {
        return entities.stream()
            .map(e -> new ScoringValue(e.getKey(), e.getValAsObject(), e.getSrc()))
            .collect(Collectors.toList());
    }

}
