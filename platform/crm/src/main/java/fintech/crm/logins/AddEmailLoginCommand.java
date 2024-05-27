package fintech.crm.logins;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.Preconditions.checkArgument;

@Data
public class AddEmailLoginCommand {
	
	private final Long clientId;
	private final String email;
	private final String password;
	private final boolean temporaryPassword;

	public AddEmailLoginCommand(Long clientId, String email, String password, boolean temporaryPassword) {
		checkArgument(!StringUtils.isEmpty(email), "Empty email");
		checkArgument(!StringUtils.isEmpty(password), "Empty password");

		this.clientId = clientId;
		this.email = StringUtils.strip(email).toLowerCase();
		this.password = password;
		this.temporaryPassword = temporaryPassword;
	}
}
