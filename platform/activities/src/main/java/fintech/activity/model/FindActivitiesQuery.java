package fintech.activity.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class FindActivitiesQuery {

    private Long clientId;
    private String action;
    private String topic;

    public static FindActivitiesQuery findByAction(long clientId, String action) {
        return FindActivitiesQuery.builder()
            .clientId(clientId)
            .action(action)
            .build();
    }
}
