package fintech.lending.core.application.model;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import java.math.BigDecimal;

@Data
public class DormantsApplicationData {

    @NotEmpty
    private BigDecimal creditLimit;
}
