package fintech.dc;


import fintech.dc.commands.DebtImportCommand;
import fintech.dc.impl.DebtImportServiceBean;

public interface DebtImportService {

    DebtImportServiceBean.ProcessedInfo importDebts(DebtImportCommand command);
}
