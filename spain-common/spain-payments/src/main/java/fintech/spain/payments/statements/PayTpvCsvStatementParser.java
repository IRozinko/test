package fintech.spain.payments.statements;

import au.com.bytecode.opencsv.CSVReader;
import fintech.DateUtils;
import fintech.JsonUtils;
import fintech.Validate;
import fintech.lending.creditline.TransactionConstants;
import fintech.payments.model.StatementParseResult;
import fintech.payments.model.StatementRow;
import fintech.payments.spi.StatementParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fintech.BigDecimalUtils.amount;
import static fintech.DateUtils.date;
import static java.util.stream.Collectors.joining;


public class PayTpvCsvStatementParser implements StatementParser {

    public static final String FORMAT_NAME = "paytpv_csv";

    private static final String STATUS_COMPLETED = "Completado";
    private static final String DATE_FORMAT = "d/M/yyyy H:m[:s]";

    private static final String INTER_COMPANY_TRANSFER_OPERATION = "Retirada de fondos";

    private final String accountNumber;
    private final String documentNumberRowAttribute;

    public PayTpvCsvStatementParser(String accountNumber, String documentNumberRowAttribute) {
        this.accountNumber = accountNumber;
        this.documentNumberRowAttribute = documentNumberRowAttribute;
    }

    @SneakyThrows
    @Override
    public StatementParseResult parse(InputStream stream) {

        CSVReader reader = new CSVReader(new InputStreamReader(stream, StandardCharsets.ISO_8859_1), ';', '"', 1);
        List<String[]> csvRows = reader.readAll();
        Validate.notEmpty(csvRows, "No rows in statement CSV file");

        List<StatementRow> rows = csvRows.stream().map(this::mapRow).collect(Collectors.toList());

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

    private StatementRow mapRow(String[] csvRow) {
        String dateText = csvRow[1];
        LocalDate valueDate = date(dateText, DATE_FORMAT);
        LocalDate date = valueDate;
        String id = csvRow[0];
        String reference = csvRow[2];
        String operation = csvRow[3];
        String amountText = csvRow[8];
        BigDecimal amount = parseAmount(amountText);
        String currency = csvRow[9];
        Validate.isTrue("EUR".equals(currency), "Invalid currency [%s] in row [%s]", currency, Arrays.toString(csvRow));
        String concepto = csvRow[11];
        String cardNumber = csvRow[6];

        String status = csvRow[4];
        Validate.isTrue(STATUS_COMPLETED.equalsIgnoreCase(status), "Invalid status [%s] in row [%s]", status, Arrays.toString(csvRow));

        String fullDetails = Stream.of(operation, concepto, reference, cardNumber)
            .filter(s -> !StringUtils.isBlank(s))
            .collect(joining("\n"));

        String documentNumber = "";
        if (!StringUtils.isBlank(concepto)) {
            documentNumber = concepto.toUpperCase().trim();
        } else {
            // extract DNI from reference, example: PAYTPV-70054677N-6dde28fc240578da08db543ab77da3c6
            String[] split = StringUtils.split(reference, '-');
            if (split.length == 3) {
                documentNumber = split[1].toUpperCase().trim();
            }
        }

        StatementRow row = new StatementRow();
        row.setValueDate(valueDate);
        row.setDate(date);
        row.setAmount(amount);
        row.setBalance(amount(0));
        row.setDescription(fullDetails);
        row.setAccountNumber(accountNumber);
        row.setCurrency(currency);
        row.setSourceJson(JsonUtils.writeValueAsString(csvRow));
        row.setReference(reference);
        if (DateUtils.loe(valueDate, date("2017-11-08")) && !StringUtils.isBlank(reference)) {
            // migrated from XLS to CSV format, keep legacy unique key generation for old transactions
            row.setUniqueKey(reference);
        } else {
            String uniqueKey = String.join("|", id, dateText, reference, amountText);
            row.setUniqueKey(uniqueKey);
        }
        if (!StringUtils.isBlank(documentNumber)) {
            row.getAttributes().put(documentNumberRowAttribute, documentNumber);
        }
        if (StringUtils.equalsIgnoreCase(operation, INTER_COMPANY_TRANSFER_OPERATION)) {
            row.setSuggestedTransactionSubType(TransactionConstants.TRANSACTION_SUB_TYPE_INTER_COMPANY_TRANSFER);
        }
        return row;
    }

    private BigDecimal parseAmount(String value) {
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
}
