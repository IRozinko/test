package fintech.spain.alfa.product.registration.forms;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

@Data
@Accessors(chain = true)
public class AnalyticsData {

    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private String utmTerm;
    private String utmContent;
    private String gclid;

    public static boolean isEmpty(AnalyticsData data) {
        return StringUtils.isBlank(data.utmSource)
            && StringUtils.isBlank(data.utmMedium)
            && StringUtils.isBlank(data.utmCampaign)
            && StringUtils.isBlank(data.utmTerm)
            && StringUtils.isBlank(data.gclid)
            && StringUtils.isBlank(data.utmContent);
    }
}
