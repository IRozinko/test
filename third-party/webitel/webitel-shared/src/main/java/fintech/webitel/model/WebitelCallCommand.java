package fintech.webitel.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(exclude = {"token", "key"})
@Accessors(chain = true)
public class WebitelCallCommand {
    private String token;
    private String key;

    private String destinationNumber;
    private String callFromUser;
}
