package fintech.spain.alfa.bo.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DocumentCheckUpdateRequest {

    private Long taskId;

    private String firstName;

    private String lastName;

    private String secondLastName;

    private LocalDate dateOfBirth;

    private String accountNumber;

    private String documentNumber;

    private BigDecimal income;

}
