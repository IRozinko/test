package fintech.crm.bankaccount;

public class DuplicateBankAccountException extends RuntimeException {

	public DuplicateBankAccountException(String message) {
		super(message);
	}
}
