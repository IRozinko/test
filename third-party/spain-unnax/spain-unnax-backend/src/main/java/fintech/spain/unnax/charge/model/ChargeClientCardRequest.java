package fintech.spain.unnax.charge.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ChargeClientCardRequest {

    @NotEmpty
    @Length(min = 6, max = 13)
    private String orderCode;

    private Integer amount;

    @NotEmpty
    private String concept;

    @NotEmpty
    private String cardHash;

    @NotEmpty
    private String cardHashReference;

}
