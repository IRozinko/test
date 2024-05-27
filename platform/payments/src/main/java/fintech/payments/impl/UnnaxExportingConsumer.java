package fintech.payments.impl;

import com.google.common.annotations.VisibleForTesting;
import fintech.crm.bankaccount.ClientBankAccount;
import fintech.crm.bankaccount.ClientBankAccountService;
import fintech.crm.client.Client;
import fintech.crm.client.ClientService;
import fintech.db.config.RequiresNew;
import fintech.payments.DisbursementService;
import fintech.payments.model.Disbursement;
import fintech.spain.unnax.UnnaxPayOutService;
import fintech.spain.unnax.db.DisbursementQueueEntity;
import fintech.spain.unnax.db.DisbursementQueueStatus;
import fintech.spain.unnax.transfer.model.TransferAutoRequest;
import fintech.spain.unnax.transfer.model.TransferAutoResponse;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Consumer;

@Slf4j
@Component
public class UnnaxExportingConsumer implements Consumer<LocalDateTime> {

    private static final String TRANSFER_CONCEPT = "Alfa";

    private final DisbursementService disbursementService;
    private final UnnaxPayOutService unnaxPayOutService;
    private final ClientService clientService;
    private final ClientBankAccountService clientBankAccountService;
    private final TransactionTemplate tx;

    public UnnaxExportingConsumer(DisbursementService disbursementService, UnnaxPayOutService unnaxPayOutService,
                                  ClientService clientService, ClientBankAccountService clientBankAccountService,
                                  @RequiresNew TransactionTemplate tx) {
        this.disbursementService = disbursementService;
        this.unnaxPayOutService = unnaxPayOutService;
        this.clientService = clientService;
        this.clientBankAccountService = clientBankAccountService;
        this.tx = tx;
    }

    @Override
    public void accept(LocalDateTime when) {
        unnaxPayOutService.getTransferOutQueue(when).stream()
            .map(el -> Tuple.of(el, exportDisbursement(el)))
            .forEach(this::handleTransferAutoResponse);
    }

    @VisibleForTesting
    protected Optional<TransferAutoResponse> exportDisbursement(DisbursementQueueEntity element) {
        return Try.of(() -> tx.execute(status -> transferAuto(element)))
            .onFailure(ex -> log.error("Error during sending request to Unnax", ex))
            .getOrElse(Optional.empty());
    }

    private Optional<TransferAutoResponse> transferAuto(DisbursementQueueEntity element) {
        return Optional.of(disbursementService.getDisbursement(element.getDisbursementId()))
            .map(this::toTransferRequest)
            .flatMap(unnaxPayOutService::transferOut);
    }

    private void handleTransferAutoResponse(Tuple2<DisbursementQueueEntity, Optional<TransferAutoResponse>> response) {
        DisbursementQueueEntity element = response._1;
        if (response._2.isPresent()) {
            unnaxPayOutService.addAttempt(element.getId(), DisbursementQueueStatus.SUCCESS);
        } else {
            disbursementService.exportError(element.getDisbursementId(), "Error during sending request to Unnax");
            unnaxPayOutService.addAttempt(element.getId(), DisbursementQueueStatus.ERROR);
        }
    }


    @VisibleForTesting
    protected TransferAutoRequest toTransferRequest(Disbursement disbursement) {
        Client client = clientService.get(disbursement.getClientId());
        ClientBankAccount bankAccount = clientBankAccountService.findPrimaryByClientId(disbursement.getClientId())
            .orElseThrow(() -> new IllegalArgumentException(String.format("Client with id [%s] has no primary bank account", disbursement.getClientId())));

        return new TransferAutoRequest()
            .setAmountInEuros(disbursement.getAmount())
            .setDestinationAccount(bankAccount.getAccountNumber())
            .setCustomerCode(client.getNumber())
            .setCustomerNames(client.getFirstAndLastName())
            .setConcept(TRANSFER_CONCEPT)
            .setOrderCode(disbursement.getReference())
            .setBankOrderCode(disbursement.getId().toString());
    }
}
