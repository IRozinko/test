package fintech.bo.api.model.invoice;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoidInvoiceRequest {

    @NotNull
    private Long invoiceId;

}
