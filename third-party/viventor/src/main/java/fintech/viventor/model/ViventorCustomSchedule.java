package fintech.viventor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class ViventorCustomSchedule {

    private List<ViventorCustomScheduleItem> items = newArrayList();

    @JsonProperty("prepaid_items")
    private Integer prepaidItems = 0;

    @JsonProperty("grace_period")
    private Integer gracePeriod = 0;

}
