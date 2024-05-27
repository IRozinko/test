package fintech.spain.payments.statements;

import fintech.BigDecimalUtils;
import fintech.DateUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.payments.model.StatementParseResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.StatementParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
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
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class SabadellStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "sabadell_xls";
    private static final int ROW_INDEX_BANK_ACCOUNT_NUMBER = 3;
    private static final int ROW_INDEX_CURRENCY = 4;
    private static final String HEADER_CELL_TEXT = "F. Operativa";
    private static final String IBAN_SEPARATOR = "ES25";

    private static final String DATE_FORMAT = "dd/MM/yyyy";


    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String accountNumber = accountNumber(sheet);
        String currency = currency(sheet);

        StatementParseResult result = new StatementParseResult();
        result.setAccountNumber(accountNumber);
        result.setAccountCurrency(currency);

        List<StatementRow> rows = new ArrayList<>();
        for (int i = firstPaymentRowIndex(sheet); i <= sheet.getLastRowNum(); i++) {
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

    // 0 - Accounting date
    // 1 - Details
    // 2 - Date
    // 3 - Amount
    // 4 - Balance
    // 5 - Reference 1
    // 6 - Reference 2
    private StatementRow mapRow(Sheet sheet, int rowIndex, String accountNumber, String currency) {
        LocalDate valueDate = DateUtils.date(getCellStringValue(sheet, rowIndex, 0), DATE_FORMAT);
        LocalDate date = DateUtils.date(getCellStringValue(sheet, rowIndex, 2), DATE_FORMAT);
        BigDecimal amount = BigDecimalUtils.amount(getCellStringValue(sheet, rowIndex, 3));
        BigDecimal balance = BigDecimalUtils.amount(getCellStringValue(sheet, rowIndex, 4));

        String details = getCellStringValue(sheet, rowIndex, 1);
        String reference1 = "";
        String reference2 = "";
        if (hasCellValue(sheet, rowIndex, 5)) {
            reference1 = getCellStringValue(sheet, rowIndex, 5);
        }
        if (hasCellValue(sheet, rowIndex, 6)) {
            reference2 = getCellStringValue(sheet, rowIndex, 6);
        }

        String fullDetails = Stream.of(details, reference1, reference2)
            .filter(s -> !StringUtils.isBlank(s))
            .collect(joining("\n"));

        StatementRow row = new StatementRow();
        row.setValueDate(valueDate);
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
        String accountNumber = getCellStringValue(sheet, ROW_INDEX_BANK_ACCOUNT_NUMBER, 1);
        Validate.notEmpty(accountNumber, "No account number found");
        accountNumber = StringUtils.replace(accountNumber, "-", "");
        accountNumber = IBAN_SEPARATOR.concat(accountNumber);
        IbanUtil.validate(accountNumber);
        return accountNumber;
    }

    private String currency(Sheet sheet) {
        String currency = getCellStringValue(sheet, ROW_INDEX_CURRENCY, 1);
        Validate.isTrue(StringUtils.equalsIgnoreCase("EUR", currency), "Invalid currency: %s", currency);
        return currency;
    }

    private int firstPaymentRowIndex(Sheet sheet) {
        return findRowIndexWithText(sheet, 0, HEADER_CELL_TEXT) + 1;
    }

    private boolean hasCellValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        if (cell == null) {
            return false;
        }
        if (cell.getCellTypeEnum() == CellType.STRING) {
            return !isBlank(cell.getStringCellValue());
        }
        return cell.getCellTypeEnum() != CellType.BLANK;
    }

    private String getSourceJsonFromRow(Sheet sheet, int rowIndex) {
        List<String> source = new ArrayList<>();
        Row row = sheet.getRow(rowIndex);
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            source.add(getCellStringValue(sheet, rowIndex, i));
        }
        return JsonUtils.writeValueAsString(source);
    }

    private String getCellStringValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        if (cell == null) {
            return "";
        } else {
            cell.setCellType(CellType.STRING);
            return cell.getStringCellValue();
        }
    }

    private Cell getCell(Sheet sheet, int rowIndex, int cellIndex) {
        Row row = sheet.getRow(rowIndex);
        Validate.notNull(row, "Row not found at index %s", rowIndex);
        return row.getCell(cellIndex);
    }
}
