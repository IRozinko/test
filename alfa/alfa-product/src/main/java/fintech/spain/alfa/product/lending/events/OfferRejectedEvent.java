package fintech.spain.alfa.product.lending.events;

import lombok.Data;

@Data
public class OfferRejectedEvent {
    private final Long clientId;
}
