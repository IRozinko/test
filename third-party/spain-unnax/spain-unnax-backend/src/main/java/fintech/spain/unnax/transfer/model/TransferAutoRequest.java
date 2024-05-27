package fintech.spain.unnax.transfer.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import fintech.BigDecimalUtils;
import fintech.Validate;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Data
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TransferAutoRequest {

    /**
     * In euro cents. 1 euro = 100 euro cents
     */
    @NotNull
    @Min(value = 1, message = "TransferRequest amount can't be zero.")
    private Integer amount;

    @NotBlank
    private String destinationAccount;

    @NotBlank
    private String customerCode;

    @NotBlank
    private String orderCode;

    @NotBlank
    private String customerNames;

    private String currency = "EUR";
    private String concept;
    private String bankOrderCode;
    private String sourceAccount;
    private int transferType = TransferAutoType.STANDARD.getNumeric();
    private List<String> tags;

    public TransferAutoRequest setAmountInEuros(BigDecimal amount) {
        Validate.notNull(amount, "TransferRequest amount can't be NULL.");
        this.amount = BigDecimalUtils.multiplyByHundred(amount);
        return this;
    }

    public List<String> getTags() {
        return Optional.ofNullable(tags).orElse(Collections.emptyList());
    }

    public TransferAutoRequest setTransferType(TransferAutoType type) {
        this.transferType = type.getNumeric();
        return this;
    }
}
