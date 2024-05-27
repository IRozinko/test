package fintech.spain.alfa.web.models;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveIovationBlackboxRequest {

    @NotNull
    private String blackBox;
}
