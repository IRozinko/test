package fintech.spain.payments.statements;

import fintech.DateUtils;
import fintech.Validate;
import fintech.crm.logins.EmailLoginService;
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

import static fintech.spain.payments.ParserUtil.defaultKey;
import static fintech.spain.payments.ParserUtil.findRowIndexWithText;
import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.getSourceJsonFromRow;
import static fintech.spain.payments.ParserUtil.parseAmount;
import static fintech.spain.payments.ParserUtil.getCellDateValue;
import static fintech.spain.payments.ParserUtil.getCellDateTimeValue;
import static java.util.stream.Collectors.joining;

@Component
public class BjsStatementParser implements StatementParser {
    public static final String FORMAT_NAME = "bjs_xlsx";

    private static final String HEADER_CELL_TEXT = "FECHA";

    private static final String DATE_FORMAT = "MM/dd/yyyy";


    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String currency = "EUR";

        StatementParseResult result = new StatementParseResult();
        result.setAccountCurrency(currency);


        List<StatementRow> rows = new ArrayList<>();
        List<Exception> exceptions = new ArrayList<>();
        int firstPaymentRowIndex = findRowIndexWithText(sheet, 0, HEADER_CELL_TEXT) + 1;
        for (int i = firstPaymentRowIndex; i <= sheet.getLastRowNum(); i++) {
            try {
                StatementRow row = mapRow(sheet, i, currency);
                rows.add(row);
            } catch (IllegalStateException e) {
                exceptions.add(e);
            } catch (NullPointerException e) {
                exceptions.add(e);
            }
        }
        if (rows.isEmpty()) {
            throw new IllegalArgumentException("No valid rows in debt import file");
        }
//        Validate.notEmpty(rows, "No rows in debt import file");
        result.setRows(rows);

        LocalDate startDate = Collections.min(rows, Comparator.comparing(StatementRow::getDate)).getDate();
        LocalDate endDate = Collections.max(rows, Comparator.comparing(StatementRow::getDate)).getDate();
        result.setStartDate(startDate);
        result.setEndDate(endDate);

        return result;
    }

    // 0 - Date
    // 1 - loan_id
    // 2 - dni
    // 3 - estado
    // 4 - importe
    // 5 - portfolio
    // 6 - nobre y appellido
    // 7 - estado
    private StatementRow mapRow(Sheet sheet, int rowIndex, String currency) {
        validateCellNotEmpty(sheet, rowIndex, 2);

        LocalDate date = getCellDateValue(sheet, rowIndex, 0, DATE_FORMAT);
        BigDecimal amount = parseAmount(getCellStringValue(sheet, rowIndex, 4));
        String portfolio = getCellStringValue(sheet, rowIndex, 5);
        String dni = getCellStringValue(sheet, rowIndex, 2);

        String details1 = getCellStringValue(sheet, rowIndex, 3);

        String fullDetails = Stream.of(details1)
            .filter(s -> !StringUtils.isBlank(s))
            .collect(joining("\n"));

        StatementRow row = new StatementRow();
        row.setValueDate(date);
        row.setDni(dni);
        row.setDate(date);
        row.setAmount(amount);
        row.setPortfolio(portfolio);
        row.setDescription(fullDetails);
        row.setCurrency(currency);
        row.setSourceJson(getSourceJsonFromRow(sheet, rowIndex));

        row.setUniqueKey(defaultKey(row));
        return row;
    }

    private void validateCellNotEmpty(Sheet sheet, int rowIndex, int cellIndex) {
        String cellValue = getCellStringValue(sheet, rowIndex, cellIndex);
        if (cellValue == null || cellValue.isEmpty()) {
            throw new IllegalArgumentException("Row is Empty");
        }
    }
}
