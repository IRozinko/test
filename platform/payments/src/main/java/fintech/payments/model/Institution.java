package fintech.payments.model;

import fintech.Validate;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

@Data
public class Institution {

    private Long id;
    private String name;
    private String code;
    private String institutionType;
    private List<InstitutionAccount> accounts;
    private boolean primary;
    private boolean disabled;
    private String statementImportFormat;
    private String statementApiExporter;
    private String statementExportFormat;
    private String statementExportParamsJson;

    public InstitutionAccount getPrimaryAccount() {
        Optional<InstitutionAccount> primary = findPrimaryAccount();
        Validate.isTrue(primary.isPresent(), "No primary account exists for institution: [{}]", this);
        return primary.get();
    }

    public Optional<InstitutionAccount> findPrimaryAccount() {
        return accounts.stream().filter(InstitutionAccount::isPrimary).findFirst();
    }

    public boolean isApiExport() {
        return StringUtils.isNotBlank(statementApiExporter);
    }
}
