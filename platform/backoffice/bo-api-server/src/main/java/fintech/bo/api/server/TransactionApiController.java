package fintech.bo.api.server;

import fintech.bo.api.model.IdResponse;
import fintech.bo.api.model.permissions.BackofficePermissions;
import fintech.bo.api.model.transaction.VoidTransactionRequest;
import fintech.bo.api.server.services.TransactionApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class TransactionApiController {

    private final TransactionApiService transactionApiService;

    @Autowired
    public TransactionApiController(TransactionApiService transactionApiService) {
        this.transactionApiService = transactionApiService;

    }

    @Secured({BackofficePermissions.ADMIN, BackofficePermissions.TRANSACTION_VOID})
    @PostMapping("/api/bo/transactions/void")
    public IdResponse voidTransaction(@Valid @RequestBody VoidTransactionRequest request) {
        return transactionApiService.voidTransaction(request);
    }
}
