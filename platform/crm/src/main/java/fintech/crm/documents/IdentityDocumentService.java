package fintech.crm.documents;

import java.util.List;
import java.util.Optional;

public interface IdentityDocumentService {

    Long addDocument(AddIdentityDocumentCommand command);

    void makeDocumentPrimary(Long documentId) throws DuplicateDocumentNumberException;

    Optional<IdentityDocument> findPrimaryDocument(Long clientId, String type);

    List<IdentityDocument> findPrimaryDocuments(Long clientId);

    Optional<IdentityDocument> findByNumber(String documentNumber, String type, boolean primary);

    boolean isDocumentNumberAvailable(String documentNumber, String documentType);
}
