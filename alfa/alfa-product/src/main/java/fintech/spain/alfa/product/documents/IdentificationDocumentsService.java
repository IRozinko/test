package fintech.spain.alfa.product.documents;

import fintech.spain.alfa.product.db.IdentificationDocumentEntity;

import java.util.Optional;

public interface IdentificationDocumentsService {

    Long saveIdentificationDocument(SaveIdentificationDocumentCommand command);

    void invalidateIdentificationDocument(InvalidateIdentificationDocument command);

    void validateIdentificationDocument(ValidateIdentificationDocument command);

    Optional<IdentificationDocumentEntity> findLatestIdentificationDocument(long clientId);
}
