package fintech.bo.api.model.admintools;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ExecuteAdminActionRequest {

    @NotNull
    private String name;

    private String params;
}
