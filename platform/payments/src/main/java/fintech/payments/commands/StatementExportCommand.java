package fintech.payments.commands;

import fintech.payments.model.Disbursement;
import lombok.Data;

import java.util.List;

@Data
public class StatementExportCommand {
    Long institutionId;
    List<Disbursement> disbursements;
}
