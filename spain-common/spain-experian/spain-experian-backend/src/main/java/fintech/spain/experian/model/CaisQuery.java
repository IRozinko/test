package fintech.spain.experian.model;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class CaisQuery {

    private Long clientId;
    private String documentNumber;
    private List<ExperianStatus> status = new ArrayList<>();
    private LocalDateTime createdAfter;

    public static CaisQuery byClientIdOkOrNotFound(Long clientId) {
        CaisQuery query = new CaisQuery();
        query.setClientId(clientId);
        query.setStatus(ImmutableList.of(ExperianStatus.FOUND, ExperianStatus.NOT_FOUND));
        return query;
    }
}
