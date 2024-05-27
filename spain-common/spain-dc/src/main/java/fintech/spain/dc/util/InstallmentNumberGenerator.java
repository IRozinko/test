package fintech.spain.dc.util;

import fintech.lending.core.loan.Loan;
import fintech.spain.dc.model.ReschedulingPreview;

public interface InstallmentNumberGenerator {

    String generate(Loan loan, ReschedulingPreview.Item item);

}
