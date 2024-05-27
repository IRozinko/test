package fintech.payments.spi;

import fintech.payments.model.StatementParseResult;

import java.io.InputStream;

public interface StatementParser {

    StatementParseResult parse(InputStream stream);

}
