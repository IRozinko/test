package fintech.spain.alfa.product.risk.idvalidation;

import fintech.scoring.values.db.ScoringModelEntity;
import fintech.scoring.values.db.ScoringModelRepository;
import fintech.scoring.values.db.ScoringValueData;
import fintech.scoring.values.db.ScoringValueSource;
import fintech.scoring.values.model.ScoringModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

@Component
@Transactional
public class IdValidationValuesServiceBean implements IdValidationValuesService {

    @Autowired
    private ScoringModelRepository modelRepository;

    @Autowired
    private IdValidationValuesProvider applicationIdValidationValuesProvider;

    @Override
    public ScoringModel collectValues(Long clientId, Long applicationId, Long identificationDocumentId) {
        ScoringModelEntity model = new ScoringModelEntity(clientId);
        List<ScoringValueData> values = collectData(clientId, applicationId, identificationDocumentId);
        model.setValues(values);
        return modelRepository.save(model).toValue();
    }

    private List<ScoringValueData> collectData(Long clientId, Long applicationId, Long identificationDocumentId) {
        Properties customerData = applicationIdValidationValuesProvider.provide(clientId, applicationId, identificationDocumentId);

        return customerData.entrySet()
            .stream()
            .map(this::toScoringValueData)
            .collect(Collectors.toList());
    }

    private ScoringValueData toScoringValueData(Map.Entry<?, Object> entry) {
        return new ScoringValueData(ScoringValueSource.INNER, entry.getKey().toString(), entry.getValue());
    }
}
