package fintech.bo.api.model.risk.blacklist;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class AddToBlacklistRequest {

    @NotNull
    Long clientId;
    Long applicationId;
    Long loanId;
    LocalDate dateUntil;
    String reason;
    String notes;

}
