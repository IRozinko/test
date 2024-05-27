package fintech.bo.components.common.field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class IntegerRange {
    private Integer min;
    private Integer max;
}
