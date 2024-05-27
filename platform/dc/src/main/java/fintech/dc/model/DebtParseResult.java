package fintech.dc.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class DebtParseResult {

    List<DebtRow> rows = new ArrayList<>();

    String error;
    String company;
    String portfolio;
    String state;
    String status;

}
