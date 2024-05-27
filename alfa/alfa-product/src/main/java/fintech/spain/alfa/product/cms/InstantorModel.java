package fintech.spain.alfa.product.cms;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

@Data
public class InstantorModel {

    private String requestId;
    private LocalDateTime dateTime;
    private String result;
    private InstantorData instantor;
    private ClientData client;

    @Data
    @Accessors(chain = true)
    public static class ClientData {
        private String clientName;
        private String personalCode;
        private String phoneNumber;
        private String email;
        private String address;
    }

    @Data
    @Accessors(chain = true)
    public static class InstantorData {
        private String clientName;
        private String personalCode;
        private List<String> phoneNumber;
        private List<String> email;
        private List<String> address;
        private String bankName;
        private LocalDateTime reportTime;
        private LocalDate periodFrom;
        private LocalDate periodTo;
        private List<Account> accounts = new LinkedList<>();
    }

    @Data
    @Accessors(chain = true)
    public static class Account {
        private String number;
        private String iban;
        private String holder;
        private String type;
        private BigDecimal balance;
        private int transactionCount;
    }

}
