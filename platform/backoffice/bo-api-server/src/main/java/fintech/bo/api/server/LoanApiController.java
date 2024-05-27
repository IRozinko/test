package fintech.bo.api.server;


import fintech.TimeMachine;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.loan.BreakLoanRequest;
import fintech.bo.api.model.loan.UseOverpaymentRequest;
import fintech.bo.api.model.loan.VoidLoanRequest;
import fintech.bo.api.model.loan.WriteOffLoanAmountRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.filestorage.CloudFile;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.VoidLoanCommand;
import fintech.lending.core.loan.commands.BreakLoanCommand;
import fintech.lending.core.loan.commands.ClosePaidLoanCommand;
import fintech.lending.core.loan.commands.RepayLoanWithOverpaymentCommand;
import fintech.lending.core.loan.commands.UnBreakLoanCommand;
import fintech.lending.core.loan.commands.WriteOffAmountCommand;
import fintech.lending.core.overpayment.OverpaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoanApiController {

    @Autowired
    private LoanService loanService;

    @Autowired
    private OverpaymentService overpaymentService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_BREAK})
    @PostMapping("/api/bo/loan/break")
    public void breakLoan(@Valid @RequestBody BreakLoanRequest request) {
        BreakLoanCommand command = new BreakLoanCommand();
        command.setLoanId(request.getLoanId());
        command.setWhen(TimeMachine.today());
        command.setReasonForBreak(request.getReasonForBreak());
        loanService.breakLoan(command);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_BREAK})
    @PostMapping("/api/bo/loan/un-break")
    public void unBreakLoan(@Valid @RequestBody IdRequest request) {
        loanService.unBreakLoan(new UnBreakLoanCommand(request.getId(), TimeMachine.today()));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_VOID})
    @PostMapping("api/bo/loan/close-paid-loan")
    public void closePaidLoan(@Valid @RequestBody IdRequest request) {
        loanService.closePaidLoan(new ClosePaidLoanCommand(request.getId()));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_VOID})
    @PostMapping("/api/bo/loan/void")
    public void voidLoan(@Valid @RequestBody VoidLoanRequest request) {
        VoidLoanCommand command = new VoidLoanCommand();
        command.setLoanId(request.getLoanId());
        command.setVoidDate(TimeMachine.today());
        loanService.voidLoan(command);
    }


    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_WRITE_OFF})
    @PostMapping("/api/bo/loan/write-off")
    public void writeOff(@Valid @RequestBody WriteOffLoanAmountRequest request) {
        WriteOffAmountCommand command = new WriteOffAmountCommand()
            .setPrincipal(request.getPrincipal())
            .setInterest(request.getInterest())
            .setPenalty(request.getPenalty())
            .setLoanId(request.getLoanId())
            .setFee(request.getFee())
            .setComments(request.getComment())
            .setSubType(request.getSubType())
            .setWhen(request.getWhen());
        loanService.writeOffAmount(command);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.OVERPAYMENT_USE})
    @PostMapping("/api/bo/loan/use-overpayment")
    public void useOverpayment(@Valid @RequestBody UseOverpaymentRequest request) {
        overpaymentService.userOverpayment(RepayLoanWithOverpaymentCommand.builder()
            .loanId(request.getLoanId())
            .amount(request.getAmount())
            .comments(request.getComments())
            .when(TimeMachine.today())
            .build());
    }

    @PostMapping("/api/bo/loan/agreements/export")
    public CloudFile exportLoanAgreements(@Valid @RequestBody IdsRequest loans) {
        return loanService.exportAgreements(loans.getIds());
    }
}
