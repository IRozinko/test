package fintech.payments;

import fintech.payments.commands.StatementImportCommand;
import fintech.payments.model.Statement;
import fintech.payments.model.StatementRow;

import java.util.List;
import java.util.Optional;

public interface StatementService {

    Long importStatement(StatementImportCommand command);

    void processStatement(Long statementId);

    Optional<Statement> findStatement(Long statementId);

    List<StatementRow> findStatementRows(Long statementId);

    Optional<StatementRow> findStatementRowByPayment(Long paymentId);

}
