package fintech.spain.alfa.product.lending.certificate;

import fintech.lending.core.loan.Loan;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Predicate;

import static fintech.spain.alfa.product.cms.CmsSetup.CERTIFICATE_OF_DEBT;
import static fintech.spain.alfa.product.cms.CmsSetup.CERTIFICATE_OF_EARLY_REPAYMENT;

@Getter
@AllArgsConstructor
public enum LoanCertificateType {

    EARLY_REPAYMENT(CERTIFICATE_OF_EARLY_REPAYMENT, Loan::isLoanBeforeEndOfTerm),
    DEBT(CERTIFICATE_OF_DEBT, Loan::isDebt);

    private final String template;
    private final Predicate<Loan> condition;

}
