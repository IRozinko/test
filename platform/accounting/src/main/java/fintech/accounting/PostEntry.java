package fintech.accounting;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class PostEntry {

    private LocalDate bookingDate;
    private LocalDate valueDate;
    private String accountCode;
    private EntryType entryType;
    private BigDecimal amount;

}
