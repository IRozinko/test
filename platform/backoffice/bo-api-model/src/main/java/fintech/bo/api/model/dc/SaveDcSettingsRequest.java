package fintech.bo.api.model.dc;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveDcSettingsRequest {

    @NotNull
    private String json;
}
