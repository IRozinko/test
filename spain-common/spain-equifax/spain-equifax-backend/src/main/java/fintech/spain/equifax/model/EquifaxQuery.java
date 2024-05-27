package fintech.spain.equifax.model;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class EquifaxQuery {

    private Long clientId;
    private String documentNumber;
    private List<EquifaxStatus> status = new ArrayList();
    private LocalDateTime createdAfter;

    public static EquifaxQuery byClientIdOkOrNotFound(Long clientId) {
        EquifaxQuery query = new EquifaxQuery();
        query.setClientId(clientId);
        query.setStatus(ImmutableList.of(EquifaxStatus.FOUND, EquifaxStatus.NOT_FOUND));
        return query;
    }

}
