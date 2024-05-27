package fintech.lending.core.loan;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@Accessors(chain = true)
public class InstallmentQuery {

    private Long loanId;
    private Long clientId;
    private Long contractId;

    private Boolean closed;
    private Set<InstallmentStatusDetail> statuses = new HashSet<>();
    private Set<InstallmentStatusDetail> excludeStatuses = new HashSet<>();

    public static InstallmentQuery openInstallments(Long loanId) {
        return new InstallmentQuery().setLoanId(loanId).setClosed(false);
    }

    public static InstallmentQuery openContractInstallments(Long contractId) {
        return new InstallmentQuery().setContractId(contractId).setClosed(false);
    }

    public static InstallmentQuery allLoanInstallments(Long loanId) {
        return new InstallmentQuery().setLoanId(loanId);
    }

    public static InstallmentQuery allContractInstallments(Long contractId) {
        return new InstallmentQuery().setContractId(contractId);
    }

}
