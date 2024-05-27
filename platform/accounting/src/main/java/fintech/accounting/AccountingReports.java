package fintech.accounting;

import java.util.List;
import java.util.Map;

public interface AccountingReports {

    List<AccountTrialBalance> getTrialBalance(ReportQuery query);

    Map<String, AccountTurnover> getTurnover(ReportQuery query);
}

