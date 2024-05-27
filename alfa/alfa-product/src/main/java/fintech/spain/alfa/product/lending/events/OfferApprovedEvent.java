package fintech.spain.alfa.product.lending.events;

import lombok.Data;

@Data
public class OfferApprovedEvent {
    private final Long clientId;
}
