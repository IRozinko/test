package fintech.spain.alfa.product.payments.statements;

import fintech.spain.alfa.product.payments.PaymentsSetup;
import fintech.spain.payments.statements.UnnaxPayInStatementParser;
import org.springframework.stereotype.Component;

@Component
public class AlfaUnnaxPayInStatementParser extends UnnaxPayInStatementParser {

    public AlfaUnnaxPayInStatementParser() {
        super(PaymentsSetup.BANK_ACCOUNT_UNNAX);
    }
}
