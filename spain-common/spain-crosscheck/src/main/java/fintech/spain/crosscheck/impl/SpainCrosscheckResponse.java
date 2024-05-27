package fintech.spain.crosscheck.impl;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SpainCrosscheckResponse {

    private boolean error;
    private String errorMessage;
    private String responseBody;
    private int responseStatusCode;

    private Attributes attributes;

    @Data
    @Accessors(chain = true)
    public static class Attributes {
        private boolean found;
        private int openLoans;
        private int maxDpd;
        private boolean blacklisted;
        private String clientNumber;
        private boolean repeatedClient;
        private long paidInvoices;
        private long unpaidInvoices;
        private boolean activeRequest;
        private String activeRequestStatus;
    }
}
