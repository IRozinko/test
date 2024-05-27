package fintech.bo.api.model.institution;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class UpdateInstitutionRequest {

    @NotNull
    private Long institutionId;

    @NotNull
    private String name;

    private boolean primary;

    private boolean disabled;

    private String statementImportFormat;

    private String statementExportFormat;

    private String statementExportParamsJson;

}
