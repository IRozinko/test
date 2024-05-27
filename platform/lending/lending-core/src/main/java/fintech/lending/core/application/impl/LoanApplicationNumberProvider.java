package fintech.lending.core.application.impl;

import com.mysema.commons.lang.Assert;
import fintech.lending.core.application.db.LoanApplicationRepository;
import fintech.lending.core.db.Entities;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
@Slf4j
public class LoanApplicationNumberProvider {

    @Autowired
    private LoanApplicationRepository repository;

    private String forcedNumber;

    public String newNumber(final String numberPrefix, final String separator, final int numberLength) {
        if (isNotBlank(forcedNumber)) {
            String newNumber = forcedNumber;
            forcedNumber = null;
            return newNumber;
        }

        Assert.hasLength(numberPrefix, "Empty number prefix");
        long index = repository.count(Entities.loanApplication.number.startsWith(numberPrefix)) + 1;
        for (long i = index; i <= index + 100; i++) {
            String number = numberPrefix + separator + StringUtils.leftPad(Long.toString(i), numberLength, "0");
            if (!repository.exists(Entities.loanApplication.number.eq(number))) {
                log.debug("Generated new loan application number: {}", number);
                return number;
            }
        }
        throw new IllegalStateException("Failed to generate unique loan application number");
    }

    public void setForcedNumber(String forcedNumber) {
        this.forcedNumber = forcedNumber;
    }

}
