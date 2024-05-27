package fintech.spain.alfa.product.payments.statements;

import fintech.spain.alfa.product.payments.PaymentsSetup;
import fintech.spain.payments.statements.PayTpvCsvStatementParser;
import fintech.spain.alfa.product.AlfaConstants;
import org.springframework.stereotype.Component;

@Component
public class AlfaPaytpvStatementParser extends PayTpvCsvStatementParser {

    public AlfaPaytpvStatementParser() {
        super(PaymentsSetup.BANK_ACCOUNT_PAY_TPV, AlfaConstants.STATEMENT_ROW_ATTRIBUTE_DOCUMENT_NUMBER);
    }
}
