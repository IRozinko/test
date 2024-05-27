package fintech.dc.commands;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ChangeCompanyCommand {

    private Long debtId;

    private String portfolio;
    private String owningCompany;
    private String managingCompany;

    private String operationType;
}
