package fintech.scoring.values.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fintech.BigDecimalUtils;
import fintech.DateUtils;
import fintech.JsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

@NoArgsConstructor
@Data
public class ScoringValueData {

    @Getter
    private ScoringValueSource src;
    @Getter
    private String key;
    @Getter
    private String type;
    @Getter
    private String val;

    public ScoringValueData(ScoringValueSource src, String key, Object val) {
        this.src = src;
        this.key = key;
        this.type = val == null ? null : val.getClass().getSimpleName();
        this.val = valueToString(val);
    }

    @JsonIgnore
    public Object getValAsObject() {
        switch (type) {
            case ScoringValueType.BOOLEAN:
                return Boolean.valueOf(val);
            case ScoringValueType.LOCAL_DATE:
                return DateUtils.date(val);
            case ScoringValueType.LOCAL_DATE_TIME:
                return DateUtils.dateTime(val);
            case ScoringValueType.BIG_DECIMAL:
                return BigDecimalUtils.amount(val);
            case ScoringValueType.LONG:
                return Long.valueOf(val);
            case ScoringValueType.INTEGER:
                return Integer.valueOf(val);
            case ScoringValueType.ARRAY_LIST:
            case ScoringValueType.LINKED_LIST:
                return JsonUtils.readValue(val, ArrayList.class);
            case ScoringValueType.HASH_MAP:
            case ScoringValueType.LINKED_HASH_MAP:
                return JsonUtils.readValue(val, Map.class);
            case ScoringValueType.STRING:
            default:
                return val;
        }
    }

    private String valueToString(Object val) {
        if (val == null) {
            return null;
        }
        switch (type) {
            case ScoringValueType.LONG:
            case ScoringValueType.INTEGER:
            case ScoringValueType.BOOLEAN:
            case ScoringValueType.STRING:
            case ScoringValueType.BIG_DECIMAL:
                return val.toString();
            case ScoringValueType.LOCAL_DATE:
                return DateUtils.toYyyyMmDd((LocalDate) val);
            case ScoringValueType.LOCAL_DATE_TIME:
                return DateUtils.localDateTimeToString((LocalDateTime) val);
            case ScoringValueType.ARRAY_LIST:
            case ScoringValueType.LINKED_LIST:
            case ScoringValueType.HASH_MAP:
            case ScoringValueType.LINKED_HASH_MAP:
                return JsonUtils.writeValueAsString(val);
            default:
                return val.toString();
        }
    }

}
