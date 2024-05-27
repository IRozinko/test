package fintech.marketing;

import fintech.lending.core.promocode.PromoCodeOffer;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MarketingModel {

    private String unsubscribeUrl;
    private String mainImageBase64String;
    private PromoCodeOffer promoCode;
    private String content;

    private Long communicationId;
    private Long campaignId;

    private String trackClickUuid;
    private String trackViewUuid;

}
