package fintech.bo.components.dc;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;

@Data
@Accessors(chain = true)
public class DebtFilterRequest {

    private Integer minDpd;
    private Integer maxDpd;
    private String agent;
    private String portfolio;
    private String status;
    private String loanStatusDetail;
    private String managingCompany;
    private String owningCompany;

    private LocalDate nextActionFrom;
    private LocalDate nextActionTo;
    private String whereSql;
    private String aging;
    private String subStatus;
}
