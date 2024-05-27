package fintech.excel;

import fintech.DateUtils;
import fintech.Validate;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class ExistingExcelDocument {

    private static final String dateFormat = "m/d/yy";
    private static final String dateTimeFormat = "m/d/yy h:mm";

    private XSSFWorkbook workbook;

    private int rowNum = 0;
    private boolean written;

    private CellStyle dateStyle;
    private CellStyle dateTimeStyle;
    private CellStyle numberStyle;


    @SneakyThrows
    public ExistingExcelDocument(InputStream file) {
        workbook = new XSSFWorkbook(file);

        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));

        dateTimeStyle = workbook.createCellStyle();
        dateTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateTimeFormat));

        numberStyle = workbook.createCellStyle();
        numberStyle.setDataFormat(workbook.createDataFormat().getFormat("0.00"));
    }

    @SneakyThrows
    public void write(OutputStream os) {
        Validate.isTrue(!this.written, "Excel document already written to output stream");

        workbook.setActiveSheet(0);
        XSSFFormulaEvaluator.evaluateAllFormulaCells(workbook);

        workbook.write(os);
        this.written = true;
    }

    public ExistingExcelDocument row(String sheetName, List<Object> values) {
        XSSFSheet sheet = workbook.getSheet(sheetName);

        Row row = sheet.createRow(rowNum++);
        for (int col = 0; col < values.size(); col++) {
            Object val = values.get(col);
            if (val != null) {
                Cell cell = row.createCell(col);
                setCellValue(cell, val);
            }
        }

        return this;
    }

    private void setCellValue(Cell cell, Object val) {
        if (val instanceof LocalDate) {
            cell.setCellStyle(dateStyle);
            cell.setCellValue((DateUtils.toDate((LocalDate) val)));
        } else if (val instanceof Date) {
            cell.setCellStyle(dateStyle);
            cell.setCellValue((Date) val);
        } else if (val instanceof LocalDateTime) {
            cell.setCellStyle(dateTimeStyle);
            cell.setCellValue((DateUtils.toDate((LocalDateTime) val)));
        } else if (val instanceof Number) {
            cell.setCellStyle(numberStyle);
            cell.setCellValue(((Number) val).doubleValue());
        } else if (val instanceof Boolean) {
            cell.setCellValue((Boolean) val);
        } else {
            cell.setCellValue(Objects.toString(val));
        }
    }

}
