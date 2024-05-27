package fintech.bo.api.server;

import fintech.TimeMachine;
import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.invoice.GenerateInvoiceRequest;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.lending.core.invoice.commands.GenerateInvoiceCommand;
import fintech.lending.core.loan.LoanService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class InvoiceApiController {

    @Autowired
    private LoanService loanService;

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.INVOICE_GENERATE})
    @PostMapping(path = "/api/bo/invoices/generate")
    public IdResponse generateInvoice(@RequestBody GenerateInvoiceRequest request) {
        Long invoiceId = loanService.generateInvoice(GenerateInvoiceCommand.builder()
            .loanId(request.getLoanId())
            .dateTo(request.getDateTo())
            .invoiceDate(TimeMachine.today())
            .generateFile(true)
            .sendFile(false)
            .manual(true)
            .build());

        return new IdResponse(invoiceId);
    }

}
