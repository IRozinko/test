package fintech.excel

import org.apache.poi.xssf.usermodel.XSSFWorkbook
import spock.lang.Specification

import static fintech.DateUtils.*

class ExcelDocumentTest extends Specification {

    def "Export"() {
        when:
        def output = new ByteArrayOutputStream()
        new ExcelDocument("Test")
                .header(["Text", "Number", "Date", "Time", "Boolean"] as String[])
                .row(["A", 1.01g, date("2016-01-01"), dateTime("2016-01-01 12:00:00"), true] as Object[])
                .write(output)
        def workbook = new XSSFWorkbook(new ByteArrayInputStream(output.toByteArray()))
        def sheet = workbook.getSheetAt(0)

        then: "Header is present"
        sheet.getRow(0).getCell(0).stringCellValue == "Text"
        sheet.getRow(0).getCell(1).stringCellValue == "Number"
        sheet.getRow(0).getCell(2).stringCellValue == "Date"

        then: "Row has right values"
        sheet.getRow(1).getCell(0).stringCellValue == "A"
        sheet.getRow(1).getCell(1).numericCellValue == 1.01d
        sheet.getRow(1).getCell(2).dateCellValue == toDate(date("2016-01-01"))
        sheet.getRow(1).getCell(3).dateCellValue == toDate(dateTime("2016-01-01 12:00:00"))
        sheet.getRow(1).getCell(4).booleanCellValue
    }
}
