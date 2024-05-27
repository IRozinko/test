package fintech.web.api.models;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class ContractAgreementRequest {

    @NotNull
    @Valid
    private Client client;
    @NotNull
    @Valid
    private Application application;

    @Data
    @Accessors(chain = true)
    public static class Client {
        @NotEmpty
        private String number;
        @NotEmpty
        private String documentNumber;
        @NotEmpty
        private String email;
        @NotEmpty
        private String phoneNumber;
        private String firstName;
        private String lastName;
        @NotEmpty
        private String iban;
        private String addressLine1;
        private String addressLine2;
    }

    @Data
    @Accessors(chain = true)
    public static class Application {
        @NotEmpty
        private String number;
        @NotNull
        private BigDecimal offeredPrincipal;
        @NotNull
        private LocalDate date;
    }
}
