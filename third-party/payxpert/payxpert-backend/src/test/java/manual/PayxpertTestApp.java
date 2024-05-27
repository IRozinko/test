package manual;

import fintech.payxpert.CardAuthorizationRequestCommand;
import fintech.payxpert.PayxpertPaymentRequest;
import fintech.payxpert.PayxpertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static fintech.BigDecimalUtils.amount;

@SpringBootApplication(scanBasePackages = "fintech")
public class PayxpertTestApp implements CommandLineRunner {

    @Autowired
    private PayxpertService service;

    public static void main(String[] args) {
        System.setProperty("payxpert.provider", "live-payxpert");
        SpringApplication.run(PayxpertTestApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        PayxpertPaymentRequest request = service.cardAuthorizationRequest(new CardAuthorizationRequestCommand()
            .setClientId(101L)
            .setAmount(amount(0.01))
            .setCurrency("EUR")
            .setCallbackUrl("https://0de49bd9.ngrok.io/api/public/web/payxpert/callback")
//            .setRedirectUrl("https://0de49bd9.ngrok.io/api/public/web/payxpert/redirect")
        );
        System.out.println("URL: " + request.getCustomerRedirectUrl());
    }
}
