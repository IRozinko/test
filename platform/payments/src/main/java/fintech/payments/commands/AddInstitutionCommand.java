package fintech.payments.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddInstitutionCommand {

    private String name;
    private String code;
    private String institutionType;
    private List<Account> accounts = new ArrayList<>();
    private List<String> paymentMethods = new ArrayList<>();
    private boolean primary;
    private boolean disabled = false;
    private String statementImportFormat;
    private String statementExportFormat;
    private String statementApiExporter;
    private String statementExportParamsJson;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Account {
        private String accountNumber;
        private String accountingAccountCode;
        private boolean primary;
    }
}
