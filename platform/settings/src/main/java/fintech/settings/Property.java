package fintech.settings;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class Property {

    private String name;

    private Boolean booleanValue;

    private Long numberValue;

    private BigDecimal decimalValue;

    private LocalDateTime dateTimeValue;

    private LocalDate dateValue;

    private String textValue;

}
