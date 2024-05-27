package fintech.spain.scoring.model;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ScoringQuery {

    private ScoringModelType type;
    private Long clientId;
    private Long applicationId;
    private List<ScoringRequestStatus> statuses = new ArrayList<>();

    public static ScoringQuery byApplicationIdOk(ScoringModelType type, Long applicationId) {
        ScoringQuery query = new ScoringQuery();
        query.setType(type);
        query.setApplicationId(applicationId);
        query.setStatuses(ImmutableList.of(ScoringRequestStatus.OK));
        return query;
    }
}
