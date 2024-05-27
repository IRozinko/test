package fintech.webitel.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class WebitelAuthToken {
    private String key;
    private String token;
    private Long expires;
    private String username;
}
