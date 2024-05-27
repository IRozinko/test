package fintech.dc.commands;

import lombok.Data;

@Data
public class DebtImportCommand {
    Long fileId;
    Long debtImportId;
    String companyName;
    String portfolioName;
    String state;
    String status;
}
