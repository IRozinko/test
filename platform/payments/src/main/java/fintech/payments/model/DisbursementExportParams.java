package fintech.payments.model;

import lombok.Data;

import java.util.List;

@Data
public class DisbursementExportParams {

    private Institution institution;
    private InstitutionAccount institutionAccount;
    private List<Disbursement> disbursements;
    
}
