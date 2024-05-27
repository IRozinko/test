package fintech.bo.api.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class UpdatePropertyRequest {

    private String name;

    private String textValue;
    private Boolean booleanValue;
    private Long numberValue;
    private BigDecimal decimalValue;
    private LocalDate dateValue;
    private LocalDateTime dateTimeValue;
}

