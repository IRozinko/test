package fintech.spain.payments.statements;

import au.com.bytecode.opencsv.CSVReader;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.payments.model.StatementParseResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.StatementParser;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static fintech.BigDecimalUtils.amount;
import static fintech.DateUtils.date;

@RequiredArgsConstructor
public class UnnaxPayInStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "unnax_payin_csv";
    private static final String DATE_FORMAT_ES = "d/M/yyyy H:m[:s]";
    private static final String DATE_FORMAT_EN = "M/d/yyyy H:m[:s]";

    private final String accountNumber;

    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {
        CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.ISO_8859_1), ',', '"', 0);
        List<String[]> csvRows = reader.readAll();
        Validate.notEmpty(csvRows, "No rows in statement CSV file");

        String dateFormat = csvRows.get(0)[0].equals("Payment Method") ? DATE_FORMAT_EN : DATE_FORMAT_ES;
        List<StatementRow> rows = csvRows.stream().skip(1).map(r -> mapRow(r, dateFormat)).filter(Objects::nonNull).collect(Collectors.toList());

        LocalDate startDate = Collections.min(rows, Comparator.comparing(StatementRow::getDate)).getDate();
        LocalDate endDate = Collections.max(rows, Comparator.comparing(StatementRow::getDate)).getDate();

        StatementParseResult result = new StatementParseResult();
        result.setAccountNumber(accountNumber);
        result.setAccountCurrency("EUR");
        result.setStartDate(startDate);
        result.setEndDate(endDate);
        result.setRows(rows);

        return result;
    }

    @Nullable
    private StatementRow mapRow(String[] csvRow, String dateFormat) {
        if (csvRow.length <= 6) return null;//as usual last row is fake with size 1
        String dateText = csvRow[1];
        LocalDate valueDate = date(dateText, dateFormat);

        String id = csvRow[2];
        String operation = csvRow[4];
        String amountText = csvRow[5];
        BigDecimal amount = BigDecimal.valueOf(parseAmount(amountText)).movePointLeft(2);
        String currency = "EUR";

        StatementRow row = new StatementRow();
        row.setValueDate(valueDate);
        row.setDate(valueDate);
        row.setAmount(amount);
        row.setBalance(amount(0));
        row.setDescription("UNX " + id);
        row.setAccountNumber(accountNumber);
        row.setCurrency(currency);
        row.setSourceJson(JsonUtils.writeValueAsString(csvRow));
        row.setReference(operation);
        row.setUniqueKey(id);
        return row;
    }

    private Integer parseAmount(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(String.format("Invalid amount value: %s", value));
        }
    }
}
