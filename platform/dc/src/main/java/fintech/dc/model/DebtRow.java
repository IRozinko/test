package fintech.dc.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DebtRow {

    private Long loanId;
    private Long clientId;
    private BigDecimal amount;
    private BigDecimal amountInterest;
    private BigDecimal amountPenalties;

    private String firstName;
    private String lastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String iban;
    private LocalDate birthDate;

    private Long daysPastDue;
    private Long periodCount;

    private LocalDate issueDate;
    private LocalDate dueDate;

    private String ProductType;

    private String loanNumber;
    private String debtNumber;
    private String clientNumber;
    private BigDecimal principalDisbursed;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal principalOutstanding;
    private BigDecimal interestOutstanding;
    private BigDecimal feeOutstanding;
    private BigDecimal penaltyOutstanding;
    private LocalDate lastPaymentDate;
    private BigDecimal lastPaymentAmount;
    private String city;
    private String postCode;
    private LocalDate dateOfBirth;
    private String gender;
    private String secondLastName;
    private String dni;
    private String province;
    private String street;
    private String houseNumber;
    private String doorNumber;

    private String company;

}
