package fintech.spain.experian.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaisRequest {

    private Long clientId;
    private Long applicationId;
    private String documentNumber;
}
