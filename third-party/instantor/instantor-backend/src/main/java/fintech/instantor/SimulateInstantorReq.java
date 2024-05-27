package fintech.instantor;

import lombok.Builder;

import java.math.BigDecimal;

@Builder(toBuilder = true)
public class SimulateInstantorReq {

    final Long clientId;
    final String dni;
    final String name;
    final String iban;
    final String iban2;
    final String iban3;
    final String account1;
    final String account2;
    final String account3;
    final BigDecimal averageAmountOfIncomingTransactionsMonth;
    final BigDecimal averageAmountOfOutgoingTransactionsMonth;

}
