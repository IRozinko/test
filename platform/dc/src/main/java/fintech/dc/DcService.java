package fintech.dc;

import fintech.dc.commands.*;
import fintech.dc.model.Debt;

import java.util.Optional;

public interface DcService {

    Long postLoan(PostLoanCommand command);

    Debt get(Long debtId);

    void triggerActions(Long debtId);

    void triggerActionsOnVoidTransaction(Long debtId);

    Long logAction(LogDebtActionCommand command);

    void assignDebt(AssignDebtCommand command);

    void unassignDebt(UnassignDebtCommand command);

    Optional<String> autoAssignDebt(AutoAssignDebtCommand command);

    Optional<Debt> findByLoanId(Long loanId);

    void changeCompany(ChangeCompanyCommand command);

    void edit(EditDebtCommand command);
    void edit(ChangeDebtStateCommand command);

    boolean isSold(Debt debt);

    boolean isExternalized(Debt debt);
}
