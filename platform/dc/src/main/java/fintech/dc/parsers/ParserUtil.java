package fintech.dc.parsers;

import fintech.DateUtils;
import fintech.Validate;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class ParserUtil {

    public static String getCellStringValue(Sheet sheet, int rowIndex, int cellIndex) {
        Cell cell = getCell(sheet, rowIndex, cellIndex);
        cell.setCellType(CellType.STRING);
        String value = cell.getStringCellValue();
        Validate.notNull(value, "No value at index %s : %s", rowIndex, cellIndex);
        return value;
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
