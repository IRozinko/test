package fintech.spain.asnef;

import java.time.LocalDate;
import java.util.List;

public interface FileAsnefService {

    Long generateRpFile(List<Long> loanIds, LocalDate when);

    Long generateFotoaltasFile(List<Long> loanIds, LocalDate when);
}
