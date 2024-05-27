package fintech.spain.inglobaly.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class InglobalyQuery {

    private Long clientId;
    private String documentNumber;
    private LocalDateTime createdAfter;
    private List<InglobalyStatus> status = new ArrayList<>();
}
