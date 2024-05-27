package fintech.spain.alfa.web.services;

import fintech.lending.core.loan.InstallmentQuery;
import fintech.lending.core.loan.Loan;
import fintech.lending.core.loan.LoanQuery;
import fintech.lending.core.loan.LoanService;
import fintech.lending.core.loan.ScheduleService;
import fintech.spain.alfa.web.models.InstallmentInfo;
import fintech.spain.alfa.web.models.convertor.InstallmentInfoConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Component
public class WebInstallmentService {

    private final LoanService loanService;
    private final ScheduleService scheduleService;

    @Autowired
    public WebInstallmentService(LoanService loanService, ScheduleService scheduleService) {
        this.loanService = loanService;
        this.scheduleService = scheduleService;
    }

    public List<InstallmentInfo> findAllActiveLoanInstallments(Long clientId) {
        return loanService.findLastLoan(LoanQuery.openLoans(clientId))
            .map(Loan::getId)
            .map(InstallmentQuery::allLoanInstallments)
            .map(scheduleService::findInstallments)
            .orElse(Collections.emptyList())
            .stream()
            .map(InstallmentInfoConverter.INSTANCE::convert)
            .collect(Collectors.toList());
    }
}
