package fintech.spain.alfa.product.cms;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientIncomingPaymentModel {

    private LocalDate valueDate;
    private BigDecimal amount;
}
