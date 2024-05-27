package fintech.bo.api.model.marketing;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PreviewCampaignRequest {
    private Long campaignId;

    @NotNull
    private Long templateId;

    private Long promoCodeId;

    @NotNull
    private String content;

    @NotNull
    private Boolean reminder;
}
