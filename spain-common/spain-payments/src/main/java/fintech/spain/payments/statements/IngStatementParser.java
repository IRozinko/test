package fintech.spain.payments.statements;

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
import java.util.stream.Stream;

import static fintech.spain.payments.ParserUtil.defaultUniqueKey;
import static fintech.spain.payments.ParserUtil.findRowIndexWithText;
import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.getSourceJsonFromRow;
import static fintech.spain.payments.ParserUtil.parseAmount;
import static java.util.stream.Collectors.joining;

@Component
public class IngStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "ing_xls";

    private static final String BANK_ACCOUNT_CELL_TEXT = "Número de cuenta:";
    private static final String HEADER_CELL_TEXT = "FECHA";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String accountNumber = accountNumber(sheet);
        String currency = "EUR";

        StatementParseResult result = new StatementParseResult();
        result.setAccountNumber(accountNumber);
        result.setAccountCurrency(currency);


        List<StatementRow> rows = new ArrayList<>();
        int firstPaymentRowIndex = findRowIndexWithText(sheet, 0, HEADER_CELL_TEXT) + 1;
        for (int i = firstPaymentRowIndex; i <= sheet.getLastRowNum(); i++) {
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

    // 0 - Date
    // 1 - excluded
    // 2 - Description
    // 3 - Description 2
    // 4 - Amount
    // 5 - Balance
    private StatementRow mapRow(Sheet sheet, int rowIndex, String accountNumber, String currency) {
        LocalDate date = DateUtils.date(getCellStringValue(sheet, rowIndex, 0), DATE_FORMAT);
        BigDecimal amount = parseAmount(getCellStringValue(sheet, rowIndex, 4));
        BigDecimal balance = parseAmount(getCellStringValue(sheet, rowIndex, 5));

        String details = getCellStringValue(sheet, rowIndex, 2);
        String details1 = getCellStringValue(sheet, rowIndex, 3);

        String fullDetails = Stream.of(details, details1)
            .filter(s -> !StringUtils.isBlank(s))
            .collect(joining("\n"));

        StatementRow row = new StatementRow();
        row.setValueDate(date);
        row.setDate(date);
        row.setAmount(amount);
        row.setBalance(balance);
        row.setDescription(fullDetails);
        row.setAccountNumber(accountNumber);
        row.setCurrency(currency);
        row.setSourceJson(getSourceJsonFromRow(sheet, rowIndex));

        row.setUniqueKey(defaultUniqueKey(row));
        return row;
    }

    private String accountNumber(Sheet sheet) {
        int bankAccountRow = findRowIndexWithText(sheet, 0, BANK_ACCOUNT_CELL_TEXT);
        String accountNumber = getCellStringValue(sheet, bankAccountRow, 1);
        Validate.notEmpty(accountNumber, "No account number found");
        accountNumber = StringUtils.replace(accountNumber, " ", "");
        // account number in the statement lacks check digit, calculate on the fly...
        String checkDigit = IbanUtil.calculateCheckDigit("ES00" + accountNumber);
        accountNumber = "ES" + checkDigit + accountNumber;
        IbanUtil.validate(accountNumber);
        return accountNumber;
    }

    public static void main(String[] args) {
        String accountNumber = StringUtils.replace("21000844240200657804", " ", "");
        // account number in the statement lacks check digit, calculate on the fly...
        String checkDigit = IbanUtil.calculateCheckDigit("ES00" + accountNumber);
        accountNumber = "ES" + checkDigit + accountNumber;
        IbanUtil.validate(accountNumber);
        System.out.println(accountNumber);
    }
}
