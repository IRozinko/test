package fintech.spain.alfa.bo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanCertificateRequest {

    @NotNull
    private String certificateType;

}
