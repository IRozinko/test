package fintech.spain.alfa.web.models;

import fintech.spain.alfa.web.validators.PasswordHash;
import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@ToString(exclude = {"currentPassword", "newPassword"})
public class ChangePasswordRequest {
    @Length(min = 6)
    @NotEmpty
    @PasswordHash(skipTemporaryValidation = true)
    private String currentPassword;

    @Length(min = 6)
    @NotEmpty
    private String newPassword;
}
