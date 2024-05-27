package fintech.spain.payments.exporters;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SepaModel {

    private String fileName;
    private String msgId;
    private String exportDateTime;
    private String accountOwnerName;
    private String accountOwnerOrgId;
    private String accountOwnerIban;
    private String accountOwnerBic;
    private String executionDate;
    private long numberOfTransactions;
    private List<Payment> payments = new ArrayList<>();

    @Data
    public static class Payment {
        private String id;
        private BigDecimal amount;
        private String creditorName;
        private String creditorIban;
        private String endToEndId;
        private String reference;
    }
}
