package fintech.viventor.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

@Data
public class ViventorLoan {

    private ViventorLoanType type;

    private BigDecimal amount;

    private BigDecimal interest;

    private String currency;

    private LocalDate startDate;

    private LocalDate dueDate;

    private LocalDateTime placementDate;

    private LocalDateTime listingDate;

    private LocalDateTime closeDate;

    private List<ViventorScheduleItem> paymentSchedule = newArrayList();

    private ViventorBorrowerConsumer consumer;

    private boolean buyback;

    private String purpose;

    private BigDecimal remainingPrincipal;

    private BigDecimal investmentAmount = BigDecimal.ZERO;

    private int investmentCount = 0;

    private int currentExtensionNumber = 0;

}
