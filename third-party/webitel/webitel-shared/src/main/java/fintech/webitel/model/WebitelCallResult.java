package fintech.webitel.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WebitelCallResult {
    private String status;
    private String info;
}
