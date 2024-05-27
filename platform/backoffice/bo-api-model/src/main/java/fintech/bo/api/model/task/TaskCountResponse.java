package fintech.bo.api.model.task;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskCountResponse {

    private long tasksDue;
}
