package fintech.crm.logins;

public class DuplicateEmailLoginException extends RuntimeException {
	public DuplicateEmailLoginException(String message) {
		super(message);
	}
}
