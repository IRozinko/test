package fintech.spain.unnax.statement;

import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.statement.model.BankStatementResponse;
import fintech.spain.unnax.statement.model.BankStatementsRequest;

public interface BankStatementsUnnaxClient {

    UnnaxResponse<BankStatementResponse> request(BankStatementsRequest request);
}
