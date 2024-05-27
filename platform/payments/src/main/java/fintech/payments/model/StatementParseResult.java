package fintech.payments.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class StatementParseResult {

    List<StatementRow> rows = new ArrayList<>();

    LocalDate startDate;
    LocalDate endDate;

    String accountNumber;
    String accountCurrency;

    String error;

}
