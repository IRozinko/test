package fintech.crm.contacts;

public class DuplicatePrimaryEmailException extends RuntimeException {
	public DuplicatePrimaryEmailException(String message) {
		super(message);
	}
}
