package fintech.payments.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentAutoProcessingResult {
    
    private boolean paymentProcessed;
    
    public static PaymentAutoProcessingResult notProcessed(){
        return new PaymentAutoProcessingResult(false);
    }
    
    public static PaymentAutoProcessingResult processed() {
        return new PaymentAutoProcessingResult(true);
    }
}
