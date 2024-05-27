package fintech.dc.commands;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ExternalizeDebtCommand {

    private Long debtId;
    private String newOwningCompany;
    private String newManagingCompany;
    private String portfolio;
    private String operationType;

}
