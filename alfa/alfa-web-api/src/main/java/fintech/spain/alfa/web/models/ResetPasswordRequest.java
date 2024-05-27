package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.AssertTrue;
import java.util.Objects;

@Data
@ToString(exclude = {"password", "repeatedPassword"})
public class ResetPasswordRequest {
    @NotEmpty
    private String token;

    @NotEmpty
    @Length(min = 6)
    private String password;

    @NotEmpty
    private String repeatedPassword;

    @AssertTrue
    public boolean isPasswordRepeat() {
        return Objects.equals(password, repeatedPassword);
    }
}
