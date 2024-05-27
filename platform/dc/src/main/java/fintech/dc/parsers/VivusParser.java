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
public class VivusParser implements DebtParser {
    public static final String FORMAT_NAME = "vivus_xls";

    private final String company = "Vivus";
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
        result.setCompany(this.company);

        return result;
    }

    // 0. sold_date
    // 1. portfolio_code
    // 2. account_number
    // 3. client_id
    // 4. loan_id
    // 5. delay_bucket
    // 6. current_due_date
    // 7. main_signed_date
    // 8. current_dpd
    // 9. days_since_signed_date
    // 10. total_issued_amount
    // 11. client_bank_account
    // 12. current_principal_amount
    // 13. current_interest_amount
    // 14. current_initial_comission_amount
    // 15. current_penalty_amount
    // 16. current_comission_amount
    // 17. total_debt_amount
    // 18. principal_interest
    // 19. borrower_personal_id
    // 20. borrower_first_name
    // 21. borrower_last_name
    // 22. province
    // 23. city
    // 24. street
    // 25. house_number
    // 26. floor
    // 27. flat
    // 28. postal_code
    // 29. borrower_home_email_address
    // 30. borrower_mobile_phone
    // 31. borrower_home_phone
    // 32. borrower_work_phone
    // 33. date_of_birth
    // 34. original_due_date
    // 35. original_principal
    // 36. extensions
    // 37. cust_type
    // 38. total_interest
    // 39. total_penalty
    // 40. total_commission

    private DebtRow mapRow(Sheet sheet, int rowIndex) {
        BigDecimal loanNumber = amount(getCell(sheet, rowIndex, 4).getNumericCellValue());
        BigDecimal debtNumber = amount(getCell(sheet, rowIndex, 4).getNumericCellValue());
        BigDecimal principalDisbursed = amount(getCell(sheet, rowIndex, 35).getNumericCellValue());
        BigDecimal totalOutstandingAmount = amount(getCell(sheet, rowIndex, 17).getNumericCellValue());
        BigDecimal principalOutstanding = amount(getCell(sheet, rowIndex, 12).getNumericCellValue());
        BigDecimal interestOutstanding = amount(getCell(sheet, rowIndex, 13).getNumericCellValue());
        BigDecimal feeOutstanding = amount(getCell(sheet, rowIndex, 15).getNumericCellValue());

        LocalDate issueDate = getCellDateValue(sheet, rowIndex, 7, "dd.MM.yyyy");
        LocalDate dueDate = getCellDateValue(sheet, rowIndex, 34, "dd.MM.yyyy");
//        LocalDate lastPaymentDate = getCellDateValue(sheet, rowIndex, 9,"yyyy-MM-dd");
//        BigDecimal lastPaymentAmount = amount(getCell(sheet, rowIndex, 10).getNumericCellValue());
        String city = getCellStringValue(sheet, rowIndex, 23);
        String postCode = getCellStringValue(sheet, rowIndex, 28);
        LocalDate dateOfBirth = getCellDateValue(sheet, rowIndex, 33,"dd.MM.yyyy");
//        String gender = getCellStringValue(sheet, rowIndex, 14);
        String iban = getCellStringValue(sheet, rowIndex, 11);
        String firstName = getCellStringValue(sheet, rowIndex, 20);
        String lastName = getCellStringValue(sheet, rowIndex, 21);
//        String secondLastName = getCellStringValue(sheet, rowIndex, 18);

        String dni = getCellStringValue(sheet, rowIndex, 19);
        String email = getCellStringValue(sheet, rowIndex, 29);
        String phone = getCellStringValue(sheet, rowIndex, 30);
        String province = getCellStringValue(sheet, rowIndex, 22);
        String street = getCellStringValue(sheet, rowIndex, 24);
        String houseNumber = getCellStringValue(sheet, rowIndex, 25);
        String doorNumber = getCellStringValue(sheet, rowIndex, 27);

//        String company = getCellStringValue(sheet, rowIndex, 26);



        DebtRow row = new DebtRow();
        Optional.of(loanNumber).ifPresent(value -> row.setLoanNumber(value.toString()));
        Optional.of(debtNumber).ifPresent(value -> row.setClientNumber(String.valueOf(value.longValue())));

        Optional.of(principalDisbursed).ifPresent(row::setPrincipalDisbursed);
        Optional.of(totalOutstandingAmount).ifPresent(row::setTotalOutstandingAmount);
        Optional.of(principalOutstanding).ifPresent(row::setPrincipalOutstanding);
        Optional.of(interestOutstanding).ifPresent(row::setInterestOutstanding);
        Optional.of(feeOutstanding).ifPresent(row::setFeeOutstanding);
        Optional.of(issueDate).ifPresent(row::setIssueDate);
        Optional.of(dueDate).ifPresent(row::setDueDate);

//        Optional.of(lastPaymentDate).ifPresent(row::setLastPaymentDate);
//        Optional.of(lastPaymentAmount).ifPresent(row::setLastPaymentAmount);
        Optional.of(city).ifPresent(row::setCity);
        Optional.of(postCode).ifPresent(row::setPostCode);
        Optional.of(dateOfBirth).ifPresent(row::setBirthDate);
//        Optional.of(gender).ifPresent(row::setGender);
        Optional.of(iban).ifPresent(row::setIban);
        Optional.of(firstName).ifPresent(row::setFirstName);
        Optional.of(lastName).ifPresent(row::setLastName);
//        Optional.of(secondLastName).ifPresent(row::setSecondLastName);

        Optional.of(dni).ifPresent(row::setDni);
        Optional.of(email).ifPresent(row::setEmail);
        Optional.of(phone).ifPresent(row::setPhone);
        Optional.of(province).ifPresent(row::setProvince);
        Optional.of(street).ifPresent(row::setStreet);
        Optional.of(houseNumber).ifPresent(row::setHouseNumber);
        Optional.of(doorNumber).ifPresent(row::setDoorNumber);
        Optional<LocalDate> maybeIssueDate = Optional.ofNullable(issueDate);
        Optional<LocalDate> maybeDueDate = Optional.ofNullable(dueDate);
        if (maybeIssueDate.isPresent() && maybeDueDate.isPresent()) {
            Long periodCount = ChronoUnit.DAYS.between(maybeIssueDate.get(), maybeDueDate.get());
            row.setPeriodCount(periodCount);
        }

        return row;
    }

    @Override
    public String getCompany() {
        return this.company;
    }

}
