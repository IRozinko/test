package fintech.bo.api.model.dc;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AddAgentAbsenceRequest {

    private String agent;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String reason;
}
