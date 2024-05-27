package fintech.payments.commands;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateInstitutionCommand {

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
