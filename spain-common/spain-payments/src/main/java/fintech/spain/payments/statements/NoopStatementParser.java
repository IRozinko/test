package fintech.spain.payments.statements;

import com.google.common.collect.Lists;
import fintech.payments.InstitutionService;
import fintech.payments.model.Institution;
import fintech.payments.model.InstitutionAccount;
import fintech.payments.model.StatementParseResult;
import fintech.payments.spi.StatementParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;

import static java.time.LocalDate.now;

@Component
public class NoopStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "dummy_bank_xls";

    @Autowired
    private InstitutionService institutionService;

    @Override
    public StatementParseResult parse(InputStream stream) {
        Institution primaryInstitution = institutionService.getPrimaryInstitution();
        InstitutionAccount primaryAccount = primaryInstitution.getPrimaryAccount();


        StatementParseResult statementParseResult= new StatementParseResult();
        statementParseResult.setRows(Lists.newArrayList());
        statementParseResult.setStartDate(now());
        statementParseResult.setEndDate(now());
        statementParseResult.setAccountNumber(primaryAccount.getAccountNumber());
        statementParseResult.setAccountCurrency("EUR");
        statementParseResult.setError("");
        return statementParseResult;
    }

}
