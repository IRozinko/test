package fintech.payments.commands;

import fintech.payments.DisbursementConstants;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AddDisbursementCommand {

    private String disbursementType = DisbursementConstants.DISBURSEMENT_TYPE_PRINCIPAL;

    @NotNull
    private Long clientId;
    private Long loanId;
    private Long applicationId;
    @NotNull
    private Long institutionId;
    @NotNull
    private Long institutionAccountId;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate valueDate;
    @NotNull
    private String reference;
}
