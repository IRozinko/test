package fintech.bo.api.model.risk.blacklist;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RemoveFromBlacklistRequest {

    @NotNull
    Long entryId;
    @NotNull
    String reason;
    String notes;

}
