package fintech.spain.payments.movements;

import fintech.payments.model.BankMovementsFileParseResult;
import fintech.payments.spi.BankMovementsFileParser;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static fintech.spain.payments.ParserUtil.getCellStringValue;
import static fintech.spain.payments.ParserUtil.hasCellValue;
import static fintech.spain.payments.ParserUtil.parseAmount;

@Component
public class IngBankMovementsFileParser implements BankMovementsFileParser {
    private static final int ROW_INDEX_FIRST_PAYMENT = 5;

    @SneakyThrows
    @Override
    public List<BankMovementsFileParseResult> parse(InputStream stream) {
        Workbook workbook = WorkbookFactory.create(stream);
        Sheet sheet = workbook.getSheetAt(0);

        List<BankMovementsFileParseResult> details = new ArrayList<>();
        for (int i = ROW_INDEX_FIRST_PAYMENT; i <= sheet.getLastRowNum(); i++) {
            if (!hasCellValue(sheet, i, 2)) {
                continue;
            }

            String description = getCellStringValue(sheet, i, 3);
            String amountText = getCellStringValue(sheet, i, 4);
            BigDecimal amount = parseAmount(amountText);

            BankMovementsFileParseResult detail = new BankMovementsFileParseResult();
            detail.setDescription(description);
            detail.setAmount(amount);
            details.add(detail);
        }
        return details;
    }

}
