package fintech.spain.unnax.statement.impl;


import fintech.spain.unnax.UnnaxClient;
import fintech.spain.unnax.model.UnnaxResponse;
import fintech.spain.unnax.statement.BankStatementsUnnaxClient;
import fintech.spain.unnax.statement.model.BankStatementResponse;
import fintech.spain.unnax.statement.model.BankStatementsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Primary
@Component
@ConditionalOnProperty(name = "unnax.mock", havingValue = "false")
public class BankStatementsUnnaxClientImpl extends UnnaxClient implements BankStatementsUnnaxClient {

    public static final String BANK_STATEMENTS_URL = "/api/v3/reader/statements/pdf_bulk/";

    public BankStatementsUnnaxClientImpl(@Autowired @Qualifier("unnaxClient") RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    public UnnaxResponse<BankStatementResponse> request(BankStatementsRequest request) {
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(BANK_STATEMENTS_URL, new HttpEntity<>(request), String.class);
        return getIfSuccess(responseEntity, 200, BankStatementResponse.class);
    }
}
