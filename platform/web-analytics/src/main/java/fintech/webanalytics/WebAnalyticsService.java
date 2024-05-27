package fintech.webanalytics;

import fintech.webanalytics.model.SaveEventCommand;
import fintech.webanalytics.model.WebAnalyticsEvent;
import fintech.webanalytics.model.WebAnalyticsEventQuery;

import java.util.Optional;

public interface WebAnalyticsService {

    Long saveEvent(SaveEventCommand command);

    Optional<WebAnalyticsEvent> findLatest(WebAnalyticsEventQuery query);
}
