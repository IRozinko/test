package fintech.bo.components.client.history;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ClientDataHistory {

    private LocalDateTime timestamp;
    private String value;

}
