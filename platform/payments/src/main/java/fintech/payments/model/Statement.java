package fintech.payments.model;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Statement {

    private Long id;
    private Long fileId;
    private String fileName;
    private Long institutionId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String accountNumber;

    private StatementStatus status;

    private String error;

    private Integer rowsTotal;
    private Integer rowsIgnored;

}
