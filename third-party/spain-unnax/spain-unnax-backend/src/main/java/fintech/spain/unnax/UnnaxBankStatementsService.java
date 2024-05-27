package fintech.spain.unnax;

import java.time.LocalDate;
import java.util.Map;

public interface UnnaxBankStatementsService {

    void requestStatementsUpload(LocalDate from, LocalDate to, String sourceIban);

    Map<String, LocalDate> lastSuccessRequestedDateByIban();

}
