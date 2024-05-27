package fintech.spain.crosscheck.model;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

@ToString
@Data
@Accessors(chain = true)
public class SpainCrosscheckInput {

    private String dni;
    private String email;
    private String phone;
}
