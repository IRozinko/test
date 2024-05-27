package fintech.spain.payments.exporters;

import lombok.Data;

@Data
public class SepaExporterParams {

    private String accountOwnerName;
    private String accountOwnerOrgId;
    private String accountOwnerBic;

}
