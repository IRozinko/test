package fintech.spain.unnax.statement.impl;


import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.statement.BankStatementsUnnaxClient;
import fintech.spain.unnax.statement.model.BankStatementResponse;
import fintech.spain.unnax.statement.model.BankStatementsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BankStatementsUnnaxClientMock implements BankStatementsUnnaxClient {

    @Override
    public UnnaxResponse<BankStatementResponse> request(BankStatementsRequest request) {
        log.info("Executing bank statements request: {}", request);
        UnnaxResponse<BankStatementResponse> response = new UnnaxResponse<>(null);
        response.setError(false);
        return response;
    }
}
