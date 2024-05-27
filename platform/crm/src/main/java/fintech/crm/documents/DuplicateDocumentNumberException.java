package fintech.crm.documents;

public class DuplicateDocumentNumberException extends RuntimeException {

	public DuplicateDocumentNumberException(String message) {
		super(message);
	}
}
