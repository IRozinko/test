package fintech.dc.commands;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddAgentAbsenceCommand {

    private String agent;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String reason;
}
