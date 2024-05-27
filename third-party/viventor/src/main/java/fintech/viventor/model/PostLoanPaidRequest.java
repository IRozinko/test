package fintech.viventor.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PostLoanPaidRequest {

    private LocalDate date;

}
