package fintech.crm.client;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateClientCommand {

    private final String clientNumber;
    private final String firstName;
    private final String lastName;
    private final String documentNumber;
    private final String phone;
    private final String accountNumber;

    public CreateClientCommand(String clientNumber) {
        this.clientNumber = clientNumber;
        this.firstName = null;
        this.lastName = null;
        this.documentNumber = null;
        this.phone = null;
        this.accountNumber = null;

    }

    public CreateClientCommand(String clientNumber,String firstName, String lastName, String documentNumber, String phone, String accountNumber) {
        this.clientNumber = clientNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.documentNumber = documentNumber;
        this.phone = phone;
        this.accountNumber = accountNumber;
    }
}
