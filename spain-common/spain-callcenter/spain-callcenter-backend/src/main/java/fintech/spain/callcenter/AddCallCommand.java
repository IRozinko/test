package fintech.spain.callcenter;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AddCallCommand {

    private Long providerCallId;
    private Long clientId;
    private CallStatus status;
}
