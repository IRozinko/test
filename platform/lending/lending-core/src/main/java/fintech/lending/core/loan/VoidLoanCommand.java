package fintech.lending.core.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class VoidLoanCommand {

    @NotNull
    private Long loanId;
    @NotNull
    private LocalDate voidDate;

}
