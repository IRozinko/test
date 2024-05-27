package fintech.spain.experian.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConcursalesRequest {

    private Long clientId;
    private String documentNumber;
}
