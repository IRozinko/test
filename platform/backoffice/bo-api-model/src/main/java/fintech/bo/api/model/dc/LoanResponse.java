package fintech.bo.api.model.dc;

import lombok.Data;

import java.util.List;

@Data
public class LoanResponse {

    private List<Long> loanIds;
    private String errorMessage;
}
