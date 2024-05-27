package fintech.bo.api.model.marketing;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SaveMarketingTemplateRequest {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private String emailBody;

    @NotNull
    private String htmlTemplate;
}
