package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ViventorPaydaySchedule {

    @JsonProperty("grace_period")
    private Integer gracePeriod = 0;

}
