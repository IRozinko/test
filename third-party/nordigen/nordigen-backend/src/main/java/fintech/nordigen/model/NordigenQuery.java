package fintech.nordigen.model;

import com.google.common.collect.ImmutableList;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class NordigenQuery {

    private Long clientId;
    private Long applicationId;
    private Long instantorResponseId;
    private List<NordigenStatus> statuses = new ArrayList<>();

    public static NordigenQuery byInstantorResponseIdOk(Long id) {
        NordigenQuery query = new NordigenQuery();
        query.setInstantorResponseId(id);
        query.setStatuses(ImmutableList.of(NordigenStatus.OK));
        return query;
    }
}
