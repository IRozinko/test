package fintech.lending.core.application.commands;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class SaveScoreCommand {

    private Long applicationId;
    private BigDecimal score;
    private String scoreSource;


}
