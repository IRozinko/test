package fintech.accounting;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BookTransactionCommand {

    private Long transactionId;
    private List<PostEntry> entries = new ArrayList<>();

}
