package fintech.crm.client;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@EqualsAndHashCode(of = "segment")
@Data
public class ClientSegment {

    private String segment;
    private LocalDateTime addedAt;
}
