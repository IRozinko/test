package fintech.spain.callcenter;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class CallQuery {
    private Long clientId;
    private Long providerId;
    private Set<CallStatus> statuses = new HashSet<>();
}
