package fintech.bo.components.dc.batch;

import fintech.bo.api.model.dc.EditDebtRequest;
import org.jooq.Record;

import static fintech.bo.db.jooq.dc.Tables.DEBT;
import static java.util.Objects.isNull;

public class EditDebtRequestBuilder {

    public static EditDebtRequest build(Record record, EditDebtDialog.EditDebtForm editedDebt) {
        return new EditDebtRequest()
            .setDebtId(record.get(DEBT.ID))
            .setAgent(editedDebt.getAgent())
            .setStatus(editedDebt.getStatus())
            .setAutoAssign(isNull(editedDebt.getAgent()))
            .setNextAction(editedDebt.getNextAction())
            .setNextActionAt(editedDebt.getNextActionAt())
            .setPortfolio(editedDebt.getPortfolio());
    }
}
