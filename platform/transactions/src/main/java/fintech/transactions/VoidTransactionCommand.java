package fintech.transactions;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VoidTransactionCommand {

    private Long id;
    private LocalDate bookingDate;
    private LocalDate voidedDate;
}
