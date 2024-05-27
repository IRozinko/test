package fintech.payxpert.events;

import com.payxpert.connect2pay.client.response.PaymentStatusResponse;
import lombok.Getter;

@Getter
public class PayxpertResponseProcessed extends AbstractPayxpertEvent {

    private PaymentStatusResponse response;

    public PayxpertResponseProcessed(Long id, Long clientId, PaymentStatusResponse response) {
        super(id, clientId);
        this.response = response;
    }
}
