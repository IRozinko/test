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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static fintech.BigDecimalUtils.amount;
import static fintech.dc.parsers.ParserUtil.getCell;
import static fintech.dc.parsers.ParserUtil.getCellDateValue;
import static fintech.dc.parsers.ParserUtil.getCellStringValue;


@Component
public class UniversalParser implements DebtParser {
    public static final String FORMAT_NAME = "universal_xls";

    private static final int ROW_INDEX_FIRST_DEBT = 1;
    private String company;

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
        result.setCompany(this.company);

        return result;
    }

    // 0. Loan ID
    // 1. Project Name
    // 2. Portfolio name
    // 3. Full name
    // 4. First name
    // 5. Last name
    // 6. Document number
    // 7. DPD
    // 8. Issued amount
    // 9. Principal amount
    // 10. Interest amount
    // 11. Commission amount
    // 12. Penalty amount
    // 13. Total debt amount
    // 14. Account number
    // 15. Bank account client
    // 16. Birthday ("dd.MM.YYYY")
    // 17. Agreement signed date
    // 18. Due date
    // 19. Cession date
    // 20. Email
    // 21. Phone number
    // 22. Postal code
    // 23. Province
    // 24. City
    // 25. Address
    // 26. Product type

    private DebtRow mapRow(Sheet sheet, int rowIndex) {
        BigDecimal loanNumber = amount(getCell(sheet, rowIndex, 0).getNumericCellValue());
        BigDecimal debtNumber = amount(getCell(sheet, rowIndex, 0).getNumericCellValue());
        BigDecimal principalDisbursed = amount(getCell(sheet, rowIndex, 8).getNumericCellValue());
        BigDecimal totalOutstandingAmount = amount(getCell(sheet, rowIndex, 13).getNumericCellValue());
        BigDecimal principalOutstanding = amount(getCell(sheet, rowIndex, 8).getNumericCellValue());
        BigDecimal interestOutstanding = amount(getCell(sheet, rowIndex, 10).getNumericCellValue());
        BigDecimal feeOutstanding = amount(getCell(sheet, rowIndex, 11).getNumericCellValue());
        BigDecimal penaltyOutstanding = amount(getCell(sheet, rowIndex, 12).getNumericCellValue());
        LocalDate issueDate = getCellDateValue(sheet, rowIndex, 17, "dd.MM.yyyy");
        LocalDate dueDate = getCellDateValue(sheet, rowIndex, 18, "dd.MM.yyyy");
//        LocalDate lastPaymentDate = getCellDateValue(sheet, rowIndex, 9,"yyyy-MM-dd");
//        BigDecimal lastPaymentAmount = amount(getCell(sheet, rowIndex, 10).getNumericCellValue());
        String city = getCellStringValue(sheet, rowIndex, 24);
        String postCode = getCellStringValue(sheet, rowIndex, 22);
        LocalDate dateOfBirth = getCellDateValue(sheet, rowIndex, 16,"dd.MM.yyyy");
        String gender = getCellStringValue(sheet, rowIndex, 14);
        String iban = getCellStringValue(sheet, rowIndex, 15);
        String firstName = getCellStringValue(sheet, rowIndex, 4);
        String lastName = getCellStringValue(sheet, rowIndex, 5);
//        String secondLastName = getCellStringValue(sheet, rowIndex, 18);

        String dni = getCellStringValue(sheet, rowIndex, 6);
        String email = getCellStringValue(sheet, rowIndex, 20);
        String phone = getCellStringValue(sheet, rowIndex, 21);
        String province = getCellStringValue(sheet, rowIndex, 23);
        String street = getCellStringValue(sheet, rowIndex, 25);
//        String houseNumber = getCellStringValue(sheet, rowIndex, 24);
//        String doorNumber = getCellStringValue(sheet, rowIndex, 25);

        String company = getCellStringValue(sheet, rowIndex, 1);



        DebtRow row = new DebtRow();
        Optional.of(loanNumber).ifPresent(value -> row.setLoanNumber(value.toString()));
        Optional.of(debtNumber).ifPresent(value -> row.setClientNumber(String.valueOf(value.longValue())));

        Optional.of(principalDisbursed).ifPresent(row::setPrincipalDisbursed);
        Optional.of(totalOutstandingAmount).ifPresent(row::setTotalOutstandingAmount);
        Optional.of(principalOutstanding).ifPresent(row::setPrincipalOutstanding);
        Optional.of(interestOutstanding).ifPresent(row::setInterestOutstanding);
        Optional.of(feeOutstanding).ifPresent(row::setFeeOutstanding);
        Optional.of(penaltyOutstanding).ifPresent(row::setPenaltyOutstanding);
        Optional.of(issueDate).ifPresent(row::setIssueDate);
        Optional.of(dueDate).ifPresent(row::setDueDate);

//        Optional.of(lastPaymentDate).ifPresent(row::setLastPaymentDate);
//        Optional.of(lastPaymentAmount).ifPresent(row::setLastPaymentAmount);
        Optional.of(city).ifPresent(row::setCity);
        Optional.of(postCode).ifPresent(row::setPostCode);
        Optional.of(dateOfBirth).ifPresent(row::setBirthDate);
        Optional.of(gender).ifPresent(row::setGender);
        Optional.of(iban).ifPresent(row::setIban);
        Optional.of(firstName).ifPresent(row::setFirstName);
        Optional.of(lastName).ifPresent(row::setLastName);
//        Optional.of(secondLastName).ifPresent(row::setSecondLastName);

        Optional.of(dni).ifPresent(row::setDni);
        Optional.of(email).ifPresent(row::setEmail);
        Optional.of(phone).ifPresent(row::setPhone);
        Optional.of(province).ifPresent(row::setProvince);
        Optional.of(street).ifPresent(row::setStreet);
//        Optional.of(houseNumber).ifPresent(row::setHouseNumber);
//        Optional.of(doorNumber).ifPresent(row::setDoorNumber);
        Optional<LocalDate> maybeIssueDate = Optional.ofNullable(issueDate);
        Optional<LocalDate> maybeDueDate = Optional.ofNullable(dueDate);
        if (maybeIssueDate.isPresent() && maybeDueDate.isPresent()) {
            Long periodCount = ChronoUnit.DAYS.between(maybeIssueDate.get(), maybeDueDate.get());
            row.setPeriodCount(periodCount);
        }

        Optional.of(company).ifPresent(s -> {
            row.setCompany(s);
            this.company = s;
        });

        return row;
    }

    @Override
    public String getCompany() {
        return this.company;
    }
}
