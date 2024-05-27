package fintech.lending.core.application.commands;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateInquiryCommand {

    private Long applicationId;
    private BigDecimal requestedPrincipal;
    private Long termInMonth;

}
