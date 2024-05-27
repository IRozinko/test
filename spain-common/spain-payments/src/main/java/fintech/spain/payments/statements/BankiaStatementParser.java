package fintech.spain.payments.statements;

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
import java.util.stream.Stream;

import static fintech.BigDecimalUtils.amount;
import static fintech.DateUtils.toLocalDate;
import static fintech.spain.payments.ParserUtil.defaultUniqueKey;
import static fintech.spain.payments.ParserUtil.getCell;
import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.getSourceJsonFromRow;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isEmpty;

@Component
public class BankiaStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "bankia_xls";

    private static final int ROW_INDEX_BANK_ACCOUNT_NUMBER = 0;
    private static final int ROW_INDEX_FIRST_PAYMENT = 5;

    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String accountNumber = accountNumber(sheet);

        List<StatementRow> rows = new ArrayList<>();
        for (int i = ROW_INDEX_FIRST_PAYMENT; i <= sheet.getLastRowNum(); i++) {
            StatementRow row = mapRow(sheet, i, accountNumber);
            row.setAccountNumber(accountNumber);
            rows.add(row);
        }
        Validate.notEmpty(rows, "No rows in statement file");
        LocalDate startDate = Collections.min(rows, Comparator.comparing(StatementRow::getDate)).getDate();
        LocalDate endDate = Collections.max(rows, Comparator.comparing(StatementRow::getDate)).getDate();

        StatementParseResult result = new StatementParseResult();
        result.setAccountNumber(accountNumber);
        result.setAccountCurrency("EUR");
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.setRows(rows);

        return result;
    }

    // 0 - Accounting date
    // 1 - Date of value
    // 2 - Details
    // 3 - Amount
    // 4 - Currency
    // 5 - Balance
    // 6 - Balance currency
    // 7 - Office
    // 8 - Details1
    // 9 - Details2
    // 10 - Details3
    // 11 - Details4
    // 12 - Details5
    // 13 - Details6
    private StatementRow mapRow(Sheet sheet, int rowIndex, String accountNumber) {
        LocalDate valueDate = toLocalDate(getCell(sheet, rowIndex, 0).getDateCellValue());
        LocalDate date = toLocalDate(getCell(sheet, rowIndex, 1).getDateCellValue());

        BigDecimal amount = amount(getCell(sheet, rowIndex, 3).getNumericCellValue());
        BigDecimal balance = amount(getCell(sheet, rowIndex, 5).getNumericCellValue());
        String currency = getCellStringValue(sheet, rowIndex, 4);
        Validate.isTrue(isEmpty(currency) || "EUR".equalsIgnoreCase(currency), "Invalid currency [%s] in row [%s]", currency, rowIndex);

        String details = getCellStringValue(sheet, rowIndex, 2);
        String details1 = getCellStringValue(sheet, rowIndex, 8);
        String details2 = getCellStringValue(sheet, rowIndex, 9);
        String details3 = getCellStringValue(sheet, rowIndex, 10);
        String details4 = getCellStringValue(sheet, rowIndex, 11);
        String details5 = getCellStringValue(sheet, rowIndex, 12);
        String details6 = getCellStringValue(sheet, rowIndex, 13);

        String fullDetails = Stream.of(details, details1, details2, details3, details4, details5, details6)
            .filter(s -> !StringUtils.isBlank(s))
            .collect(joining("\n"));

        StatementRow row = new StatementRow();
        row.setValueDate(valueDate);
        row.setDate(date);
        row.setAmount(amount);
        row.setBalance(balance);
        row.setDescription(fullDetails);
        row.setCurrency("EUR");
        row.setSourceJson(getSourceJsonFromRow(sheet, rowIndex));
        row.setUniqueKey(defaultUniqueKey(row));
        return row;
    }


    private String accountNumber(Sheet sheet) {
        String accountNumber = getCellStringValue(sheet, ROW_INDEX_BANK_ACCOUNT_NUMBER, 0);
        Validate.notEmpty(accountNumber, "No account number found");
        accountNumber = accountNumber.toLowerCase().replace("Últimos movimientos - cuenta: ".toLowerCase(), "").toUpperCase();
        IbanUtil.validate(accountNumber);
        return accountNumber;
    }
}
