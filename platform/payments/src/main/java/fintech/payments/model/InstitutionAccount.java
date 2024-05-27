package fintech.payments.model;

import lombok.Data;

@Data
public class InstitutionAccount {

    private Long id;
    private String accountNumber;
    private Long institutionId;
    private boolean primary;

    private String accountingAccountCode;

}
