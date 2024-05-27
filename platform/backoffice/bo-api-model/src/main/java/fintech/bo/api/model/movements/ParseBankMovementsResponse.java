package fintech.bo.api.model.movements;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ParseBankMovementsResponse {
    private List<Long> disbursementIds;
}
