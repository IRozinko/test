package fintech.spain.alfa.bo.api;

import fintech.JsonUtils;
import fintech.TimeMachine;
import fintech.Validate;
import fintech.bo.api.model.IdRequest;
import fintech.bo.api.model.client.IdsRequest;
import fintech.bo.api.model.dc.*;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.server.security.BackofficeUser;
import fintech.cms.Pdf;
import fintech.dc.DebtImportService;
import fintech.dc.commands.ChangeDebtStateCommand;
import fintech.dc.commands.DebtImportCommand;
import fintech.dc.commands.RecoverExternalCommand;
import fintech.dc.impl.DebtImportServiceBean;
import fintech.filestorage.CloudFile;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanService;
import fintech.spain.alfa.bo.model.*;
import fintech.spain.alfa.product.cms.AlfaCmsModels;
import fintech.spain.alfa.product.cms.CompanyModel;
import fintech.spain.alfa.product.crm.impl.AlfaClientBlacklistService;
import fintech.spain.alfa.product.dc.DcFacade;
import fintech.spain.alfa.product.dc.commands.ReassignDebtCommand;
import fintech.spain.alfa.product.dc.impl.DebtExportService;
import fintech.spain.alfa.product.lending.LoanPrepayment;
import fintech.spain.alfa.product.lending.LoanServicingFacade;
import fintech.spain.alfa.product.lending.UnderwritingFacade;
import fintech.spain.alfa.product.lending.penalty.PenaltyService;
import fintech.spain.alfa.product.strategy.StrategyCmsItemRenderer;
import fintech.spain.alfa.product.web.WebLoginService;
import fintech.spain.crm.client.ClientDeleteService;
import fintech.spain.dc.command.BreakReschedulingCommand;
import fintech.spain.dc.command.RepurchaseDebtCommand;
import fintech.spain.dc.command.RescheduleCommand;
import fintech.spain.dc.command.ReschedulingPreviewCommand;
import fintech.spain.dc.model.ReschedulingPreview;
import fintech.transactions.Balance;
import fintech.transactions.TransactionQuery;
import fintech.transactions.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AlfaBoApiController {

    private final UnderwritingFacade underwritingFacade;
    private final LoanServicingFacade loanServicingFacade;
    private final AlfaApiFacade apiFacade;
    private final WebLoginService webLoginService;
    private final AlfaCmsModels cmsModels;
    private final LoanService loanService;
    private final DcFacade dcFacade;
    private final TransactionService transactionService;
    private final ClientDeleteService clientDeleteService;
    private final PenaltyService penaltyService;
    private final StrategyCmsItemRenderer strategyCmsItemRenderer;
    private final AlfaClientBlacklistService alfaClientBlacklistService;
    private final DebtExportService debtExportService;
    private final DebtImportService debtImportService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_SEND_SMS})
    @PostMapping(path = "/api/bo/spain/alfa/send-offer-sms")
    public void sendOfferSms(@Valid @RequestBody SendCmsNotificationRequest request) {
        underwritingFacade.sendLoanOfferSms(request.getApplicationId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping(path = "/api/bo/spain/alfa/update-client-data")
    public void updateClientData(@Valid @RequestBody UpdateClientDataRequest request) {
        apiFacade.updateClient(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping(path = "/api/bo/spain/alfa/save-identification-document")
    public void saveIdentificationDocument(@Valid @RequestBody SaveIdentificationDocumentRequest request) {
        apiFacade.saveIdentificationDocument(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TASK_PROCESS})
    @PostMapping(path = "/api/bo/spain/alfa/document-check-update")
    public void documentCheckUpdate(@Valid @RequestBody DocumentCheckUpdateRequest request) {
        apiFacade.documentCheckUpdate(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_WEB_LOGIN})
    @PostMapping(path = "/api/bo/spain/alfa/client-web-login")
    public ClientWebLoginResponse clientWebLogin(@AuthenticationPrincipal BackofficeUser user,
                                                 @Valid @RequestBody ClientWebLoginRequest request) {
        String token = webLoginService.loginOnBehalfOfClient(request.getClientId(), user.getUsername());
        CompanyModel company = cmsModels.company();
        String url = company.getWebBaseUrl() + "/bo-login?jwtToken=" + token;
        ClientWebLoginResponse response = new ClientWebLoginResponse();
        response.setJwtToken(token);
        response.setUrl(url);
        return response;
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_RENOUNCE})
    @PostMapping(path = "/api/bo/spain/alfa/renounce-loan")
    public void renounceLoan(@AuthenticationPrincipal BackofficeUser user,
                             @Valid @RequestBody RenounceLoanRequest request) {
        loanServicingFacade.renounceLoan(request.getLoanId(), request.getDate());
    }

    @PostMapping(path = "/api/bo/spain/alfa/calculate-prepayment")
    public CalculatePrepaymentResponse calculatePrepayment(@AuthenticationPrincipal BackofficeUser user,
                                                           @Valid @RequestBody CalculatePrepaymentRequest request) {
        LoanPrepayment prepayment = loanServicingFacade.calculatePrepayment(request.getLoanId(), request.getOnDate());
        CalculatePrepaymentResponse response = new CalculatePrepaymentResponse();
        response.setPrepaymentAvailable(prepayment.isPrepaymentAvailable());
        response.setPrincipalToPay(prepayment.getPrincipalToPay());
        response.setInterestToPay(prepayment.getInterestToPay());
        response.setInterestToWriteOff(prepayment.getInterestToWriteOff());
        response.setPrepaymentFeeToPay(prepayment.getPrepaymentFeeToPay());
        response.setTotalToPay(prepayment.getTotalToPay());
        return response;
    }

    @PostMapping(path = "/api/bo/spain/alfa/calculate-penalty")
    public CalculatePenaltyResponse calculatePenalty(@AuthenticationPrincipal BackofficeUser user,
                                                     @Valid @RequestBody CalculatePrepaymentRequest request) {
        Loan loan = loanService.getLoan(request.getLoanId());
        if (request.getOnDate().isBefore(loan.getPaymentDueDate())) {
            return new CalculatePenaltyResponse().setPenaltyApplicable(false);
        } else {
            Balance balance = transactionService.getBalance(TransactionQuery.byLoan(request.getLoanId()));
            BigDecimal newPenalty = penaltyService.calculatePenalty(request.getLoanId(), request.getOnDate());
            CalculatePenaltyResponse response = new CalculatePenaltyResponse();
            response.setPenaltyApplicable(true);
            response.setPrincipalDue(balance.getPrincipalDue());
            response.setInterestDue(balance.getInterestDue());
            response.setFeeDue(balance.getFeeDue());
            response.setPenaltyDue(balance.getPenaltyDue());
            response.setNewPenalty(newPenalty);
            response.setTotalPenaltyDue(balance.getPenaltyDue().add(newPenalty));
            response.setTotalDue(balance.getTotalDue().add(newPenalty));
            return response;
        }
    }

    @PostMapping(path = "/api/bo/spain/alfa/generate-penalties")
    public void generatePenalty(@AuthenticationPrincipal BackofficeUser user,
                                @Valid @RequestBody IdRequest request) {
        penaltyService.applyPenalty(request.getId(), TimeMachine.today());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_SOFT_DELETE})
    @PostMapping(path = "/api/bo/spain/alfa/soft-delete-client")
    public void softDeleteClient(@Valid @RequestBody IdRequest request) {
        clientDeleteService.softDelete(request.getId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_HARD_DELETE})
    @PostMapping(path = "/api/bo/spain/alfa/hard-delete-client")
    public void hardDeleteClient(@Valid @RequestBody IdRequest request) {
        clientDeleteService.hardDelete(request.getId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.APPLICATION_RETRY})
    @PostMapping(path = "/api/bo/spain/alfa/retry-application")
    public void retryApplication(@Valid @RequestBody RetryLoanApplicationRequest request) {
        underwritingFacade.retryApplication(request.getApplicationId());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_RESCHEDULE})
    @PostMapping("/api/bo/spain/alfa/dc-rescheduling-preview")
    public ReschedulingPreviewResponse generateReschedulingPreview(@RequestBody ReschedulingPreviewRequest request) {
        ReschedulingPreview preview = dcFacade.generateReschedulePreview(new ReschedulingPreviewCommand()
            .setLoanId(request.getLoanId())
            .setNumberOfPayments(request.getNumberOfPayments())
            .setWhen(request.getWhen()));
        String serialized = JsonUtils.writeValueAsString(preview);
        return JsonUtils.readValue(serialized, ReschedulingPreviewResponse.class);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_RESCHEDULE})
    @PostMapping("/api/bo/spain/alfa/reschedule-loan")
    public void rescheduleLoan(@Valid @RequestBody RescheduleLoanRequest request) {
        String serialized = JsonUtils.writeValueAsString(request.getReschedulingPreview());
        dcFacade.reschedule(new RescheduleCommand()
            .setLoanId(request.getLoanId())
            .setPreview(JsonUtils.readValue(serialized, ReschedulingPreview.class))
            .setWhen(TimeMachine.today())
        );
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.LOAN_BREAK_RESCHEDULE})
    @PostMapping("/api/bo/spain/alfa/break-rescheduled-loan")
    public void breakRescheduleLoan(@Valid @RequestBody IdRequest request) {
        dcFacade.breakRescheduling(new BreakReschedulingCommand()
            .setLoanId(request.getId())
            .setWhen(TimeMachine.today())
        );
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping(path = "/api/bo/spain/alfa/add-client-address")
    public void addClientAddress(@Valid @RequestBody AddClientAddressRequest request) {
        apiFacade.addClientAddress(request);
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/spain/alfa/dc/externalize-debt")
    public DebtEditResponse externalizeDebt(@Valid @RequestBody ChangeCompanyRequest request) {
        return runOnDebt(request.getDebtId(), () -> dcFacade.externalize(request.getDebtId(), request.getCompany()));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/spain/alfa/dc/sell-debt")
    public DebtEditResponse sellDebt(@Valid @RequestBody ChangeCompanyRequest request) {
        return runOnDebt(request.getDebtId(), () -> dcFacade.sell(request.getDebtId(), request.getCompany()));
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/spain/alfa/dc/recover-debt")
    public DebtEditResponse recoverExternal(@RequestBody EditDebtRequest request) {
        return runOnDebt(request.getDebtId(), () -> {
            RecoverExternalCommand command = new RecoverExternalCommand()
                .setDebtId(request.getDebtId())
                .setAgent(request.getAgent())
                .setAutoAssign(request.isAutoAssign())
                .setNextAction(request.getNextAction())
                .setNextActionAt(request.getNextActionAt())
                .setStatus(request.getStatus());
            dcFacade.recoverExternal(command);
        });
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/spain/alfa/dc/repurchase-debt")
    public DebtEditResponse repurchase(@RequestBody EditDebtRequest request) {
        return runOnDebt(request.getDebtId(), () -> {
            RepurchaseDebtCommand command = new RepurchaseDebtCommand()
                .setDebtId(request.getDebtId())
                .setAgent(request.getAgent())
                .setAutoAssign(request.isAutoAssign())
                .setNextAction(request.getNextAction())
                .setNextActionAt(request.getNextActionAt())
                .setStatus(request.getStatus())
                .setPortfolio(request.getPortfolio());
            dcFacade.repurchase(command);

        });
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.DC_DEBT_EDIT})
    @PostMapping("/api/bo/spain/alfa/dc/reassign-debt")
    public DebtEditResponse reassign(@RequestBody EditDebtRequest request) {
        return runOnDebt(request.getDebtId(), () -> {
            ReassignDebtCommand command = new ReassignDebtCommand()
                .setDebtId(request.getDebtId())
                .setAgent(request.getAgent())
                .setAutoAssign(request.isAutoAssign())
                .setNextAction(request.getNextAction())
                .setNextActionAt(request.getNextActionAt())
                .setStatus(request.getStatus())
                .setPortfolio(request.getPortfolio());
            dcFacade.reassign(command);
        });
    }

    private DebtEditResponse runOnDebt(Long debtId, Runnable runnable) {
        DebtEditResponse response = new DebtEditResponse();
        response.setDebtId(debtId);
        try {
            runnable.run();
        } catch (RuntimeException e) {
            response.setErrorMessage(e.getMessage());
        }
        return response;

    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.STRATEGIES_VIEW})
    @PostMapping("/api/bo/spain/alfa/preview-strategy-cms-item")
    @SneakyThrows
    public void renderStrategyCmsItem(@RequestBody PreviewCalculationStrategyCmsItemRequest request, HttpServletResponse response) {
        Optional<Pdf> pdfMaybe = strategyCmsItemRenderer.renderStrategy(request.getCalculationStrategyId());
        Validate.isTrue(pdfMaybe.isPresent(), "PDF not generated");
        Pdf pdf = pdfMaybe.get();
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader("Content-Disposition", "inline; filename=\"" + pdf.getName() + "\"");
        response.setContentLength(pdf.getContent().length);
        try (ServletOutputStream os = response.getOutputStream()) {
            IOUtils.write(pdf.getContent(), os);
            os.flush();
        }
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping(path = "/api/bo/spain/alfa/blacklist-client")
    public void blacklistClient(@Valid @RequestBody BlacklistClientRequest request) {
        alfaClientBlacklistService.blacklistClient(request.getClientId(), request.getComment());
    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.CLIENT_EDIT})
    @PostMapping(path = "/api/bo/spain/alfa/unblacklist-client")
    public void unBlacklistClient(@Valid @RequestBody IdRequest clientRequest) {
        alfaClientBlacklistService.unblacklistClient(clientRequest.getId());
    }

    @Secured({BackofficePermissions.ADMIN})
    @PostMapping("/api/bo/alfa/export-debts")
    @SneakyThrows
    public CloudFile exportDebts(@Valid @RequestBody IdsRequest loans) {
        return debtExportService.export(loans.getIds());
    }
    @Secured({BackofficePermissions.ADMIN})
    @PostMapping("/api/bo/dc/import-debts")
    public DebtImportServiceBean.ProcessedInfo importDebt(@RequestBody ImportDebtRequest request) {
        DebtImportCommand command = new DebtImportCommand();
        command.setDebtImportId(request.getInstitutionId());
        command.setFileId(request.getFileId());
        command.setCompanyName(request.getCompanyName());
        command.setPortfolioName(request.getPortfolioName());
        command.setStatus(request.getDebtStatus());
        command.setState(request.getDebtState());
        return debtImportService.importDebts(command);
    }
    @Secured({BackofficePermissions.ADMIN})
    @PostMapping("/api/bo/dc/save-debt-status")
    public DebtEditResponse saveDebtStatus(@RequestBody SaveDebtStatusRequest request) {
        return runOnDebt(request.getDebtId(), () -> {
            ChangeDebtStateCommand command = new ChangeDebtStateCommand()
                .setDebtId(request.getDebtId())
                .setState(request.getState())
                .setStatus(request.getStatus())
                .setSubStatus(request.getSubStatus());
            dcFacade.changeDebtState(command);
        });
    }
}
