package fintech.payments

import fintech.payments.model.StatementParseResult
import fintech.payments.spi.StatementParser
import org.springframework.stereotype.Component

@Component
class MockStatementParser implements StatementParser {

    public static final String IMPORT_FORMAT_NAME = "mock_file"

    static fintech.payments.model.StatementParseResult storedResult

    static void setResult(StatementParseResult mockResult) {
        MockStatementParser.storedResult = mockResult
    }

    StatementParseResult parse(InputStream stream) {
        return MockStatementParser.storedResult
    }

}
