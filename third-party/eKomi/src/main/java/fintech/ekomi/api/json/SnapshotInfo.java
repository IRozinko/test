package fintech.ekomi.api.json;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SnapshotInfo {

    @SerializedName("fb_count")
    private Long count;

    @SerializedName("fb_avg")
    private BigDecimal average;
}
