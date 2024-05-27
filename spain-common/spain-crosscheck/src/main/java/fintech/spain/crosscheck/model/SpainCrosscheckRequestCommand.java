package fintech.spain.crosscheck.model;

import lombok.Data;

@Data
public class SpainCrosscheckRequestCommand {

    private Long clientId;
    private Long applicationId;
    private Long loanId;
    private String dni;
    private String email;
    private String phone;
}
