package fintech.payments.spi;

import fintech.payments.model.BankMovementsFileParseResult;

import java.io.InputStream;
import java.util.List;

public interface BankMovementsFileParser {

    List<BankMovementsFileParseResult> parse(InputStream stream);
}
