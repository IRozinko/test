package fintech.spain.alfa.web.controllers.web;

import fintech.spain.alfa.web.config.security.WebApiUser;
import fintech.spain.alfa.web.models.InvoiceInfo;
import fintech.spain.alfa.web.services.WebInvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
class InvoiceApi {

    @Autowired
    private WebInvoiceService invoiceService;

    @GetMapping("/api/web/invoices")
    public List<InvoiceInfo> invoices(@AuthenticationPrincipal WebApiUser user) {
        return invoiceService.listInvoices(user.getClientId());
    }

}
