package fintech.dc.parsers;

import fintech.Validate;
import fintech.dc.DebtParser;
import fintech.dc.model.DebtParseResult;
import fintech.dc.model.DebtRow;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.dc.parsers.ParserUtil.getCell;
import static fintech.dc.parsers.ParserUtil.getCellDateTimeValue;
import static fintech.dc.parsers.ParserUtil.getCellDateValue;
import static fintech.dc.parsers.ParserUtil.getCellStringValue;


@Component
public class MoneymanParser implements DebtParser {
    public static final String FORMAT_NAME = "moneyman_xls";

    private final String company = "Moneyman";
    private static final int ROW_INDEX_FIRST_DEBT = 1;

    @SneakyThrows
    @Override
    public DebtParseResult parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        String accountNumber = getCellStringValue(sheet, 0, 0);
        Validate.notEmpty(accountNumber, "No account number found");

        List<DebtRow> rows = new ArrayList<>();
        for (int i = ROW_INDEX_FIRST_DEBT; i <= sheet.getLastRowNum(); i++) {
            DebtRow row = mapRow(sheet, i);
            rows.add(row);
        }
        Validate.notEmpty(rows, "No rows in debt import file");

        DebtParseResult result = new DebtParseResult();
        result.setRows(rows);

        return result;
    }
    private DebtRow mapRow(Sheet sheet, int rowIndex) {
        BigDecimal loanNumber = amount(getCell(sheet, rowIndex, 0).getNumericCellValue());
        BigDecimal debtNumber = amount(getCell(sheet, rowIndex, 1).getNumericCellValue());
        BigDecimal principalDisbursed = amount(getCell(sheet, rowIndex, 2).getNumericCellValue());
        BigDecimal totalOutstandingAmount = amount(getCell(sheet, rowIndex, 3).getNumericCellValue());
        BigDecimal principalOutstanding = amount(getCell(sheet, rowIndex, 4).getNumericCellValue());
        BigDecimal interestOutstanding = amount(getCell(sheet, rowIndex, 5).getNumericCellValue());
        BigDecimal feeOutstanding = amount(getCell(sheet, rowIndex, 6).getNumericCellValue());
        LocalDateTime issueDate = getCellDateTimeValue(sheet, rowIndex, 11, "yyyy-MM-dd HH:mm:ss");
        LocalDate dueDate = getCellDateValue(sheet, rowIndex, 20, "yyyy-MM-dd");
        LocalDate lastPaymentDate = getCellDateValue(sheet, rowIndex, 22,"yyyy-MM-dd");
        BigDecimal lastPaymentAmount = amount(getCell(sheet, rowIndex, 24).getNumericCellValue());
        String city = getCellStringValue(sheet, rowIndex, 108);
        String postCode = getCellStringValue(sheet, rowIndex, 109);
        LocalDate dateOfBirth = getCellDateValue(sheet, rowIndex, 110,"yyyy-MM-dd");
        String gender = getCellStringValue(sheet, rowIndex, 111);
        String iban = getCellStringValue(sheet, rowIndex, 130);
        String firstName = getCellStringValue(sheet, rowIndex, 132);
        String lastName = getCellStringValue(sheet, rowIndex, 133);
        String secondLastName = getCellStringValue(sheet, rowIndex, 134);

        String dni = getCellStringValue(sheet, rowIndex, 135);
        String email = getCellStringValue(sheet, rowIndex, 137);
        String phone = getCellStringValue(sheet, rowIndex, 136);
        String province = getCellStringValue(sheet, rowIndex, 139);
        String street = getCellStringValue(sheet, rowIndex, 140);
        String houseNumber = getCellStringValue(sheet, rowIndex, 141);
        String doorNumber = getCellStringValue(sheet, rowIndex, 142);



        DebtRow row = new DebtRow();
        Optional.of(loanNumber).ifPresent(value -> row.setLoanNumber(value.toString()));
        Optional.of(debtNumber).ifPresent(value -> row.setClientNumber(String.valueOf(value.longValue())));

        Optional.of(principalDisbursed).ifPresent(row::setPrincipalDisbursed);
        Optional.of(totalOutstandingAmount).ifPresent(row::setTotalOutstandingAmount);
        Optional.of(principalOutstanding).ifPresent(row::setPrincipalOutstanding);
        Optional.of(interestOutstanding).ifPresent(row::setInterestOutstanding);
        Optional.of(feeOutstanding).ifPresent(row::setFeeOutstanding);
        Optional.of(issueDate).ifPresent(value -> row.setIssueDate(value.toLocalDate()));
        Optional.of(dueDate).ifPresent(row::setDueDate);

        Optional.of(lastPaymentDate).ifPresent(row::setLastPaymentDate);
        Optional.of(lastPaymentAmount).ifPresent(row::setLastPaymentAmount);
        Optional.of(city).ifPresent(row::setCity);
        Optional.of(postCode).ifPresent(row::setPostCode);
        Optional.of(dateOfBirth).ifPresent(row::setBirthDate);
        Optional.of(gender).ifPresent(row::setGender);
        Optional.of(iban).ifPresent(row::setIban);
        Optional.of(firstName).ifPresent(row::setFirstName);
        Optional.of(lastName).ifPresent(row::setLastName);
        Optional.of(secondLastName).ifPresent(row::setSecondLastName);

        Optional.of(dni).ifPresent(row::setDni);
        Optional.of(email).ifPresent(row::setEmail);
        Optional.of(phone).ifPresent(row::setPhone);
        Optional.of(province).ifPresent(row::setProvince);
        Optional.of(street).ifPresent(row::setStreet);
        Optional.of(houseNumber).ifPresent(row::setHouseNumber);
        Optional.of(doorNumber).ifPresent(row::setDoorNumber);
        Optional<LocalDateTime> maybeIssueDate = Optional.ofNullable(issueDate);
        Optional<LocalDate> maybeDueDate = Optional.ofNullable(dueDate);
        if (maybeIssueDate.isPresent() && maybeDueDate.isPresent()) {
            Long periodCount = ChronoUnit.DAYS.between(maybeIssueDate.get().toLocalDate(), maybeDueDate.get());
            row.setPeriodCount(periodCount);
        }

        return row;
    }
    @Override
    public String getCompany() {
        return this.company;
    }

}
