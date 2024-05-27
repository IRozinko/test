package fintech.spain.payments.statements;

import com.google.common.collect.Sets;
import fintech.DateUtils;
import fintech.Validate;
import fintech.payments.model.StatementParseResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.StatementParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.iban4j.IbanUtil;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static fintech.BigDecimalUtils.amount;
import static fintech.spain.payments.ParserUtil.defaultUniqueKey;
import static fintech.spain.payments.ParserUtil.findRowIndexWithTextOptional;
import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.getSourceJsonFromRow;
import static fintech.spain.payments.ParserUtil.hasCellValue;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
public class CaixaStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "caixa_xls";

    private static final String DATE_FORMAT = "dd/MM/yyyy";

    private static final int ROW_INDEX_PERIOD = 1;

    private static final Set<String> HEADER_CELL_TEXT = Sets.newHashSet("Número de compte", "Número de cuenta");

    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String accountNumber = accountNumber(sheet);

        List<StatementRow> rows = new ArrayList<>();
        for (int i = firstPaymentRowIndex(sheet); i <= sheet.getLastRowNum(); i++) {
            if (!hasCellValue(sheet, i, 1)) {
                continue;
            }
            StatementRow row = mapRow(sheet, i, accountNumber);
            rows.add(row);
        }
        Validate.notEmpty(rows, "No rows in statement file");

        Pair<LocalDate, LocalDate> period = period(sheet);
        StatementParseResult result = new StatementParseResult();
        result.setAccountNumber(accountNumber);
        result.setAccountCurrency("EUR");
        result.setStartDate(period.getLeft());
        result.setEndDate(period.getRight());
        result.setRows(rows);
        return result;
    }

    // 1 - Company bank account
    // 2 - Office
    // 3 - Currency
    // 4 - Accounting date
    // 5 - Date
    // 6 - Incoming amount (+)
    // 7 - Outgoing amount (-)
    // 8 - Balance (+)
    // 9 - Balance (-)
    // 10 - 23 - detail feilds
    // 10 - Concepto (tx code?)
    // 11 - Concepto (tx code?)
    // 12 - Reference 1 (always 000000 ?)
    // 13 - Reference 2
    // 14-23 - Concepto...
    private StatementRow mapRow(Sheet sheet, int rowIndex, String accountNumber) {
        LocalDate valueDate = DateUtils.date(getCellStringValue(sheet, rowIndex, 4), DATE_FORMAT);
        LocalDate date = DateUtils.date(getCellStringValue(sheet, rowIndex, 5), DATE_FORMAT);
        BigDecimal amount = amountFromCell(sheet, rowIndex, 6, 7);
        BigDecimal balance = amountFromCell(sheet, rowIndex, 8, 9);
        String currency = getCellStringValue(sheet, rowIndex, 3);
        Validate.isTrue("EUR".equals(currency), "Invalid currency [%s] in row [%s]", currency, rowIndex);

        String details1 = getCellStringValue(sheet, rowIndex, 10);
        String details2 = getCellStringValue(sheet, rowIndex, 11);
        String details3 = getCellStringValue(sheet, rowIndex, 12);
        String details4 = getCellStringValue(sheet, rowIndex, 13);
        String details5 = getCellStringValue(sheet, rowIndex, 14);
        String details6 = getCellStringValue(sheet, rowIndex, 15);
        String details7 = getCellStringValue(sheet, rowIndex, 16);
        String details8 = getCellStringValue(sheet, rowIndex, 17);
        String details9 = getCellStringValue(sheet, rowIndex, 18);
        String details10 = getCellStringValue(sheet, rowIndex, 19);
        String details11 = getCellStringValue(sheet, rowIndex, 20);
        String details12 = getCellStringValue(sheet, rowIndex, 21);
        String details13 = getCellStringValue(sheet, rowIndex, 22);
        String details14 = getCellStringValue(sheet, rowIndex, 23);

        String fullDetails = Stream.of(details1, details1, details2, details3, details4, details5, details6, details7, details8, details9, details10, details11, details12, details13, details14)
            .filter(s -> !isBlank(s))
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

    private BigDecimal amountFromCell(Sheet sheet, int rowIndex, int positiveAmountColumn, int negativeAmountColumn) {
        String positiveAmount = null;
        String negativeAmount = null;
        if (hasCellValue(sheet, rowIndex, positiveAmountColumn)) {
            positiveAmount = getCellStringValue(sheet, rowIndex, positiveAmountColumn);
        }
        if (hasCellValue(sheet, rowIndex, negativeAmountColumn)) {
            negativeAmount = getCellStringValue(sheet, rowIndex, negativeAmountColumn);
        }
        Validate.isTrue(!isBlank(positiveAmount) || !isBlank(negativeAmount), "Could not find positive or negative amount in row [%s] and cells [%s][%s]", rowIndex, positiveAmountColumn, negativeAmountColumn);
        Validate.isTrue(!(!isBlank(positiveAmount) && !isBlank(negativeAmount)), "Found both positive and negative amounts in row [%s] and cells [%s][%s]", rowIndex, positiveAmountColumn, negativeAmountColumn);

        if (!StringUtils.isBlank(positiveAmount)) {
            return amount(positiveAmount);
        } else {
            return amount(negativeAmount).negate();
        }
    }

    private int firstPaymentRowIndex(Sheet sheet) {
        return HEADER_CELL_TEXT.stream()
            .map(text -> findRowIndexWithTextOptional(sheet, 1, text))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(String.format("Row not found with text [%s]", HEADER_CELL_TEXT))) + 1;
    }

    private String accountNumber(Sheet sheet) {
        String accountNumber = getCellStringValue(sheet, firstPaymentRowIndex(sheet), 1);
        Validate.notEmpty(accountNumber, "No account number found");
        accountNumber = StringUtils.replace(accountNumber, " ", "");
        String checkDigit = IbanUtil.calculateCheckDigit("ES00" + accountNumber);
        accountNumber = "ES" + checkDigit + accountNumber;
        IbanUtil.validate(accountNumber);
        return accountNumber;
    }

    // Example:
    // MOVIMIENTOS DESDE : 01/04/2017 HASTA: 30/04/2017
    private Pair<LocalDate, LocalDate> period(Sheet sheet) {
        String periodText = getCellStringValue(sheet, ROW_INDEX_PERIOD, 1);
        Validate.notEmpty(periodText, "No period found");
        periodText = periodText.toUpperCase();
        periodText = StringUtils.replace(periodText, " ", "");
        periodText = StringUtils.replace(periodText, "MOVIMIENTOSDESDE:", "");
        String[] split = periodText.split("HASTA:");
        Validate.isTrue(split.length == 2, "Invalid period: %s", periodText);
        LocalDate from = DateUtils.date(split[0], DATE_FORMAT);
        LocalDate to = DateUtils.date(split[1], DATE_FORMAT);
        return ImmutablePair.of(from, to);
    }
}
