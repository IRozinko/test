package fintech.bo.api.model.statements;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ImportStatementRequest {
    @NotNull
    Long fileId;
    @NotNull
    Long institutionId;
}
