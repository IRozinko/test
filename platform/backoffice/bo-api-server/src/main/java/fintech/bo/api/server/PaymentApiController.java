package fintech.bo.api.server;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.institution.UpdateInstitutionRequest;
import fintech.bo.api.model.payments.AddDisbursementSettledTransactionRequest;
import fintech.bo.api.model.payments.AddOtherTransactionRequest;
import fintech.bo.api.model.payments.AddRepaymentTransactionRequest;
import fintech.bo.api.model.payments.OperateOverpaymentTransactionRequest;
import fintech.bo.api.model.payments.UnvoidPaymentRequest;
import fintech.bo.api.model.payments.VoidPaymentRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.filestorage.CloudFile;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.RepayLoanCommand;
import fintech.lending.core.loan.commands.SettleDisbursementCommand;
import fintech.lending.core.overpayment.ApplyOverpaymentCommand;
import fintech.lending.core.overpayment.OverpaymentService;
import fintech.lending.core.overpayment.RefundOverpaymentCommand;
import fintech.lending.core.payments.AddPaymentTransactionCommand;
import fintech.lending.core.payments.LendingPaymentsService;
import fintech.payments.InstitutionService;
import fintech.payments.PaymentService;
import fintech.payments.commands.UpdateInstitutionCommand;
import fintech.payments.impl.PaymentBankStatementsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class PaymentApiController {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private LendingPaymentsService lendingPaymentsService;

    @Autowired
    private OverpaymentService overpaymentService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private PaymentBankStatementsService paymentBankStatementsService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-repayment-transaction")
    public List<IdResponse> addRepaymentTransaction(@Valid @RequestBody AddRepaymentTransactionRequest request) {
        RepayLoanCommand command = new RepayLoanCommand();
        command.setComments(request.getComments());
        command.setLoanId(request.getLoanId());
        command.setPaymentId(request.getPaymentId());
        command.setOverpaymentAmount(request.getOverpaymentAmount());
        command.setPaymentAmount(request.getPaymentAmount());
        command.setValueDate(request.getValueDate());
        List<Long> txIds = loanService.repayLoan(command);
        return txIds.stream().map(IdResponse::new).collect(Collectors.toList());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_VOID})
    @PostMapping("/api/bo/payments/void")
    public void voidPayment(@Valid @RequestBody VoidPaymentRequest request) {
        paymentService.voidPayment(request.getPaymentId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_VOID})
    @PostMapping("/api/bo/payments/unvoid")
    public void voidPayment(@Valid @RequestBody UnvoidPaymentRequest request) {
        paymentService.unvoidPayment(request.getPaymentId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-other-transaction")
    public IdResponse addOtherTransaction(@Valid @RequestBody AddOtherTransactionRequest request) {
        Long txId = lendingPaymentsService.addPaymentTransaction(
            AddPaymentTransactionCommand.builder()
                .paymentId(request.getPaymentId())
                .amount(request.getAmount())
                .comments(request.getComments())
                .clientId(request.getClientId())
                .transactionSubType(request.getTransactionSubType())
                .build()
        );
        return new IdResponse(txId);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-overpayment-transaction")
    public IdResponse addOverpaymentTransaction(@Valid @RequestBody OperateOverpaymentTransactionRequest request) {
        Long txId = overpaymentService.applyOverpayment(
            ApplyOverpaymentCommand.builder()
                .paymentId(request.getPaymentId())
                .clientId(request.getClientId())
                .loanId(request.getLoanId())
                .amount(request.getAmount())
                .comments(request.getComments())
                .build()
        );
        return new IdResponse(txId);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-refund-overpayment-transaction")
    public IdResponse addRefundOverpaymentTransaction(@Valid @RequestBody OperateOverpaymentTransactionRequest request) {
        Long txId = overpaymentService.refundOverpayment(
            RefundOverpaymentCommand.builder()
                .paymentId(request.getPaymentId())
                .clientId(request.getClientId())
                .loanId(request.getLoanId())
                .amount(request.getAmount())
                .comments(request.getComments())
                .build()
        );
        return new IdResponse(txId);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.PAYMENT_ADD_TRANSACTION})
    @PostMapping("/api/bo/payments/add-disbursement-settled-transaction")
    public IdResponse addOverpaymentTransaction(@Valid @RequestBody AddDisbursementSettledTransactionRequest request) {
        Long id = loanService.settleDisbursement(
            new SettleDisbursementCommand()
                .setPaymentId(request.getPaymentId())
                .setAmount(request.getPaymentAmount())
                .setDisbursementId(request.getDisbursementId())
                .setComments(request.getComments()));
        return new IdResponse(id);
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping("/api/bo/payments/update-institution")
    public void addOverpaymentTransaction(@Valid @RequestBody UpdateInstitutionRequest request) {
        UpdateInstitutionCommand command = new UpdateInstitutionCommand();
        command.setInstitutionId(request.getInstitutionId());
        command.setName(request.getName());
        command.setPrimary(request.isPrimary());
        command.setDisabled(request.isDisabled());
        command.setStatementImportFormat(request.getStatementImportFormat());
        command.setStatementExportFormat(request.getStatementExportFormat());
        command.setStatementExportParamsJson(request.getStatementExportParamsJson());

        institutionService.updateInstitution(command);
    }

    @PostMapping("/api/bo/payments/export-bank-statements")
    public CloudFile exportBankStatements(@Valid @RequestBody IdsRequest loans) {
        return paymentBankStatementsService.exportBankStatements(loans.getIds());
    }
}
