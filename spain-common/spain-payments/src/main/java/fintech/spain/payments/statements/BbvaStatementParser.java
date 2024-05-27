package fintech.spain.payments.statements;

import fintech.BigDecimalUtils;
import fintech.DateUtils;
import fintech.Validate;
import fintech.payments.model.StatementParseResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.StatementParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.iban4j.IbanUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static fintech.spain.payments.ParserUtil.defaultUniqueKey;
import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.getSourceJsonFromRow;

@Component
public class BbvaStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "bbva_xls";

    private static final int ROW_INDEX_BANK_ACCOUNT_NUMBER = 6;
    private static final int ROW_INDEX_CURRENCY = 7;
    private static final int ROW_INDEX_PERIOD = 11;
    private static final int ROW_INDEX_FIRST_PAYMENT = 15;

    private static final String DATE_FORMAT = "dd/MM/yyyy";


    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        try (Workbook workbook = WorkbookFactory.create(stream)) {

            Sheet sheet = workbook.getSheetAt(0);

            String accountNumber = accountNumber(sheet);
            String currency = currency(sheet);

            StatementParseResult result = new StatementParseResult();
            result.setAccountNumber(accountNumber);
            result.setAccountCurrency(currency);

            List<StatementRow> rows = new ArrayList<>();
            for (int i = ROW_INDEX_FIRST_PAYMENT; i <= sheet.getLastRowNum(); i++) {
                StatementRow row = mapRow(sheet, i, accountNumber, currency);
                rows.add(row);
            }
            result.setRows(rows);

            LocalDate startDate = Collections.min(rows, Comparator.comparing(StatementRow::getDate)).getDate();
            LocalDate endDate = Collections.max(rows, Comparator.comparing(StatementRow::getDate)).getDate();
            result.setStartDate(startDate);
            result.setEndDate(endDate);

            return result;
        }
    }

    // 1 - Accounting date
    // 2 - Date of value
    // 3 - Code
    // 4 - Operation type
    // 5 - Notes
    // 6 - Amount
    // 7 - Balance
    // 8 - Currency
    // 9 - Office
    private StatementRow mapRow(Sheet sheet, int rowIndex, String accountNumber, String currency) {
        LocalDate valueDate = DateUtils.date(getCellStringValue(sheet, rowIndex, 1), DATE_FORMAT);
        LocalDate date = DateUtils.date(getCellStringValue(sheet, rowIndex, 2), DATE_FORMAT);
        BigDecimal amount = BigDecimalUtils.amount(getCellStringValue(sheet, rowIndex, 6));
        BigDecimal balance = BigDecimalUtils.amount(getCellStringValue(sheet, rowIndex, 7));

        StatementRow row = new StatementRow();
        row.setValueDate(valueDate);
        row.setDate(date);
        row.setAmount(amount);
        row.setBalance(balance);
        row.setTransactionCode(getCellStringValue(sheet, rowIndex, 3));
        row.setDescription(getCellStringValue(sheet, rowIndex, 5));
        row.setAccountNumber(accountNumber);
        row.setCurrency(currency);
        row.setSourceJson(getSourceJsonFromRow(sheet, rowIndex));

        row.setUniqueKey(defaultUniqueKey(row));
        return row;
    }

    private String accountNumber(Sheet sheet) {
        String accountNumber = getCellStringValue(sheet, ROW_INDEX_BANK_ACCOUNT_NUMBER, 2);
        Validate.notEmpty(accountNumber, "No account number found");
        IbanUtil.validate(accountNumber);
        return accountNumber;
    }

    private String currency(Sheet sheet) {
        String currency = getCellStringValue(sheet, ROW_INDEX_CURRENCY, 2);
        Validate.isTrue(StringUtils.equalsIgnoreCase("EUR", currency), "Invalid currency: %s", currency);
        return currency;
    }
}
