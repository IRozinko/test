package fintech.spain.alfa.bo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class SendCmsNotificationRequest {

    @NotNull
    private final Long applicationId;
}
