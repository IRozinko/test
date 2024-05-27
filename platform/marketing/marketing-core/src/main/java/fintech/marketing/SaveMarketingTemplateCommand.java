package fintech.marketing;

import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

@Data
@Accessors(chain = true)
public class SaveMarketingTemplateCommand {

    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String emailBody;

    @NotEmpty
    private String htmlTemplate;

    @NotNull
    private byte[] mainCampaignImage;//todo

}
