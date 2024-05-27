package fintech.spain.alfa.web.models;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class LoansResponse {

    private List<LoanData> loans;
}
