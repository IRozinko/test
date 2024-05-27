package fintech.spain.payments;

import fintech.DateUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.payments.model.StatementRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

public class ParserUtil {

    public static String uniqueKey(String... chunks) {
        StringBuilder sb = new StringBuilder();
        for (String chunk : chunks) {
            sb.append("[");
            chunk = StringUtils.replace(chunk, " ", "");
            chunk = StringUtils.replace(chunk, "\n", "");
            chunk = StringUtils.replace(chunk, "\r", "");
            chunk = StringUtils.lowerCase(chunk);
            sb.append(chunk);
            sb.append("]");
        }
        return sb.toString();
    }

    public static String defaultUniqueKey(StatementRow row) {
        return uniqueKey(
            row.getAccountNumber(),
            DateUtils.toYyyyMmDd(row.getDate()),
            row.getAmount().toString(),
            row.getDescription(),
            row.getBalance().toString()
        );
    }

    public static String defaultKey(StatementRow row) {
        return uniqueKey(
            row.getAccountNumber(),
            DateUtils.toYyyyMmDd(row.getDate()),
            row.getAmount().toString(),
            row.getDescription()
        );
    }

    public static String getCellStringValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue();
        Validate.notNull(value, "No value at index %s : %s", rowIndex, cellIndex);
        return value;
    }

    public static boolean hasCellValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        if (cell == null) {
            return false;
        }
        if (cell.getCellTypeEnum() == CellType.STRING) {
            return !isBlank(cell.getStringCellValue());
        }
        return cell.getCellTypeEnum() != CellType.BLANK;
    }

    public static LocalDate getCellDateValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        Date value = cell.getDateCellValue();
        Validate.notNull(value, "No value at index %s : %s", rowIndex, cellIndex);
        return DateUtils.toLocalDate(value);
    }

    public static Optional<Integer> findRowIndexWithTextOptional(Sheet sheet, int cellIndex, String text) {
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex < sheet.getLastRowNum(); rowIndex++) {
            if (sheet.getRow(rowIndex) == null || sheet.getRow(rowIndex).getCell(cellIndex) == null) {
                continue;
            }

            String cellValue = getCellStringValue(sheet, rowIndex, cellIndex);
            if (equalsIgnoreCase(trimToEmpty(cellValue), text)) {
                return Optional.of(rowIndex);
            }
        }
        return Optional.empty();
    }

    public static int findRowIndexWithText(Sheet sheet, int cellIndex, String text) {
        return findRowIndexWithTextOptional(sheet, cellIndex, text)
            .orElseThrow(() -> new IllegalArgumentException(String.format("Row not found with text [%s]", text)));
    }

    public static String getSourceJsonFromRow(Sheet sheet, int rowIndex) {
        List<String> source = new ArrayList<>();
        Row row = sheet.getRow(rowIndex);
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            source.add(getCellStringValue(sheet, rowIndex, i));
        }
        return JsonUtils.writeValueAsString(source);
    }

    public static BigDecimal parseAmount(String value) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        String pattern = "#,##0.00";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        try {
            return (BigDecimal) decimalFormat.parse(value);
        } catch (ParseException e) {
            throw new IllegalArgumentException(String.format("Invalid amount value: %s", value));
        }
    }

    public static LocalDate getCellDateValue(Sheet sheet, int rowIndex, int cellIndex, String pattern) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        if (cell != null) {
            if (cell.getCellTypeEnum() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                Date value = cell.getDateCellValue();
                return DateUtils.toLocalDate(value);
            } else if (cell.getCellTypeEnum() == CellType.STRING) {
                String dateString = cell.getStringCellValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDate.parse(dateString, formatter);
            } else {
                // Handle other cell types or throw an exception
                throw new IllegalArgumentException("Cell is not a valid date format at row " + rowIndex + " and column " + cellIndex);
            }
        } else {
            throw new IllegalArgumentException("No cell found at row " + rowIndex + " and column " + cellIndex);
        }
    }
    public static LocalDateTime getCellDateTimeValue(Sheet sheet, int rowIndex, int cellIndex, String pattern) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        if (cell != null) {
            if (cell.getCellTypeEnum() == CellType.STRING) {
                String dateString = cell.getStringCellValue();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
                return LocalDateTime.parse(dateString, formatter);
            } else {
                // Handle other cell types or throw an exception
                throw new IllegalArgumentException("Cell is not a valid dateTime format at row " + rowIndex + " and column " + cellIndex);
            }
        } else {
            throw new IllegalArgumentException("No cell found at row " + rowIndex + " and column " + cellIndex);
        }
    }

    public static Cell getCell(Sheet sheet, int rowIndex, int cellIndex) {
        Row row = sheet.getRow(rowIndex);
        Validate.notNull(row, "Row not found at index %s", rowIndex);
        Cell cell = row.getCell(cellIndex);
        Validate.notNull(cell, "Cell not found at index %s : %s", rowIndex, cellIndex);
        return cell;
    }
}
