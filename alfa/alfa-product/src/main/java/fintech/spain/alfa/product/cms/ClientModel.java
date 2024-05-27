package fintech.spain.alfa.product.cms;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientModel {

    private String number;
    private String documentNumber;
    private String email;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String secondLastName;
    private String fullName;
    private String iban;
    private String ibanFormatted;

    private String addressLine1;
    private String addressLine2;

    private String registrationIpAddress;
    private LocalDateTime registeredAt;
    private boolean acceptMarketing;
    private BigDecimal creditLimit;
    private BigDecimal loyaltyDiscount;
}
