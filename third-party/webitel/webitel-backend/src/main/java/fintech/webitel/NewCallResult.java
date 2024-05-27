package fintech.webitel;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class NewCallResult {
    private String status;
    private String info;
}
