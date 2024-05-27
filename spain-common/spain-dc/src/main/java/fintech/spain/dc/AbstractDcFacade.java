package fintech.spain.dc;

import fintech.dc.commands.ChangeDebtStateCommand;
import fintech.dc.commands.RecoverExternalCommand;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.dc.command.RepurchaseDebtCommand;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.command.ReschedulingPreviewCommand;
import fintech.spain.dc.model.ReschedulingPreview;

import java.util.Optional;

public interface AbstractDcFacade {

    Optional<Long> postLoan(Long loanId, String state,String status, boolean triggerActionsImmediately);

    ReschedulingPreview generateReschedulePreview(ReschedulingPreviewCommand command);

    void reschedule(RescheduleCommand command);

    void breakRescheduling(BreakReschedulingCommand command);

    void sell(Long debtId, String company);

    void repurchase(RepurchaseDebtCommand command);

    void externalize(Long debtId, String company);

    void recoverExternal(RecoverExternalCommand command);

    void changeDebtState(ChangeDebtStateCommand command);

}
