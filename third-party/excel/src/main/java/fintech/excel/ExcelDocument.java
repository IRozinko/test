package fintech.excel;

import fintech.DateUtils;
import fintech.Validate;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import static com.google.common.base.Throwables.propagate;

public class ExcelDocument {

    public static final String dateFormat = "m/d/yy";
    public static final String dateTimeFormat = "m/d/yy h:mm";
    private CellStyle numberStyle;

    private SXSSFWorkbook wb;
    private SXSSFSheet sh;

    private CellStyle dateStyle;
    private CellStyle dateTimeStyle;
    private CellStyle headerStyle;

    private int rowNum = 0;
    private boolean written;

    public ExcelDocument(String sheetName) {
        wb = new SXSSFWorkbook(100);
        wb.setCompressTempFiles(true);
        sh = wb.createSheet(sheetName);
        sh.trackAllColumnsForAutoSizing();

        CreationHelper createHelper = wb.getCreationHelper();
        dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateFormat));

        dateTimeStyle = wb.createCellStyle();
        dateTimeStyle.setDataFormat(createHelper.createDataFormat().getFormat(dateTimeFormat));

        headerStyle = wb.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        Font font = wb.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        numberStyle = wb.createCellStyle();
        numberStyle.setDataFormat(wb.createDataFormat().getFormat("0.00"));
    }

    public ExcelDocument header(String[] headers) {
        Row row = sh.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellStyle(headerStyle);
            cell.setCellValue(headers[i]);
        }
        return this;
    }

    public ExcelDocument row(Object[] values) {
        Row row = sh.createRow(++rowNum);

        for (int col = 0; col < values.length; col++) {
            Object val = values[col];
            if (val != null) {
                Cell cell = row.createCell(col);
                setCellValue(cell, val);
            }
            if (rowNum == 1) {
                sh.autoSizeColumn(col);
            }
        }
        return this;
    }

    public void write(OutputStream os) {
        Validate.isTrue(!this.written, "Excel document already written to output stream");
        try {
            wb.write(os);
        } catch (IOException e) {
            throw propagate(e);
        } finally {
            wb.dispose();
            this.written = true;
        }
    }

    private void setCellValue(Cell cell, Object val) {
        if (val instanceof LocalDate) {
            cell.setCellStyle(dateStyle);
            cell.setCellValue((DateUtils.toDate((LocalDate) val)));
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
