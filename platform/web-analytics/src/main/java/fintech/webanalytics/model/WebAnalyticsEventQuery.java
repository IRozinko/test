package fintech.webanalytics.model;

import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class WebAnalyticsEventQuery {

    private Long clientId;
    private Long applicationId;
    private List<String> eventTypes = new ArrayList<>();


    public static WebAnalyticsEventQuery byApplicationIdAndEventType(Long applicationId, String eventType) {
        WebAnalyticsEventQuery query = new WebAnalyticsEventQuery();
        query.setApplicationId(applicationId);
        query.setEventTypes(ImmutableList.of(eventType));
        return query;
    }
}
