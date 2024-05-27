package fintech.spain.alfa.product.risk.idvalidation;

import fintech.scoring.values.model.ScoringModel;

public interface IdValidationValuesService {
    ScoringModel collectValues(Long clientId, Long applicationId, Long identificationDocumentId);
}
