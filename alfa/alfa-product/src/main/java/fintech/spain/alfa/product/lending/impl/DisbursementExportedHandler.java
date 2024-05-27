package fintech.spain.alfa.product.lending.impl;

import com.google.common.collect.ImmutableMap;
import fintech.Validate;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.commands.DisburseLoanCommand;
import fintech.payments.DisbursementConstants;
import fintech.payments.events.DisbursementExportedEvent;
import fintech.spain.alfa.product.lending.UpsellService;
import fintech.spain.alfa.product.payments.AlfaDisbursementConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Consumer;

@Component
public class DisbursementExportedHandler {

    @Autowired
    private LoanService loanService;

    @Autowired
    private UpsellService upsellService;

    private final Map<String, Consumer<DisbursementExportedEvent>> HANDLERS = ImmutableMap.<String, Consumer<DisbursementExportedEvent>>builder()
        .put(DisbursementConstants.DISBURSEMENT_TYPE_PRINCIPAL, this::disburseLoan)
        .put(AlfaDisbursementConstants.DISBURSEMENT_TYPE_UPSELL, this::disburseUpsell)
        .build();

    @EventListener
    public void disbursementExported(DisbursementExportedEvent event) {
        String disbursementType = event.getDisbursement().getDisbursementType();
        Validate.isTrue(HANDLERS.containsKey(disbursementType), "Unable to find disbursement export handler by type " + disbursementType);
        HANDLERS.get(disbursementType).accept(event);
    }

    private void disburseLoan(DisbursementExportedEvent event) {
        loanService.disburseLoan(DisburseLoanCommand.builder().disbursementId(event.getDisbursement().getId()).build());
    }

    private void disburseUpsell(DisbursementExportedEvent event) {
        upsellService.disburseUpsell(event.getDisbursement().getId());
    }
}
