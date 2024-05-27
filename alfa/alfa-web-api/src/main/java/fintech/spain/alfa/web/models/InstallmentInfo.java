package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class InstallmentInfo {

    private String installmentNumber;
    private String statusDetail;
    private LocalDate dueDate;
    private BigDecimal totalDue;
    private BigDecimal totalInvoiced;
    private BigDecimal totalPaid;
}
