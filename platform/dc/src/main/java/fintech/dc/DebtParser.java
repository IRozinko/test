package fintech.dc;


import fintech.dc.model.DebtParseResult;

import java.io.InputStream;

public interface DebtParser {

    DebtParseResult parse(InputStream stream);

    String getCompany();
}
